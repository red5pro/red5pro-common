package com.red5pro.interstitial.api;

import java.io.IOException;

import org.red5.server.api.event.IEvent;
import org.red5.server.messaging.IMessageInput;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.VideoData;

public abstract class InterstitialSession implements Comparable<InterstitialSession> {

	public static final int AAC_AUDIO = 'A' | ('A' << 8) | ('A' << 16) | (' ' << 24);

	public static final int SPEX_AUDIO = 'S' | ('P' << 8) | ('E' << 16) | ('X' << 24);

	/**
	 * Arbitrary
	 */
	public long id;

	/**
	 * Resource
	 */
	public String uri;

	/**
	 * Packet source.
	 */
	public IMessageInput io;

	/**
	 * When to stop insertion.
	 */
	public InterstitialDurationControl sessionControl;

	public int audioChannels;

	public int audioSampleRate;

	public int width;

	public int height;

	/**
	 * Audio Codec
	 */
	public int codec;

	private boolean isInterstitialAudio;

	private boolean isInterstitialVideo;

	public InterstitialSession(boolean isInterstitialAudio, boolean isInterstitialVideo) {
		this.isInterstitialAudio = isInterstitialAudio;
		this.isInterstitialVideo = isInterstitialVideo;
	}

	/**
	 * Include the metada of the insert. Default is false. Metadata from inserted
	 * source material will be dropped.
	 */
	public boolean includeMetaData;

	public void setAudioParams(int codec, int audioChannels, int audioSampleRate) {
		this.codec = codec;
		this.audioChannels = audioChannels;
		this.audioSampleRate = audioSampleRate;
	}

	public void setVideoParams(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Called when this session is next.
	 */
	public abstract void queue();

	/**
	 * Called at the moment this session is activated.
	 */
	public abstract void open() throws Exception;

	/**
	 * When presented with a timestamp, we dispatch alternate content into the
	 * prostream. This may be one or none packets depending on the timestamp.
	 * 
	 * @param timestamp
	 *            value to compare.
	 * @param output
	 *            dispatchInterstitial method
	 * @throws IOException
	 */
	public abstract void process(long timestamp, IRTMPEvent event, IInterstitialStream output) throws IOException;

	public abstract void dispose();

	/**
	 * This assumes no nulls in our structure, in particular sessionControl.
	 * 
	 * @param arg0
	 * @return
	 */
	@Override
	public int compareTo(InterstitialSession them) {

		if (them == null) {// by the book.
			throw new NullPointerException();
		}

		long ourTime = 0;
		switch (sessionControl.getLifeCycle()) {
			case StreamClock :
				// 0 to infinity.
				ourTime = sessionControl.getStart();
				break;
			case Indefinite :
				// now!
				ourTime = 0;
				break;
			case WallClock :
				ourTime = sessionControl.getStart() - System.currentTimeMillis();
				break;
		}

		long theirTime = 0;
		switch (them.sessionControl.getLifeCycle()) {
			case StreamClock :
				// 0 to infinity.
				theirTime = them.sessionControl.getStart();
				break;
			case Indefinite :
				// now!
				theirTime = 0;
				break;
			case WallClock :
				theirTime = them.sessionControl.getStart() - System.currentTimeMillis();
				break;
		}

		int results = (int) (ourTime - theirTime);
		System.out.println("results " + results);
		if (results == 0) {
			// shazbot! Attempt to avoid throwing scheduling error.
			results = (int) (id - them.id);
		}

		return results;
	}

	public boolean isForwardAudio() {
		return isInterstitialAudio;
	}

	public boolean isForwardVideo() {
		return isInterstitialVideo;
	}

	/**
	 * @param event
	 *            the event to dispatch
	 * @param isOriginal
	 *            is this from the original stream (or else, from the interstitial
	 *            stream)?
	 */
	public void dispatchEvent(IEvent event, boolean isOriginal, IInterstitialStream stream) {
		boolean isAudio = AudioData.class.isAssignableFrom(event.getClass());
		boolean isVideo = VideoData.class.isAssignableFrom(event.getClass());

		boolean isNeither = !isVideo && !isAudio;
		boolean isReallyAudio = (isOriginal ^ isForwardAudio()) && isAudio;
		boolean isReallyVideo = (isOriginal ^ isForwardVideo()) && isVideo;

		// log.info("isNeither: {}, isReallyAudio: {}, isReallyVideo: {}", isNeither,
		// isReallyAudio, isReallyVideo);

		if (isNeither || isReallyAudio || isReallyVideo) {
			stream.dispatchInterstitial(event);
		}
		// else {
		// if (isAudio) {
		// log.warn("don't dispatch interstitial audio");
		// } else if (isVideo) {
		// log.warn("don't dispatch interstitial audio");
		// } else {
		// log.warn("don't dispatch interstitial other");
		// }
		// }
	}
}

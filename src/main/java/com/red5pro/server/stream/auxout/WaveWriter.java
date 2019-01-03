package com.red5pro.server.stream.auxout;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WaveWriter implements AuxOut {

	private static final int Format = ('P') | ('C' << 8) | ('M' << 16) | (' ' << 24);

	private static final int HEADER_LENGTH = 44;

	private static final int RIFF = 1380533830; // 'RIFF'

	private static final long WAVE_FMT = 6287401410857104416l; // 'WAVEfmt '

	private static final int DATA = 1684108385; // 'data'

	private int channels;
	private int rate;
	private int bitsPerSample;
	private int segmentWritten;
	private RandomAccessFile out;
	private int bytesWritten;
	private ExecutorService runner = Executors.newFixedThreadPool(1);
	private Future<?> future;
	private boolean doRun = true;
	private LinkedBlockingQueue<WaveSamples> soundin = new LinkedBlockingQueue<WaveSamples>();
	private LinkedBlockingQueue<WaveSamples> soundout = new LinkedBlockingQueue<WaveSamples>();

	public WaveWriter(String name, int chan, int rate, int bitsPerSample) {
		this.channels = chan;
		this.rate = rate;
		this.bitsPerSample = bitsPerSample;
		try {
			out = new RandomAccessFile(name, "rw");
			out.write(new byte[HEADER_LENGTH]);
			future = runner.submit(this);
		} catch (IOException e) {
		}
	}

	public void packetReceived(WaveSamples samples) {
		soundin.add(samples);
	}

	@Override
	public void run() {
		while (doRun) {
			try {
				WaveSamples data = soundin.poll(250, TimeUnit.MILLISECONDS);
				if (data != null) {
					writeSamples(data);
				}
			} catch (InterruptedException e) {
			}
		}

		doFlush();

		try {
			close();
		} catch (IOException e) {
		}
	}

	private void writeSamples(WaveSamples data) {

		int sampleCount = (data.samples.length / (bitsPerSample / 8)) / data.channels;
		segmentWritten += sampleCount;
		soundout.add(data);
		if (segmentWritten >= rate) {// 1 second worth
			doFlush();
			segmentWritten = 0;
		}
	}

	private void doFlush() {

		WaveSamples samples;
		while ((samples = soundout.poll()) != null) {
			byte[] data = samples.samples;
			// convert to little endian
			byte tmp;
			for (int i = 0; i < data.length; i += 2) {
				tmp = data[i + 1];
				data[i + 1] = data[i];
				data[i] = tmp;
			}
			try {
				bytesWritten += data.length;
				out.write(data, 0, data.length);
			} catch (IOException e) {
			}
		}
	}

	public void stop() {
		this.doRun = false;

		try {

			future.get(500, TimeUnit.MILLISECONDS);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() throws IOException {
		out.seek(0);
		final int bytesPerSec = (bitsPerSample + 7) / 8;
		out.writeInt(RIFF); // wave label
		out.writeInt(Integer.reverseBytes(bytesWritten + 36)); // length in bytes without header
		out.writeLong(WAVE_FMT);
		out.writeInt(Integer.reverseBytes(16)); // length of pcm format declaration area
		out.writeShort(Short.reverseBytes((short) 1)); // is PCM
		out.writeShort(Short.reverseBytes((short) channels)); // number of channels
		out.writeInt(Integer.reverseBytes(rate)); // sample rate
		out.writeInt(Integer.reverseBytes(rate * channels * bytesPerSec)); // bytes per second
		out.writeShort(Short.reverseBytes((short) (channels * bytesPerSec))); // bytes per sample time
		out.writeShort(Short.reverseBytes((short) bitsPerSample)); // bits per sample
		out.writeInt(DATA); // data section label
		out.writeInt(Integer.reverseBytes(bytesWritten)); // length of raw pcm data in bytes
		out.close();
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getFormat() {
		return Format;
	}

	@Override
	public int getBitsPerSample() {
		// TODO Auto-generated method stub
		return bitsPerSample;
	}
}

package com.red5pro.media.sdp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.red5pro.media.sdp.model.AttributeField;
import com.red5pro.media.sdp.model.AttributeKey;
import com.red5pro.media.sdp.model.BandwidthField;
import com.red5pro.media.sdp.model.MediaField;
import com.red5pro.media.sdp.model.OriginField;
import com.red5pro.media.sdp.model.SDPMediaType;
import com.red5pro.media.sdp.model.SessionField;
import com.red5pro.media.sdp.model.TimingField;
import com.red5pro.util.IdGenerator;

/**
 * A session description, based on RFC4566
 * {@link https://tools.ietf.org/html/rfc4566}.
 * 
 * @author Paul Gregoire
 */
public class SessionDescription {

	private final static String version = "v=0\n";

	// media id constant for audio
	public final static String AUDIO = "audio";

	// media id constant for video
	public final static String VIDEO = "video";

	// generate an id for RTCMediaStream Id / msid/mslabel
	private String msid;

	private OriginField origin;

	private SessionField session = new SessionField();

	private TimingField timing = new TimingField();

	// session level attributes
	private AttributeField[] attributes;

	// session level bandwidth
	private BandwidthField bandwidth;

	private MediaField[] mediaDescriptions;

	// audio media description counter
	private int audio;

	// video media description counter
	private int video;

	// whether or not to mux rtcp
	private boolean rtcpMux = true;

	// whether or not to bundle
	private boolean bundle;

	// whether or not to use unified or plan-b (WebRTC 1.0 prefers Unified)
	private boolean unified;

	// where the sdp came from
	private SDPUserAgent userAgent = SDPUserAgent.undefined;

	// SDP tracks
	private Set<SDPTrack> tracks;

	// Stream metadata
	private Map<Object, Object> metadata;

	// Used for payload id generation (96 to 127)
	private int nextDynamicId = 96;

	public CryptographyParam srtpInfo;

	public SessionDescription() {
	}

	public SessionDescription(SDPUserAgent userAgent) {
		this.userAgent = userAgent;
	}

	public void free() {
		attributes = null;
		bandwidth = null;
		mediaDescriptions = null;
		if (metadata != null) {
			metadata.clear();
			metadata = null;
		}
		origin = null;
		session = null;
		srtpInfo = null;
		timing = null;
		if (tracks != null) {
			tracks.clear();
			tracks = null;
		}
		userAgent = null;
	}

	public int getDynamicPayloadId() {
		if (nextDynamicId == 128) {
			throw new Error("too many dynamic ID requested from the SDP session description object");
		}
		return nextDynamicId++;
	}

	/**
	 * Adds an SDPTrack.
	 * 
	 * @param track
	 */
	public boolean addTrack(SDPTrack track) {
		if (tracks == null) {
			tracks = new CopyOnWriteArraySet<SDPTrack>();
		}
		return tracks.add(track);
	}

	/**
	 * Returns the SDPTracks if any exist.
	 * 
	 * @return collection of SDPTrack
	 */
	public Set<SDPTrack> getTracks() {
		return tracks;
	}

	/**
	 * Returns a media description for a given type.
	 * 
	 * @param type
	 *            MediaType
	 * @return media description matching type or null if not found
	 */
	public MediaField getMediaDescription(SDPMediaType type) {
		if (mediaDescriptions != null && mediaDescriptions.length > 0) {
			for (MediaField media : mediaDescriptions) {
				if (media.getMediaType().equals(type)) {
					return media;
				}
			}
		}
		return null;
	}

	/**
	 * Returns all the media descriptions for a given type.
	 * 
	 * @param type
	 *            MediaType
	 * @return list of media descriptions matching type or empty list if none match
	 */
	public List<MediaField> getMediaDescriptions(SDPMediaType type) {
		List<MediaField> medias = Collections.emptyList();
		if (mediaDescriptions != null && mediaDescriptions.length > 0) {
			for (MediaField media : mediaDescriptions) {
				if (media.getMediaType().equals(type)) {
					if (medias.isEmpty()) {
						medias = new ArrayList<>();
					}
					medias.add(media);
				}
			}
		}
		return medias;
	}

	public OriginField getOrigin() {
		return origin;
	}

	public void setOrigin(OriginField origin) {
		this.origin = origin;
	}

	public SessionField getSession() {
		return session;
	}

	public void setSession(SessionField session) {
		this.session = session;
	}

	public TimingField getTiming() {
		return timing;
	}

	public void setTiming(TimingField timing) {
		this.timing = timing;
	}

	public void addAttributeField(AttributeField attr) {
		if (attributes == null) {
			attributes = new AttributeField[1];
			attributes[0] = attr;
		} else {
			int index = attributes.length;
			AttributeField[] temp = Arrays.copyOf(attributes, attributes.length + 1);
			temp[index] = attr;
			setAttributes(temp);
		}
	}

	/**
	 * Lookup attribute by its key.
	 * 
	 * @param key
	 * @return attribute if found and null otherwise
	 */
	public AttributeField getAttribute(AttributeKey key) {
		if (attributes != null && attributes.length > 0) {
			for (AttributeField attr : attributes) {
				if (attr.getAttribute().equals(key)) {
					return attr;
				}
			}
		}
		return null;
	}

	/**
	 * Remove an attribute by its key. Be wary as this removes all attributes
	 * matching the given key.
	 */
	public void remove(AttributeKey key) {
		if (attributes != null && attributes.length > 0) {
			// what will become the new array
			AttributeField[] temp = new AttributeField[attributes.length - 1];
			int t = 0;
			for (int a = 0; a < attributes.length; a++) {
				if (!attributes[a].getAttribute().equals(key)) {
					// move it to the new array
					temp[t++] = attributes[a];
				}
			}
			setAttributes(temp);
		}
	}

	/**
	 * Remove an attribute.
	 */
	public void remove(AttributeField attr) {
		if (attributes != null && attributes.length > 0) {
			// what will become the new array
			AttributeField[] temp = new AttributeField[attributes.length - 1];
			int t = 0;
			for (int a = 0; a < attributes.length; a++) {
				if (!attributes[a].equals(attr)) {
					// move it to the new array
					temp[t++] = attributes[a];
				}
			}
			setAttributes(temp);
		}
	}

	public AttributeField[] getAttributes() {
		return attributes;
	}

	public void setAttributes(AttributeField[] attributes) {
		this.attributes = attributes;
	}

	public BandwidthField getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(BandwidthField bandwidth) {
		this.bandwidth = bandwidth;
	}

	/**
	 * Add a media field (description).
	 * 
	 * @param mediaField
	 */
	@SuppressWarnings("incomplete-switch")
	public void addMediaDescription(MediaField mediaField) {
		if (mediaDescriptions == null) {
			mediaDescriptions = new MediaField[1];
			mediaDescriptions[0] = mediaField;
		} else {
			int index = mediaDescriptions.length;
			MediaField[] temp = Arrays.copyOf(mediaDescriptions, mediaDescriptions.length + 1);
			temp[index] = mediaField;
			setMediaDescriptions(temp);
		}
		SDPMediaType type = mediaField.getMediaType();
		switch (type) {
			case audio :
				incrementAudio();
				break;
			case video :
				incrementVideo();
				break;
		}
	}

	/**
	 * Remove a media field (description).
	 * 
	 * @param mediaField
	 */
	public void remove(MediaField mediaField) {
		if (mediaDescriptions != null) {
			// what will become the new array
			MediaField[] temp = new MediaField[Math.max(1, (mediaDescriptions.length - 1))];
			int t = 0;
			for (int m = 0; m < mediaDescriptions.length; m++) {
				if (!mediaDescriptions[m].equals(mediaField)) {
					// move it to the new array
					temp[t++] = mediaDescriptions[m];
				} else {
					// skip it
					if (SDPMediaType.audio.equals(mediaField.getMediaType())) {
						decrementAudio();
					} else if (SDPMediaType.video.equals(mediaField.getMediaType())) {
						decrementVideo();
					}
				}
			}
			setMediaDescriptions(temp);
		}
	}

	public MediaField[] getMediaDescriptions() {
		return mediaDescriptions;
	}

	public void setMediaDescriptions(MediaField[] mediaDescriptions) {
		this.mediaDescriptions = mediaDescriptions;
	}

	/**
	 * Set metadata and create attributes for each entry.
	 * 
	 * @param metadata
	 */
	public void setMetadata(Map<Object, Object> metadata) {
		this.metadata = metadata;
		// create attributes for the metadata entries
		StringBuilder metaSb = new StringBuilder();
		metadata.forEach((key, value) -> {
			metaSb.append(String.format("%s=%s;", key.toString(), value.toString()));
		});
		addAttributeField(new AttributeField(AttributeKey.metadata, metaSb.toString()));
	}

	/**
	 * Returns metadata if it exists.
	 * 
	 * @return metadata
	 */
	public Map<Object, Object> getMetadata() {
		return metadata;
	}

	public boolean hasMetadata() {
		return metadata != null;
	}

	/**
	 * Returns a audio track if it exists.
	 * 
	 * @return audio track or null if none exist
	 */
	public SDPTrack getAudioTrack() {
		if (tracks != null) {
			Optional<SDPTrack> track = tracks.stream().filter(t -> t.isAudio()).findFirst();
			if (track.isPresent()) {
				return track.get();
			}
		}
		return null;
	}

	/**
	 * Returns a video track if it exists.
	 * 
	 * @return video track or null if none exist
	 */
	public SDPTrack getVideoTrack() {
		if (tracks != null) {
			Optional<SDPTrack> track = tracks.stream().filter(t -> t.isVideo()).findFirst();
			if (track.isPresent()) {
				return track.get();
			}
		}
		return null;
	}

	/**
	 * Returns an application track if it exists.
	 * 
	 * @return application track or null if none exist
	 */
	public SDPTrack getApplicationTrack() {
		if (tracks != null) {
			Optional<SDPTrack> track = tracks.stream().filter(t -> t.isApplication()).findFirst();
			if (track.isPresent()) {
				return track.get();
			}
		}
		return null;
	}

	/**
	 * Add SRTP cryptography configuration.
	 *
	 * @param crypto
	 *            cryptography parameters
	 */
	public void addCrypto(String crypto) {
		// a=crypto:1 AES_CM_128_HMAC_SHA1_32 inline:<hash>|SHA1
		addCrypto(crypto, true);
	}

	/**
	 * Add SRTP cryptography configuration.
	 *
	 * @param crypto
	 *            cryptography parameters
	 * @param addAttributeField
	 *            to add the field or not
	 */
	public void addCrypto(String crypto, boolean addAttributeField) {
		// a=crypto:1 AES_CM_128_HMAC_SHA1_32 inline:<hash>|SHA1
		CryptographyParam cryptoParam = new CryptographyParam();
		if (cryptoParam.parseSDPLine(crypto)) {
			if (addAttributeField) {
				addAttributeField(new AttributeField(AttributeKey.crypto, cryptoParam.toString()));
			}
			srtpInfo = cryptoParam;
		}
	}

	public static String getVersion() {
		return version;
	}

	public String getMsid() {
		return msid;
	}

	public void setMsid(String msid) {
		this.msid = msid;
	}

	public int getAudio() {
		return audio;
	}

	public void incrementAudio() {
		audio += 1;
	}

	public void decrementAudio() {
		audio -= 1;
	}

	public boolean hasAudio() {
		return audio > 0;
	}

	public int getVideo() {
		return video;
	}

	public void incrementVideo() {
		video += 1;
	}

	public void decrementVideo() {
		video -= 1;
	}

	public boolean hasVideo() {
		return video > 0;
	}

	public boolean isRtcpMux() {
		return rtcpMux;
	}

	public void setRtcpMux(boolean rtcpMux) {
		this.rtcpMux = rtcpMux;
	}

	public boolean isBundle() {
		return bundle;
	}

	public void setBundle(boolean bundle) {
		this.bundle = bundle;
	}

	public boolean isUnified() {
		return unified;
	}

	public void setUnified(boolean unified) {
		this.unified = unified;
	}

	public void setUA(SDPUserAgent userAgent) {
		this.userAgent = userAgent;
	}

	public boolean isAndroid() {
		return userAgent == SDPUserAgent.android_chrome || userAgent == SDPUserAgent.android_firefox;
	}

	public boolean isChrome() {
		return userAgent == SDPUserAgent.chrome || userAgent == SDPUserAgent.android_chrome;
	}

	public boolean isFirefox() {
		return userAgent == SDPUserAgent.mozilla || userAgent == SDPUserAgent.firefox
				|| userAgent == SDPUserAgent.android_firefox;
	}

	public boolean isEdge() {
		return userAgent == SDPUserAgent.edge;
	}

	public boolean isSafari() {
		return userAgent == SDPUserAgent.safari;
	}

	public boolean isRed5ProSDK() {
		return userAgent == SDPUserAgent.red5pro;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(version);
		// ensure there's no "null" origin
		if (origin == null) {
			// use the "name" from the session, default to unknown / undefined == r5p
			String name = session != null ? session.getName() : "r5p";
			// ensure there's an origin
			origin = new OriginField(name, IdGenerator.generateNumericStringId(13), 1L, "0.0.0.0");
		}
		sb.append(origin);
		sb.append(session);
		sb.append(timing);
		// skip webrtc stuff for pro sdk UA
		if (!isRed5ProSDK()) {
			// unified or plan-b support signaling is via attribute as well and also not
			// stored in the collection
			if (!unified) {
				// signaling plan-b
				if (msid == null) {
					// mslabel value can be supplied instead of '*' but asterisk seems most common
					sb.append("a=msid-semantic:");
					if (isChrome()) {
						// chrome has a freaking space before the WMS
						sb.append(' ');
					}
					sb.append("WMS *\n");
				} else {
					sb.append("a=msid-semantic:WMS ");
					sb.append(msid);
					sb.append('\n');
				}
			}
		}
		// bundle is a group attribute, but we don't store it in the attr collection
		if (bundle) {
			if (mediaDescriptions != null) {
				sb.append("a=group:BUNDLE ");
				for (MediaField media : mediaDescriptions) {
					sb.append(media.getMediaId());
					sb.append(' ');
				}
				// trim-off trailing space
				sb.deleteCharAt(sb.lastIndexOf(" "));
				sb.append('\n');
			}
		}
		if (attributes != null) {
			for (AttributeField attribute : attributes) {
				AttributeKey key = attribute.getAttribute();
				// prevent adding these attributes twice to the string
				if (!AttributeKey.msidsemantic.equals(key) && !AttributeKey.group.equals(key)) {
					sb.append(attribute);
				}
			}
		}
		// next line, bandwidth
		if (bandwidth != null) {
			sb.append(bandwidth);
		}
		if (mediaDescriptions != null) {
			for (MediaField media : mediaDescriptions) {
				sb.append(media);
			}
		}
		// finalize the sdp content
		String sdp = sb.toString();
		// if the UA is red5pro sdk, swap lf for crlf (XXX not sure if this is required
		// by our mobile sdk or not, doing for safety)
		if (userAgent.equals(SDPUserAgent.red5pro)) {
			return sdp.replaceAll("\n", "\r\n");
		}
		return sdp;
	}

}

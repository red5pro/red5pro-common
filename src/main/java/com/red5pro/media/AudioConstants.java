package com.red5pro.media;

/**
 * Central location for Audio constants.
 */
public class AudioConstants {
	/**
	 * The <tt>/rtp</tt> constant. Introduced in order to achieve consistency in the
	 * casing of the String.
	 */
	public static final String _RTP = "/rtp";

	public static final String LINEAR = "LINEAR";

	public static final String ULAW = "ULAW";

	public static final String ALAW = "alaw";

	public static final String MPEGLAYER3 = "mpeglayer3";

	public static final String MPEG = "mpegaudio";

	public static final String MPEG_RTP = "mpegaudio" + _RTP;

	public static final String ILBC_RTP = "ilbc" + _RTP;

	/**
	 * The ULAW/rtp constant.
	 */
	public static final String ULAW_RTP = "ULAW" + _RTP;

	/**
	 * The ALAW/rtp constant.
	 */
	public static final String ALAW_RTP = "ALAW" + _RTP;

	/**
	 * The opus constant.
	 */
	public static final String OPUS = "opus";

	/**
	 * The opus/rtp constant.
	 */
	public static final String OPUS_RTP = OPUS + _RTP;

	/**
	 * The SPEEX constant.
	 */
	public static final String SPEEX = "speex";

	/**
	 * The SPEEX/RTP constant.
	 */
	public static final String SPEEX_RTP = SPEEX + _RTP;

	public static final String AAC = "aac";

	public static final String AAC_RAW = AAC + "/raw";

	public static final String AAC_RTMP = AAC + "/rtmp";

	public static final String AAC_MODE_FMTP = "mode";

	public static final String AAC_PROFILE_LEVEL_ID_FMTP = "profile-level-id";

	/**
	 * The list of well-known audio sample rates.
	 */
	public static final double[] AUDIO_SAMPLE_RATES = {48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000};

}

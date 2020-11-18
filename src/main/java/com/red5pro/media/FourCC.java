package com.red5pro.media;

import java.util.EnumSet;

/**
 * FourCC representation for media, protocol, or transport.
 * 
 * @author Andy Shaules
 * @author Paul Gregoire
 *
 */
public enum FourCC {

	/**
	 * Undefined / unknown / not set
	 */
	UNDEFINED('U', 'N', 'K', '0'),

	/**
	 * 541934416 // assumes PCMU
	 */
	PCM('P', 'C', 'M', ' '),

	/**
	 * ?
	 */
	PCMA('P', 'C', 'M', 'A'),

	/**
	 * ?
	 */
	RGBA('R', 'G', 'B', 'A'),

	/**
	 * 808596553
	 */
	I420('I', '4', '2', '0'),

	/**
	 * 1398100047
	 */
	OPUS('O', 'P', 'U', 'S'),

	/**
	 * 541278529
	 */
	AAC('A', 'A', 'C', ' '),

	/**
	 * 540561494
	 */
	VP8('V', 'P', '8', ' '),

	/**
	 * 540627030
	 */
	VP9('V', 'P', '9', ' '),

	/**
	 * 875967048
	 */
	H264('H', '2', '6', '4'),

	/**
	 * ?
	 */
	HEVC('H', 'E', 'V', 'C'),

	/**
	 * ?
	 */
	AV1('A', 'V', '1', ' '),

	/**
	 * 1096041805
	 */
	METADATA('M', 'E', 'T', 'A'),

	/**
	 * 541478209
	 */
	AMF('A', 'M', 'F', ' '),

	/**
	 * ?
	 */
	RTMP('R', 'T', 'M', 'P'),

	/**
	 * ?
	 */
	RTC('R', 'T', 'C', ' '),

	/**
	 * 542135378
	 */
	RTP('R', 'T', 'P', ' '),

	/**
	 * ?
	 */
	SRTP('R', 'T', 'P', 'S'),

	/**
	 * ?
	 */
	RTSP('R', 'T', 'S', 'P'),

	/**
	 * ?
	 */
	SRT('S', 'R', 'T', ' '),

	/**
	 * 1398034509
	 */
	MPTS('M', 'P', 'T', 'S'),

	/**
	 * ?
	 */
	XML('X', 'M', 'L', ' '),

	/**
	 * 1313821514
	 */
	JSON('J', 'S', 'O', 'N'),

	/**
	 * ?
	 */
	JSON_RPC('J', 'R', 'P', 'C');

	// audio fourcc identifiers
	private static EnumSet<FourCC> audios = EnumSet.of(FourCC.AAC, FourCC.OPUS, FourCC.PCM, FourCC.PCMA);

	// video fourcc identifiers
	private static EnumSet<FourCC> videos = EnumSet.of(FourCC.AV1, FourCC.H264, FourCC.HEVC, FourCC.I420, FourCC.RGBA,
			FourCC.VP8, FourCC.VP9);

	final int code;

	FourCC(char a, char b, char c, char d) {
		this.code = fourCC(a, b, c, d);
	}

	/**
	 * Returns the integer value of the FourCC code.
	 * 
	 * @return fourCC code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Returns a FourCC enum based on the supplied characters.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return FourCC
	 */
	public static FourCC getFourCC(char a, char b, char c, char d) {
		int target = FourCC.fourCC(a, b, c, d);
		for (FourCC fcc : FourCC.values()) {
			if (fcc.code == target) {
				return fcc;
			}
		}
		return null;
	}

	/**
	 * @param a
	 *            char
	 * @param b
	 *            char
	 * @param c
	 *            char
	 * @param d
	 *            char
	 * @return integer fourCC
	 */
	public static int fourCC(char a, char b, char c, char d) {
		return (a) | (b << 8) | (c << 16) | (d << 24);
	}

	/**
	 * Returns true if the target FourCC identifier is of audio type.
	 * 
	 * @param target
	 * @return true if audio and false otherwise
	 */
	public static boolean isAudio(FourCC target) {
		return audios.contains(target);
	}

	/**
	 * Returns true if the target FourCC identifier is of video type.
	 * 
	 * @param target
	 * @return true if video and false otherwise
	 */
	public static boolean isVideo(FourCC target) {
		return videos.contains(target);
	}

}

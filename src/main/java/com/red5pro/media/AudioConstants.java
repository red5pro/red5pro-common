//
// Copyright © 2015 Infrared5, Inc. All rights reserved.
//
// The accompanying code comprising examples for use solely in conjunction with Red5 Pro (the "Example Code")
// is  licensed  to  you  by  Infrared5  Inc.  in  consideration  of  your  agreement  to  the  following
// license terms  and  conditions.  Access,  use,  modification,  or  redistribution  of  the  accompanying
// code  constitutes your acceptance of the following license terms and conditions.
//
// Permission is hereby granted, free of charge, to you to use the Example Code and associated documentation
// files (collectively, the "Software") without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The Software shall be used solely in conjunction with Red5 Pro. Red5 Pro is licensed under a separate end
// user  license  agreement  (the  "EULA"),  which  must  be  executed  with  Infrared5,  Inc.
// An  example  of  the EULA can be found on our website at: https://account.red5pro.com/assets/LICENSE.txt.
//
// The above copyright notice and this license shall be included in all copies or portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,  INCLUDING  BUT
// NOT  LIMITED  TO  THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR  A  PARTICULAR  PURPOSE  AND
// NONINFRINGEMENT.   IN  NO  EVENT  SHALL INFRARED5, INC. BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM,  OUT  OF  OR  IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
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

	public static final String OPUS_RTMP = OPUS + "/rtmp";

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

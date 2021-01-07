//
// Copyright © 2020 Infrared5, Inc. All rights reserved.
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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central location for video constants.
 */
public class VideoConstants {

    private static final Logger log = LoggerFactory.getLogger(VideoConstants.class);

    /**
     * The <tt>/rtp</tt> constant. Introduced in order to achieve consistency in the
     * casing of the String.
     */
    public static final String _RTP = "/rtp";

    /**
     * The H264 constant.
     */
    public static final String H264 = "h264";

    /**
     * The H264/rtp constant.
     */
    public static final String H264_RTP = H264 + _RTP;

    /**
     * The H264/rtmp constant.
     */
    public static final String H264_RTMP = H264 + "/rtmp";

    /**
     * The name of the RED RTP format (RFC2198)
     */
    public static final String RED = "red";

    /**
     * The name of the ulpfec RTP format (RFC5109)
     */
    public static final String ULPFEC = "ulpfec";

    /**
     * The VP8 constant
     */
    public static final String VP8 = "VP8";

    /**
     * The VP8/rtp constant.
     */
    public static final String VP8_RTP = VP8 + _RTP;

    /**
     * The VP8/rtmp constant.
     */
    public static final String VP8_RTMP = VP8 + "/rtmp";

    /**
     * The VP9 constant
     */
    public static final String VP9 = "VP9";

    /**
     * The VP9/rtp constant.
     */
    public static final String VP9_RTP = VP9 + _RTP;

    /**
     * The VP9/rtmp constant.
     */
    public static final String VP9_RTMP = VP9 + "/rtmp";

    /** MPEG format. */
    public static final String MPEG = "mpeg";

    /** MPEG RTP format. */
    public static final String MPEG_RTP = "mpeg/rtp";

    /**
     * The default value of the <tt>clockRate</tt> property for video.
     */
    public static final double DEFAULT_CLOCK_RATE = 90000d;

    /**
     * Bitrate key.
     */
    public static final String BITRATE = "bitrate";

    /**
     * Frames-per-second key.
     */
    public static final String FRAMES_PER_SECOND = "fps";

    /**
     * The name of the format parameter which specifies the packetization mode for
     * an H.264 RTP payload.
     */
    public static final String H264_PACKETIZATION_MODE_FMTP = "packetization-mode";

    public static final String H264_SPROP_PARAMETER_SETS_FMTP = "sprop-parameter-sets";

    public static final String H264_PROFILE_LEVEL_ID_FMTP = "profile-level-id";

    /**
     * Determines whether two sets of format parameters match in the context of a
     * specific encoding.
     *
     * @param encoding
     *            the encoding (name) related to the two sets of format parameters
     *            to be matched
     * @param fmtps1
     *            the first set of format parameters which is to be matched against
     *            <tt>fmtps2</tt>
     * @param fmtps2
     *            the second set of format parameters which is to be matched against
     *            <tt>fmtps1</tt>
     * @return true if the two sets of format parameters match in the context of the
     *         encoding; otherwise, false
     */
    public static boolean formatParametersMatch(String encoding, Map<String, String> fmtps1, Map<String, String> fmtps2) {
        log.trace("formatParametersMatch {} {} {}", encoding, fmtps1, fmtps2);
        boolean matches = true;
        // RFC 3984 "RTP Payload Format for H.264 Video" says that "[w]hen the value of
        // packetization-mode is equal to 0 or packetization-mode is
        // not present, the single NAL mode, as defined in section 6.2 of RFC 3984, MUST
        // be used."
        if (H264.equalsIgnoreCase(encoding) || H264_RTP.equalsIgnoreCase(encoding)) {
            String pm1 = (fmtps1 == null ? "0" : fmtps1.get(H264_PACKETIZATION_MODE_FMTP));
            String pm2 = (fmtps2 == null ? "0" : fmtps2.get(H264_PACKETIZATION_MODE_FMTP));
            matches = Integer.valueOf(pm1 == null ? "0" : pm1) == Integer.valueOf(pm2 == null ? "0" : pm2);
            log.trace("matches? {} pm1: {} pm2: {}", matches, pm1, pm2);
        }
        return matches;
    }

    /**
     * Determines whether a specific set of format parameters is equal to another
     * set of format parameters in the sense that they define an equal number of
     * parameters and assign them equal values. Since the values are
     * <tt>String</tt>s, presumes that a value of <tt>null</tt> is equal to the
     * empty <tt>String</tt>.
     * <p>
     * The two <tt>Map</tt> instances of format parameters to be checked for
     * equality are presumed to be modifiable in the sense that if the lack of a
     * format parameter in a given <tt>Map</tt> is equivalent to it having a
     * specific value, an association of the format parameter to the value in
     * question may be added to or removed from the respective <tt>Map</tt> instance
     * for the purposes of determining equality.
     * </p>
     *
     * @param encoding
     *            the encoding (name) related to the two sets of format parameters
     *            to be tested for equality
     * @param fmtps1
     *            the first set of format parameters to be tested for equality
     * @param fmtps2
     *            the second set of format parameters to be tested for equality
     * @return <tt>true</tt> if the specified sets of format parameters are equal;
     *         <tt>false</tt>, otherwise
     */
    public static boolean formatParametersAreEqual(String encoding, Map<String, String> fmtps1, Map<String, String> fmtps2) {
        /*
         * RFC 3984 "RTP Payload Format for H.264 Video" says that "[w]hen the value of
         * packetization-mode is equal to 0 or packetization-mode is not present, the
         * single NAL mode, as defined in section 6.2, MUST be used."
         */
        if (H264.equalsIgnoreCase(encoding) || H264_RTP.equalsIgnoreCase(encoding)) {
            String packetizationMode = H264_PACKETIZATION_MODE_FMTP;
            String pm1 = null;
            String pm2 = null;
            if (fmtps1 != null) {
                pm1 = fmtps1.remove(packetizationMode);
            }
            if (fmtps2 != null) {
                pm2 = fmtps2.remove(packetizationMode);
            }
            if (pm1 == null) {
                pm1 = "0";
            }
            if (pm2 == null) {
                pm2 = "0";
            }
            if (!pm1.equals(pm2)) {
                return false;
            }
        }
        return FormatUtils.formatParametersAreEqual(encoding, fmtps1, fmtps2);
    }

}

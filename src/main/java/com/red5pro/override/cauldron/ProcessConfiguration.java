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
/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.override.cauldron;

/**
 * This is the native processor core configuration model. <br>
 * This configuration affects what type of video packets are passed to the
 * process module, and what kind of data it will return, if any. <br>
 * 
 * @author Andy Shaules
 */
public class ProcessConfiguration {
	/**
	 * Processor basic type
	 *
	 */
	public static enum ProcessType {
		/**
		 * no native processing.
		 */
		NONE,
		/**
		 * Pass packet data as-is without decoding. Data is H264 Annex-B
		 */
		PASS_THROUGH,
		/**
		 * Pass decoded data. Data is raw image bytes of ProcessVideoFormat.
		 */
		DECODE,
		/**
		 * Re-encode processed data. Data is re-encoded and replaces original frame
		 * data.
		 */
		ENCODE
	}

	/**
	 * Processor blocking settings.
	 *
	 */
	public static enum ProcessTiming {
		/**
		 * Live stream does not wait for processed data to return. Use passing through
		 * annex-b data or for grabbing samples from raw image bytes.
		 */
		NO_WAIT,
		/**
		 * Live stream waits for encoded processed data return. This adds latency from
		 * decoding, editing, and re-encoding the video frame.
		 */
		WAIT
	}

	/**
	 * Processor return type
	 *
	 */
	public static enum ProcessReturn {
		/**
		 * Process returns nothing.
		 */
		NONE,
		/**
		 * Processor returns images bytes which are re-encoded.
		 */
		IMAGE,
		/**
		 * Processor returns script data tage.
		 */
		DATA
	}

	/**
	 * Decoded image format.
	 */
	public static enum ProcessVideoFormat {
		/**
		 * Raw image byte format, 12 bit, packed.
		 */
		YV420P,
		/**
		 * Raw image byte format, 24 bit, packed.
		 */
		BGR
	}

	/**
	 * max frames per second of encoder.
	 */
	public int processFramerate = 15;

	/**
	 * Max Bits per second of encoder
	 */
	public int processBitrate = 500000;

	/**
	 * ProcessType ordinal
	 */
	public int processType = ProcessType.ENCODE.ordinal();

	/**
	 * ProcessTiming ordinal
	 */
	public int processTiming = ProcessTiming.WAIT.ordinal();

	/**
	 * ProcessVideoFormat ordinal
	 */
	public int processFormat = ProcessVideoFormat.YV420P.ordinal();

	/**
	 * ProcessReturn ordinal
	 */
	public int processReturnType = ProcessReturn.IMAGE.ordinal();
}

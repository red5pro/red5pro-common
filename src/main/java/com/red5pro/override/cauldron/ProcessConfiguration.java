/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.override.cauldron;

/**
 * This is the native processor core configuration model.
 * <p />
 * This configuration affects what type of video packets are passed to the process module, and what kind of data it will return, if any.
 * @author Andy Shaules
 *
 */
public class ProcessConfiguration {
	/**
	 * Processor basic type
	 *
	 */
	public static enum ProcessType{
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
		 * Re-encode processed data. Data is re-encoded and replaces original frame data.
		 */
		ENCODE
	}	
	/**
	 * Processor blocking settings.
	 *
	 */
	public static enum ProcessTiming{
		/**
		 * Live stream does not wait for processed data to return. Use passing through annex-b data or for grabbing samples from raw image bytes.
		 */
		NO_WAIT,
		/**
		 * Live stream waits for encoded processed data return. This adds latency from decoding, editing, and re-encoding the video frame. 
		 */
		WAIT
	}	
	/**
	 * Processor return type
	 *
	 */
	public static enum ProcessReturn{
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
	public static enum ProcessVideoFormat{
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

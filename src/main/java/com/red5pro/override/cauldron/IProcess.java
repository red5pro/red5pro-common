/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.override.cauldron;
/**
 * Core native bindings.
 * 
 * @author Andy Shaules
 *
 */
public interface IProcess {
	/**
	 * Load a dynamic process library into the cauldron cache. 
	 * @param path
	 * @return guid
	 */
	public long loadLibrary(String path);
	/**
	 * 
	 * @param framerate
	 * @param bitrate
	 * @param type
	 * @param timing
	 * @param format
	 * @param returnType
	 * @return id of process
	 */
    public long open(int framerate, int bitrate, int type, int timing, int format, int returnType);
    /**
     * 
     * @param guid
     * @return
     */
    public long brew(int guid, long id);
    /**
     * 
     * @param id
     * @return
     */
    public long close(long id);
    /**
     * process video
     * @param time milliseconds
     * @param nals packets
     * @param frameReturn information about available data
     * @param id
     * @return
     */
    public int write(long time, byte[] nals, byte[] frameReturn, long id);
    /**
     * retrieve processed video
     * @param frameReturn timestamps
     * @param packetData nal packets
     * @param id
     * @return
     */
    public int read(byte[] frameReturn,byte[]packetData, long id );
    /**
     * 
     * @param guid module guid
     * @param key module property 
     * @param value module property value
     * @param id
     * @return
     */
    public int apply(long guid,String key, String value, long id);
}

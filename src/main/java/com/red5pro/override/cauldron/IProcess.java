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
     * 
     * @param path library path
     * @return guid
     */
    public long loadLibrary(String path);

    /**
     * Open process.
     * 
     * @param framerate target framerate
     * @param bitrate target bitrate
     * @param type type
     * @param timing timing
     * @param format format
     * @param returnType type to return
     * @return id of process
     */
    public long open(int framerate, int bitrate, int type, int timing, int format, int returnType);

    /**
     * Brew.
     * 
     * @param guid globally unique id
     * @param id instance id
     * @return code
     */
    public long brew(int guid, long id);

    /**
     * Close.
     * 
     * @param id instance id
     * @return code
     */
    public long close(long id);

    /**
     * Process video.
     * 
     * @param time milliseconds
     * @param nals packets
     * @param frameReturn information about available data
     * @param id instance id
     * @return code
     */
    public int write(long time, byte[] nals, byte[] frameReturn, long id);

    /**
     * Retrieve processed video.
     * 
     * @param frameReturn timestamps
     * @param packetData nal packets
     * @param id instance id
     * @return code
     */
    public int read(byte[] frameReturn, byte[] packetData, long id);

    /**
     * Apply.
     * 
     * @param guid module guid
     * @param key module property 
     * @param value module property value
     * @param id instance id
     * @return code
     */
    public int apply(long guid, String key, String value, long id);
}

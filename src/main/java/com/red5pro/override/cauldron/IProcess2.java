package com.red5pro.override.cauldron;
/**
 * Adds audio processing capabilities to user native api.
 * @author Andy
 *
 */
public interface IProcess2 extends IProcess {
    /**
     * 
     * @param fourCC media type, 'PCM ' 'I420', 'RGB '.
     * @param time milliseconds
     * @param data media 
     * @param frameReturn user info
     * @param id pointer
     * @return error code
     */
    public int write2(int fourCC, long time, byte[] nals, byte[] frameReturn, long id);
}

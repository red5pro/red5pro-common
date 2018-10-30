package com.red5pro.override.cauldron;

public interface MIRVPreprocessor {
    /**
     * Create an instance. <br /> Must call close to free the memory.
     * @param outputs number of outputs.
     * @return unique pointer value or zero if error.
     */
    public long open(int outputs);
    /**
     * 
     * @param id pointer
     * @return 1
     */
    public long start(long id);

    /**
     * 
     * @param id pointer
     * @return
     */
    public long close(long id);

    /**
     * Pass nals to processor. Callers provide IMIRVReceiver to get returning frames. 
     * Preprocessor Implementors must call output handler with index, milliseconds, and annex-b nalus.  
     * @param outputHandler implements IMIRVReceiver. 
     * @param time milliseconds
     * @param nals annex b
     * @param id pointer.
     * @return non-zero if frame was processed.
     */
    public int write(Object outputHandler, long time, byte[] nals, long id);

    /**
     * 
     * @param guid module guid
     * @param key module property 
     * @param value module property value
     * @param id pointer.
     * @return
     */
    public int apply(int index, String key, String value, long id);
}

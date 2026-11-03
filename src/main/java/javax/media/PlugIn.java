package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public interface PlugIn {

    /** Human-readable names indexed by process result code. */
    public static final String[] STATUSES = new String[] { "Ok", "Failed", "Buffer not consumed", "Unk3", "Buffer not filled", "Unk5", "Unk6", "Unk7", "Terminated" };

    /** Result code: the buffer was processed successfully. */
    public static int BUFFER_PROCESSED_OK = 0;

    /** Result code: processing the buffer failed. */
    public static int BUFFER_PROCESSED_FAILED = 1;

    /** Result code: the input buffer was not fully consumed. */
    public static int INPUT_BUFFER_NOT_CONSUMED = 2;

    /** Result code: the output buffer was not filled. */
    public static int OUTPUT_BUFFER_NOT_FILLED = 4;

    /** Result code: the plug-in has been terminated. */
    public static int PLUGIN_TERMINATED = 8;

    /**
     * Closes the plug-in and releases its resources.
     */
    public void close();

    /**
     * Returns the name of the plug-in.
     *
     * @return the plug-in name
     */
    public String getName();

    /**
     * Opens the plug-in and acquires the resources it needs.
     *
     * @throws ResourceUnavailableException if a required resource is unavailable
     */
    public void open() throws ResourceUnavailableException;

    /**
     * Resets the plug-in to its initial state.
     */
    public void reset();
}

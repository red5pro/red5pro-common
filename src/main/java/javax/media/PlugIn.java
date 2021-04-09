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

    public static final String[] STATUSES = new String[] { "Ok", "Failed", "Buffer not consumed", "Unk3", "Buffer not filled", "Unk5", "Unk6", "Unk7", "Terminated" };

    public static int BUFFER_PROCESSED_OK = 0;

    public static int BUFFER_PROCESSED_FAILED = 1;

    public static int INPUT_BUFFER_NOT_CONSUMED = 2;

    public static int OUTPUT_BUFFER_NOT_FILLED = 4;

    public static int PLUGIN_TERMINATED = 8;

    public void close();

    public String getName();

    public void open() throws ResourceUnavailableException;

    public void reset();
}

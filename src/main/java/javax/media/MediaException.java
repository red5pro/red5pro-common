package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class MediaException extends Exception {

    private static final long serialVersionUID = -5346295861743653629L;

    /** Creates an exception without a message. */
    public MediaException() {
        super();
    }

    /**
     * Creates an exception with a message.
     *
     * @param message detail message
     */
    public MediaException(String message) {
        super(message);
    }

}

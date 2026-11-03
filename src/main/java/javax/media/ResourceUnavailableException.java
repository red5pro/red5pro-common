package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class ResourceUnavailableException extends MediaException {

    private static final long serialVersionUID = 4457827273708249965L;

    /**
     * Creates an exception with no detail message.
     */
    public ResourceUnavailableException() {
        super();
    }

    /**
     * Creates an exception with a detail message.
     *
     * @param message the detail message
     */
    public ResourceUnavailableException(String message) {
        super(message);
    }

}

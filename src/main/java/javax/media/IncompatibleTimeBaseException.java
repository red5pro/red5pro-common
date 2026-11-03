package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class IncompatibleTimeBaseException extends MediaException {

    private static final long serialVersionUID = -3438317563542022924L;

    /**
     * Creates an exception with no message.
     */
    public IncompatibleTimeBaseException() {
        super();
    }

    /**
     * Creates an exception with the given message.
     *
     * @param message the detail message
     */
    public IncompatibleTimeBaseException(String message) {
        super(message);
    }

}

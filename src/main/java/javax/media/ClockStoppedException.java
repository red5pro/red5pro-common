package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class ClockStoppedException extends MediaException {

    private static final long serialVersionUID = 8320564125045783506L;

    /** Creates an exception without a detail message. */
    public ClockStoppedException() {
        super();
    }

    /** Creates an exception with a detail message.
     * @param message detail message
     */
    public ClockStoppedException(String message) {
        super(message);
    }

}

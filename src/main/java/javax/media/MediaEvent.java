package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class MediaEvent extends java.util.EventObject {

    private static final long serialVersionUID = -1386410533246388334L;

    public MediaEvent(Object source) {
        super(source);
    }
}

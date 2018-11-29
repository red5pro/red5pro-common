package javax.media;

/**
 * Standard JMF class.
 * @see <a href="http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class ControllerEvent extends MediaEvent {

    private static final long serialVersionUID = -6885219910030515482L;

    Controller eventSrc;

    public ControllerEvent(Controller from) {
        super(from);
        eventSrc = from;
    }

    @Override
    public Object getSource() {
        return eventSrc;
    }

    public Controller getSourceController() {
        return eventSrc;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[source=" + eventSrc + "]";
    }
}
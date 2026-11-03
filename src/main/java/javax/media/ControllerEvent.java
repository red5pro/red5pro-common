package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class ControllerEvent extends MediaEvent {

    private static final long serialVersionUID = -6885219910030515482L;

    /**
     * The controller that generated this event.
     */
    Controller eventSrc;

    /**
     * Creates an event for the given controller.
     *
     * @param from controller that generated the event
     */
    public ControllerEvent(Controller from) {
        super(from);
        eventSrc = from;
    }

    @Override
    public Object getSource() {
        return eventSrc;
    }

    /**
     * Returns the controller that generated this event.
     *
     * @return source controller
     */
    public Controller getSourceController() {
        return eventSrc;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[source=" + eventSrc + "]";
    }
}
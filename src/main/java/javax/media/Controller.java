package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public interface Controller extends Clock, Duration {

    /**
     * Latency value returned when the start latency cannot be determined.
     */
    public static final Time LATENCY_UNKNOWN = new Time(Long.MAX_VALUE);

    /**
     * Closes the controller and releases its resources.
     */
    public void close();

    /**
     * Releases resources acquired while realizing or prefetching.
     */
    public void deallocate();

    /**
     * Returns the control with the given class or interface name.
     *
     * @param forName control type name
     * @return matching control, or null if none
     */
    public Control getControl(String forName);

    /**
     * Returns all controls of this controller.
     *
     * @return controls
     */
    public Control[] getControls();

    /**
     * Returns the time needed to start playback.
     *
     * @return start latency, or LATENCY_UNKNOWN
     */
    public Time getStartLatency();

    /**
     * Returns the current state.
     *
     * @return current state
     */
    public State getState();

    /**
     * Returns the state the controller is moving toward.
     *
     * @return target state
     */
    public State getTargetState();

    /**
     * Acquires the resources needed to start and prefills buffers.
     */
    public void prefetch();

    /**
     * Acquires the non-exclusive resources needed to play.
     */
    public void realize();

}

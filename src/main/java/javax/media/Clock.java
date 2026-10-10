package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public interface Clock {

    /** Stop-time sentinel meaning no stop time is set. */
    public static final Time RESET = new Time(Long.MAX_VALUE);

    /** Returns media time in nanoseconds.
     * @return media time in nanoseconds
     */
    public long getMediaNanoseconds();

    /** Returns the current media time.
     * @return current media time
     */
    public Time getMediaTime();

    /** Returns the playback rate.
     * @return playback rate
     */
    public float getRate();

    /** Returns the configured stop time.
     * @return configured stop time
     */
    public Time getStopTime();

    /** Returns the synchronization time.
     * @return synchronization time
     */
    public Time getSyncTime();

    /** Returns the clock's time base.
     * @return clock time base
     */
    public TimeBase getTimeBase();

    /** Maps media time to time-base time.
     * @param t media time to map
     * @return corresponding time-base time
     * @throws ClockStoppedException if the clock is stopped
     */
    public Time mapToTimeBase(Time t) throws ClockStoppedException;

    /** Sets the current media time.
     * @param now new media time
     */
    public void setMediaTime(Time now);

    /** Sets the playback rate.
     * @param factor new playback rate
     * @return effective playback rate
     */
    public float setRate(float factor);

    /** Sets the time at which playback stops.
     * @param stopTime stop time
     */
    public void setStopTime(Time stopTime);

    /** Sets the clock's time base.
     * @param master new time base
     * @throws IncompatibleTimeBaseException if the time base cannot be used
     */
    public void setTimeBase(TimeBase master) throws IncompatibleTimeBaseException;

    /** Stops the clock. */
    public void stop();

    /** Starts the clock at the specified time-base time.
     * @param at start time
     */
    public void syncStart(Time at);
}

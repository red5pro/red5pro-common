package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class Time implements java.io.Serializable {

    private static final long serialVersionUID = -8166306034625732160L;

    /**
     * Number of nanoseconds in one second.
     */
    public static final long ONE_SECOND = 1000000000;

    /**
     * Time value representing an unknown time.
     */
    public static final Time TIME_UNKNOWN = new Time(Long.MAX_VALUE - 1);

    private static final double NANO_TO_SEC = 1E-9;

    /**
     * The time in nanoseconds.
     */
    protected long nanoseconds;

    static { // for Serializable compatibility.
    }

    /**
     * Creates a time from seconds.
     *
     * @param seconds time in seconds
     */
    public Time(double seconds) {
        this.nanoseconds = secondsToNanoseconds(seconds);
    }

    /**
     * Creates a time from nanoseconds.
     *
     * @param nanoseconds time in nanoseconds
     */
    public Time(long nanoseconds) {
        this.nanoseconds = nanoseconds;
    }

    /**
     * Returns the time in nanoseconds.
     *
     * @return nanoseconds
     */
    public long getNanoseconds() {
        return nanoseconds;
    }

    /**
     * Returns the time in seconds.
     *
     * @return seconds
     */
    public double getSeconds() {
        return nanoseconds * NANO_TO_SEC;
    }

    /**
     * Converts seconds to nanoseconds.
     *
     * @param seconds time in seconds
     * @return time in nanoseconds
     */
    protected long secondsToNanoseconds(double seconds) {
        return (long) (seconds * ONE_SECOND);
    }
}

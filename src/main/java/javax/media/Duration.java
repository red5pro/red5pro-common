package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public interface Duration {
    /** Duration value indicating the duration is unbounded. */
    public static Time DURATION_UNBOUNDED = new Time(Long.MAX_VALUE);

    /** Duration value indicating the duration is unknown. */
    public static Time DURATION_UNKNOWN = new Time(Long.MAX_VALUE - 1);

    /**
     * Returns the duration of the media.
     *
     * @return the duration, or DURATION_UNBOUNDED or DURATION_UNKNOWN
     */
    public Time getDuration();

}

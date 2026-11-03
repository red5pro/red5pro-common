package javax.media;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public interface TimeBase {
    /**
     * Returns the current time of this time base in nanoseconds.
     *
     * @return nanoseconds
     */
    public long getNanoseconds();

    /**
     * Returns the current time of this time base.
     *
     * @return current time
     */
    public Time getTime();
}

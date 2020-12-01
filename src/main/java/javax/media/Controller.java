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

	public static final Time LATENCY_UNKNOWN = new Time(Long.MAX_VALUE);

	public void close();

	public void deallocate();

	public Control getControl(String forName);

	public Control[] getControls();

	public Time getStartLatency();

	public State getState();

	public State getTargetState();

	public void prefetch();

	public void realize();

}

package javax.media;

/**
 * Standard JMF class.
 * 
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class IncompatibleTimeBaseException extends MediaException {

	private static final long serialVersionUID = -3438317563542022924L;

	public IncompatibleTimeBaseException() {
		super();
	}

	public IncompatibleTimeBaseException(String message) {
		super(message);
	}

}

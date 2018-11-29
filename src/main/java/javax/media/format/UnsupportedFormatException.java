package javax.media.format;

import javax.media.Format;
import javax.media.MediaException;

/**
 * Standard JMF class.
 * 
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class UnsupportedFormatException extends MediaException {

	private static final long serialVersionUID = 3528437653760833635L;

	private final Format unsupportedFormat;

	public UnsupportedFormatException(Format unsupportedFormat) {
		super();
		this.unsupportedFormat = unsupportedFormat;
	}

	public UnsupportedFormatException(String message, Format unsupportedFormat) {
		super(message);
		this.unsupportedFormat = unsupportedFormat;

	}

	public Format getFailedFormat() {
		return unsupportedFormat;
	}
}

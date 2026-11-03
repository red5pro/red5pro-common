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

    /** The format that is not supported. */
    private final Format unsupportedFormat;

    /**
     * Creates an exception without a message.
     *
     * @param unsupportedFormat the format that is not supported
     */
    public UnsupportedFormatException(Format unsupportedFormat) {
        super();
        this.unsupportedFormat = unsupportedFormat;
    }

    /**
     * Creates an exception with a message.
     *
     * @param message the detail message
     * @param unsupportedFormat the format that is not supported
     */
    public UnsupportedFormatException(String message, Format unsupportedFormat) {
        super(message);
        this.unsupportedFormat = unsupportedFormat;

    }

    /**
     * Returns the format that was not supported.
     *
     * @return the unsupported format
     */
    public Format getFailedFormat() {
        return unsupportedFormat;
    }
}

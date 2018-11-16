package javax.media.format;

import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.Format;

/**
 * Standard JMF class.
 * @see <a href="http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class FormatChangeEvent extends ControllerEvent {
    /**
     * 
     */
    private static final long serialVersionUID = 7416642567048750643L;

    protected Format oldFormat;

    protected Format newFormat;

    public FormatChangeEvent(Controller source) {
        super(source);
    }

    public FormatChangeEvent(Controller source, Format oldFormat, Format newFormat) {
        super(source);
        this.oldFormat = oldFormat;
        this.newFormat = newFormat;
    }

    public Format getNewFormat() {
        return newFormat;
    }

    public Format getOldFormat() {
        return oldFormat;
    }
}

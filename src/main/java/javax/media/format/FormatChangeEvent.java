package javax.media.format;

import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.Format;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class FormatChangeEvent extends ControllerEvent {

    private static final long serialVersionUID = 7416642567048750643L;

    /**
     * The format before the change.
     */
    protected Format oldFormat;

    /**
     * The format after the change.
     */
    protected Format newFormat;

    /**
     * Creates an event without format information.
     *
     * @param source controller that generated the event
     */
    public FormatChangeEvent(Controller source) {
        super(source);
    }

    /**
     * Creates an event describing a format change.
     *
     * @param source controller that generated the event
     * @param oldFormat format before the change
     * @param newFormat format after the change
     */
    public FormatChangeEvent(Controller source, Format oldFormat, Format newFormat) {
        super(source);
        this.oldFormat = oldFormat;
        this.newFormat = newFormat;
    }

    /**
     * Returns the format after the change.
     *
     * @return new format
     */
    public Format getNewFormat() {
        return newFormat;
    }

    /**
     * Returns the format before the change.
     *
     * @return old format
     */
    public Format getOldFormat() {
        return oldFormat;
    }
}

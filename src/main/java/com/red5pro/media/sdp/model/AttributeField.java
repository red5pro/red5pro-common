package com.red5pro.media.sdp.model;

import java.util.regex.Pattern;

/**
 * The attributes "a=" are the primary means for extending SDP. Attributes may
 * be defined to be used as "session-level" attributes, "media-level"
 * attributes, or both.
 *
 * A media description may have any number of attributes ("a=" fields) that are
 * media specific. These are referred to as "media-level" attributes and add
 * information about the media stream. Attribute fields can also be added before
 * the first media field; these "session-level" attributes convey additional
 * information that applies to the conference as a whole rather than to
 * individual media.
 *
 * <pre>
      a=&lt;attribute&gt;
      a=&lt;attribute&gt;:&lt;value&gt;
 * </pre>
 *
 * @author Paul Gregoire
 */
public class AttributeField {

    /** Matches an SDP attribute name and optional value. */
    public final static Pattern PATTERN = Pattern.compile("([\\w|-]+)(:(.*)){0,1}");

    // indication that this is a binary attribute (ie. no ':')
    private boolean binary;

    private AttributeKey attribute;

    private String value;

    /** Creates an attribute, treating a null value as a binary flag.
     * @param attribute SDP attribute key
     * @param value attribute value, or null for a binary flag
     */
    public AttributeField(AttributeKey attribute, String value) {
        this.attribute = attribute;
        if (value == null) {
            binary = true;
        } else {
            this.value = value;
        }
    }

    /** Returns whether this attribute has no value.
     * @return true when this is a binary flag
     */
    public boolean isBinary() {
        return binary;
    }

    /** Sets whether this attribute has no value.
     * @param binary true for a binary flag
     */
    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    /** Returns the attribute key.
     * @return attribute key
     */
    public AttributeKey getAttribute() {
        return attribute;
    }

    /** Sets the attribute key.
     * @param attribute attribute key
     */
    public void setAttribute(AttributeKey attribute) {
        this.attribute = attribute;
    }

    /** Returns the attribute value, or null for a binary flag.
     * @return attribute value
     */
    public String getValue() {
        return value;
    }

    /** Sets the value; null marks this as a binary flag.
     * @param value attribute value
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (!binary) {
            return String.format("a=%s:%s\n", attribute, value);
        }
        return String.format("a=%s\n", attribute);
    }

}

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
      a=<attribute>
      a=<attribute>:<value>
 * </pre>
 *
 * @author Paul Gregoire
 */
public class AttributeField {

	public final static Pattern PATTERN = Pattern.compile("([\\w|-]+)(:(.*)){0,1}");

	// indication that this is a binary attribute (ie. no ':')
	private boolean binary;

	private AttributeKey attribute;

	private String value;

	public AttributeField(AttributeKey attribute, String value) {
		this.attribute = attribute;
		if (value == null) {
			binary = true;
		} else {
			this.value = value;
		}
	}

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}

	public AttributeKey getAttribute() {
		return attribute;
	}

	public void setAttribute(AttributeKey attribute) {
		this.attribute = attribute;
	}

	public String getValue() {
		return value;
	}

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

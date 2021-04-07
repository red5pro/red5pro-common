package com.red5pro.media.sdp.model;

import java.util.regex.Pattern;

/**
 * The session name "s=" field is the textual session name. There MUST be one
 * and only one "s=" field per session description.
 * 
 * <pre>
      s=<session name>
 * </pre>
 *
 * @author Paul Gregoire
 */
public class SessionField {

	public final static Pattern PATTERN = Pattern.compile("([\\w|\\d|\\s|-]+)");

	private String name = "-";

	public SessionField() {
	}

	public SessionField(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("s=%s\n", name);
	}

}

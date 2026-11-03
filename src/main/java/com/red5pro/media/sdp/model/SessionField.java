package com.red5pro.media.sdp.model;

import java.util.regex.Pattern;

/**
 * The session name "s=" field is the textual session name. There MUST be one
 * and only one "s=" field per session description.
 *
 * <pre>
      s=&lt;session name&gt;
 * </pre>
 *
 * @author Paul Gregoire
 */
public class SessionField {

    /** Pattern matching a valid session name value. */
    public final static Pattern PATTERN = Pattern.compile("([\\w|\\d|\\s|-]+)");

    private String name = "-";

    /**
     * Creates a session field with the default name "-".
     */
    public SessionField() {
    }

    /**
     * Creates a session field with the given name.
     *
     * @param name the session name
     */
    public SessionField(String name) {
        this.name = name;
    }

    /**
     * Returns the session name.
     *
     * @return the session name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the session name.
     *
     * @param name the session name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("s=%s\n", name);
    }

}

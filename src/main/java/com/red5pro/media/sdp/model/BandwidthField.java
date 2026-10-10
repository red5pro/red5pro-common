package com.red5pro.media.sdp.model;

import java.util.regex.Pattern;

/**
 * The bandwidth "b=" field. This OPTIONAL field denotes the proposed bandwidth
 * to be used by the session or media. The bandwidth type is an alphanumeric
 * modifier that describes the bandwidth value.
 *
 * <pre>
      b=&lt;bwtype&gt;:&lt;bandwidth&gt;
 * </pre>
 *
 * @author Paul Gregoire
 */
public class BandwidthField {

    /** Matches an SDP bandwidth type and value. */
    public final static Pattern PATTERN = Pattern.compile("([A-Z]{2}):([0-9]{1,6})"); // allot for hundred-mega-streamers

    private String type = "AS";

    private int bandwidth;

    /** Creates a bandwidth field with the default type and zero value. */
    public BandwidthField() {
    }

    /** Creates a bandwidth field with the default type.
     * @param bandwidth bandwidth value
     */
    public BandwidthField(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    /** Creates a bandwidth field with the given type and value.
     * @param type bandwidth type
     * @param bandwidth bandwidth value
     */
    public BandwidthField(String type, int bandwidth) {
        this.type = type;
        this.bandwidth = bandwidth;
    }

    /** Returns the bandwidth type.
     * @return bandwidth type
     */
    public String getType() {
        return type;
    }

    /** Sets the bandwidth type.
     * @param type bandwidth type
     */
    public void setType(String type) {
        this.type = type;
    }

    /** Returns the bandwidth value.
     * @return bandwidth value
     */
    public int getBandwidth() {
        return bandwidth;
    }

    /** Sets the bandwidth value.
     * @param bandwidth bandwidth value
     */
    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    @Override
    public String toString() {
        return String.format("b=%s:%d\n", type, bandwidth);
    }

}

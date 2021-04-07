package com.red5pro.media.sdp.model;

import java.util.regex.Pattern;

/**
 * The bandwidth "b=" field. This OPTIONAL field denotes the proposed bandwidth
 * to be used by the session or media. The <bwtype> is an alphanumeric modifier
 * giving the meaning of the <bandwidth> figure.
 * 
 * <pre>
      b=<bwtype>:<bandwidth>
 * </pre>
 *
 * @author Paul Gregoire
 */
public class BandwidthField {

    public final static Pattern PATTERN = Pattern.compile("([A-Z]{2}):([0-9]{1,4})");

    private String type = "AS";

    private int bandwidth;

    public BandwidthField() {
    }

    public BandwidthField(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public BandwidthField(String type, int bandwidth) {
        this.type = type;
        this.bandwidth = bandwidth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    @Override
    public String toString() {
        return String.format("b=%s:%d\n", type, bandwidth);
    }

}

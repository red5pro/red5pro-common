package com.red5pro.media.sdp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.red5pro.media.rtp.RTPCodecEnum;

/**
 * The media descriptions "m=" field. A session description may contain a number
 * of media descriptions. Each media description starts with an "m=" field and
 * is terminated by either the next "m=" field or by the end of the session
 * description.
 *
 * <pre>
      m=<media> <port> <proto> <fmt> ...

      m=audio 49152 UDP/TLS/RTP/SAVPF 109
 * </pre>
 *
 * @author Paul Gregoire
 */
public class MediaField implements Comparable<MediaField> {

    // Java regex tester https://www.regexplanet.com/advanced/java/index.html
    public final static Pattern PATTERN = Pattern.compile("([\\w]{4,11}) ([0-9]{1,5}) ([\\w|\\/]*)(((\\s[0-9]{1,4})+)|(\\swebrtc-datachannel))");

    // get first number at the start of a string
    public final static Pattern PATTERN_GET_FIRST_NUMBER = Pattern.compile("^(\\d+)");

    public final static String PROTOCOL_ANY = "RTP/SAVPF";

    public final static String PROTOCOL_UDP = "UDP/TLS/RTP/SAVPF";

    public final static String PROTOCOL_TCP = "TCP/TLS/RTP/SAVPF";

    public final static String PROTOCOL_AVP = "RTP/AVP";

    public final static String PROTOCOL_SCTP = "DTLS/SCTP";

    public final static String PROTOCOL_UDP_SCTP = "UDP/DTLS/SCTP";

    public final static String PROTOCOL_TCP_SCTP = "TCP/DTLS/SCTP";

    private SDPMediaType mediaType;

    private int port = 9;

    // transport protocol
    private String protocol = PROTOCOL_UDP;

    // media format description
    private int[] formats;

    private ConnectionField connection;

    // media level attributes
    private AttributeField[] attributes;

    private BandwidthField bandwidth;

    private String mediaId;

    public MediaField() {
    }

    public MediaField(SDPMediaType mediaType, int port, int codecCount) {
        this.mediaType = mediaType;
        // default the media id on creation so subscribe will work properly
        this.mediaId = mediaType.name();
        this.port = port;
        this.formats = new int[codecCount];
    }

    public MediaField(SDPMediaType mediaType, int port, String protocol, int codecCount) {
        this.mediaType = mediaType;
        // default the media id on creation so subscribe will work properly
        this.mediaId = mediaType.name();
        this.port = port;
        this.protocol = protocol;
        this.formats = new int[codecCount];
    }

    public MediaField(SDPMediaType mediaType, int port, String protocol, int[] formats) {
        this.mediaType = mediaType;
        // default the media id on creation so subscribe will work properly
        this.mediaId = mediaType.name();
        this.port = port;
        this.protocol = protocol;
        this.formats = formats;
    }

    /**
     * Returns true if a given attribute exists.
     *
     * @param key
     * @return true if found and false otherwise
     */
    public boolean hasAttribute(AttributeKey key) {
        if (attributes != null && attributes.length > 0) {
            for (AttributeField attr : attributes) {
                if (attr != null && attr.getAttribute().equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if a given attribute exists containing the given value.
     *
     * @param key
     * @param value
     * @return true if found and false otherwise
     */
    public boolean hasAttributeWithValue(AttributeKey key, String value) {
        if (attributes != null && attributes.length > 0) {
            for (AttributeField attr : attributes) {
                if (attr != null && attr.getAttribute().equals(key) && attr.getValue().indexOf(value) > -1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Lookup attribute by its key.
     *
     * @param key
     * @return attribute if found and null otherwise
     */
    public AttributeField getAttribute(AttributeKey key) {
        if (attributes != null && attributes.length > 0) {
            for (AttributeField attr : attributes) {
                if (attr.getAttribute().equals(key)) {
                    return attr;
                }
            }
        }
        return null;
    }

    /**
     * Lookup attribute by a given key and with a matching identifier.
     *
     * @param key
     * @param withId
     * @return attribute if found and null otherwise
     */
    public AttributeField getAttribute(AttributeKey key, int withId) {
        if (attributes != null && attributes.length > 0) {
            for (AttributeField attr : attributes) {
                if (attr != null && attr.getAttribute().equals(key) && attr.getValue().indexOf(String.valueOf(withId)) > -1) {
                    return attr;
                }
            }
        }
        return null;
    }

    /**
     * Lookup attribute by a given key containing a given string.
     *
     * @param key
     * @param withString
     * @return attribute if found and null otherwise
     */
    public AttributeField getAttribute(AttributeKey key, String withString) {
        if (attributes != null && attributes.length > 0) {
            for (AttributeField attr : attributes) {
                if (attr != null && attr.getAttribute().equals(key) && attr.getValue().indexOf(withString) > -1) {
                    return attr;
                }
            }
        }
        return null;
    }

    /**
     * Lookup attribute by a given key and with a matching codec encoding from a set
     * of codecs.
     *
     * @param key
     * @param codecs
     *            set of codecs in-order of preference
     * @return attribute if found and null otherwise
     */
    public AttributeField getAttribute(AttributeKey key, EnumSet<RTPCodecEnum> codecs) {
        for (RTPCodecEnum codec : codecs) {
            if (attributes != null && attributes.length > 0) {
                for (AttributeField attr : attributes) {
                    // skip binary attributes as they are not codec related
                    if (attr != null && attr.getAttribute().equals(key) && !attr.isBinary()) {
                        String[] vals = attr.getValue().split("[\\s|/]");
                        if (vals.length >= 2) {
                            String codecName = vals[1];
                            if (codecName.equals(codec.encodingName)) {
                                return attr;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns an attributes first integer in the value.
     *
     * @param attr
     * @return number if found and -1 otherwise
     */
    public int getAttributeInt(AttributeField attr) {
        int ret = -1;
        if (attr != null) {
            Matcher matcher = PATTERN_GET_FIRST_NUMBER.matcher(attr.getValue());
            if (matcher.find()) {
                ret = Integer.valueOf(matcher.group());
            }
        }
        return ret;
    }

    /**
     * Returns an attributes first integer in the value.
     *
     * @param key
     * @return number if found and -1 otherwise
     */
    public int getAttributeInt(AttributeKey key) {
        int ret = -1;
        if (attributes != null && attributes.length > 0) {
            for (AttributeField attr : attributes) {
                if (attr != null && attr.getAttribute().equals(key)) {
                    Matcher matcher = PATTERN_GET_FIRST_NUMBER.matcher(attr.getValue());
                    if (matcher.find()) {
                        ret = Integer.valueOf(matcher.group());
                        break;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Returns an attributes first long in the value.
     *
     * @param attr
     * @return number if found and -1 otherwise
     */
    public long getAttributeLong(AttributeField attr) {
        long ret = -1;
        if (attr != null) {
            Matcher matcher = PATTERN_GET_FIRST_NUMBER.matcher(attr.getValue());
            if (matcher.find()) {
                ret = Long.valueOf(matcher.group());
            }
        }
        return ret;
    }

    /**
     * Returns an attributes first long in the value.
     *
     * @param key
     * @return number if found and -1 otherwise
     */
    public long getAttributeLong(AttributeKey key) {
        long ret = -1;
        if (attributes != null && attributes.length > 0) {
            for (AttributeField attr : attributes) {
                if (attr != null && attr.getAttribute().equals(key)) {
                    Matcher matcher = PATTERN_GET_FIRST_NUMBER.matcher(attr.getValue());
                    if (matcher.find()) {
                        ret = Long.valueOf(matcher.group());
                        break;
                    }
                }
            }
        }
        return ret;
    }

    public List<AttributeField> getAttributeSelections(AttributeKey key, EnumSet<RTPCodecEnum> codecs) {
        List<AttributeField> ret = new ArrayList<>();
        for (RTPCodecEnum codec : codecs) {
            if (attributes != null && attributes.length > 0) {
                for (AttributeField attr : attributes) {
                    // skip binary attributes as they are not codec related
                    if (attr != null && attr.getAttribute().equals(key) && !attr.isBinary()) {
                        String[] vals = attr.getValue().split("[\\s|/]");
                        if (vals.length >= 2) {
                            String codecName = vals[1];
                            if (codecName.equals(codec.encodingName)) {
                                ret.add(attr);
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    public SDPMediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(SDPMediaType mediaType) {
        this.mediaType = mediaType;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void addFormat(int format) {
        if (formats == null) {
            formats = new int[1];
            formats[0] = format;
        } else {
            int index = formats.length;
            int[] temp = Arrays.copyOf(formats, formats.length + 1);
            temp[index] = format;
            setFormats(temp);
        }
    }

    public int[] getFormats() {
        return formats;
    }

    public void setFormats(int[] formats) {
        this.formats = new int[formats.length];
        System.arraycopy(formats, 0, this.formats, 0, formats.length);
    }

    public ConnectionField getConnection() {
        return connection;
    }

    public void setConnection(ConnectionField connection) {
        this.connection = connection;
    }

    public void addAttributeField(AttributeField attr) {
        if (AttributeKey.mid.equals(attr.getAttribute())) {
            mediaId = attr.getValue();
        }
        if (attributes == null) {
            attributes = new AttributeField[1];
            attributes[0] = attr;
        } else {
            int index = attributes.length;
            AttributeField[] temp = Arrays.copyOf(attributes, attributes.length + 1);
            temp[index] = attr;
            setAttributes(temp);
        }
    }

    public AttributeField[] getAttributes(AttributeKey key) {
        List<AttributeField> subset = new ArrayList<>(1);
        if (attributes != null && attributes.length > 0) {
            for (AttributeField attr : attributes) {
                if (attr != null && attr.getAttribute().equals(key)) {
                    subset.add(attr);
                }
            }
        }
        return subset.toArray(new AttributeField[0]);
    }

    public AttributeField[] getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributeField[] attributes) {
        this.attributes = attributes;
    }

    public BandwidthField getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(BandwidthField bandwidth) {
        this.bandwidth = bandwidth;
    }

    /**
     * Returns the media identifier or mid.
     *
     * @return mediaId
     */
    public String getMediaId() {
        return mediaId;
    }

    /**
     * Sets the media identifier or mid.
     *
     * @param mediaId
     */
    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mediaType == null) ? 0 : mediaType.hashCode());
        result = prime * result + ((mediaId == null) ? 0 : mediaId.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MediaField other = (MediaField) obj;
        if (mediaType != other.mediaType)
            return false;
        if (mediaId != other.mediaId)
            return false;
        if (port != other.port)
            return false;
        return true;
    }

    // order of sections m, i, c, b, k, a
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("m=");
        sb.append(mediaType);
        sb.append(' ');
        sb.append(port);
        sb.append(' ');
        // if its application (datachannel) type, we'll have special handling
        if (mediaType == SDPMediaType.application) {
            if (hasAttribute(AttributeKey.sctpport)) {
                // XXX UDP vs TCP may matter one day...
                sb.append(PROTOCOL_UDP_SCTP);
                sb.append(' ');
                sb.append("webrtc-datachannel"); // newer draft uses this style + sctpport
            } else {
                sb.append(protocol);
                sb.append(' ');
                sb.append(formats[0]);
            }
        } else {
            sb.append(protocol);
            if (formats != null) {
                for (int format : formats) {
                    sb.append(' ');
                    sb.append(format);
                }
            }
        }
        sb.append('\n');
        // next line, connection
        if (connection != null) {
            sb.append(connection);
        }
        // next line, bandwidth
        if (bandwidth != null) {
            sb.append(bandwidth);
        }
        // next line(s) attributes
        if (attributes != null) {
            for (AttributeField attribute : attributes) {
                switch (attribute.getAttribute()) {
                    case fmtp:
                    case rtpmap:
                    case rtcpfb:
                        if (Arrays.binarySearch(formats, Integer.valueOf(attribute.getValue().split("\\s")[0])) == -1) {
                            break;
                        }
                    default:
                        sb.append(attribute);
                }
            }
        }
        return sb.toString();
    }

    @Override
    public int compareTo(MediaField that) {
        int thisIndex = StringUtils.isNumeric(getMediaId()) ? Integer.valueOf(getMediaId()) : -1;
        if (thisIndex == -1) {
            return getMediaType().compareTo(that.getMediaType());
        } else {
            int thatIndex = StringUtils.isNumeric(that.getMediaId()) ? Integer.valueOf(that.getMediaId()) : -1;
            if (thatIndex == -1) {
                return getMediaType().compareTo(that.getMediaType());
            } else {
                return Integer.compare(thisIndex, thatIndex);
            }
        }
    }

}

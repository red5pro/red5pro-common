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
      m=&lt;media&gt; &lt;port&gt; &lt;proto&gt; &lt;fmt&gt; ...

      m=audio 49152 UDP/TLS/RTP/SAVPF 109
 * </pre>
 *
 * @author Paul Gregoire
 */
public class MediaField implements Comparable<MediaField> {

    // Java regex tester https://www.regexplanet.com/advanced/java/index.html
    /**
     * Pattern matching the media, port, protocol and format list of an m= line value.
     */
    public final static Pattern PATTERN = Pattern.compile("([\\w]{4,11}) ([0-9]{1,5}) ([\\w|\\/]*)(((\\s[0-9]{1,4})+)|(\\swebrtc-datachannel))");

    // get first number at the start of a string
    /**
     * Pattern capturing the integer at the start of a string.
     */
    public final static Pattern PATTERN_GET_FIRST_NUMBER = Pattern.compile("^(\\d+)");

    /**
     * Generic protocol: RTP/SAVPF.
     */
    public final static String PROTOCOL_ANY = "RTP/SAVPF";

    /**
     * Protocol: UDP/TLS/RTP/SAVPF.
     */
    public final static String PROTOCOL_UDP = "UDP/TLS/RTP/SAVPF";

    /**
     * Protocol: TCP/TLS/RTP/SAVPF.
     */
    public final static String PROTOCOL_TCP = "TCP/TLS/RTP/SAVPF";

    /**
     * Protocol: RTP/AVP.
     */
    public final static String PROTOCOL_AVP = "RTP/AVP";

    /**
     * Protocol: DTLS/SCTP.
     */
    public final static String PROTOCOL_SCTP = "DTLS/SCTP";

    /**
     * Protocol: UDP/DTLS/SCTP.
     */
    public final static String PROTOCOL_UDP_SCTP = "UDP/DTLS/SCTP";

    /**
     * Protocol: TCP/DTLS/SCTP.
     */
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

    /**
     * Creates an empty media field with default port 9 and UDP/TLS/RTP/SAVPF protocol.
     */
    public MediaField() {
    }

    /**
     * Creates a media field with the default protocol and an empty format list.
     *
     * @param mediaType media type
     * @param port transport port
     * @param codecCount number of formats to allocate
     */
    public MediaField(SDPMediaType mediaType, int port, int codecCount) {
        this.mediaType = mediaType;
        // default the media id on creation so subscribe will work properly
        this.mediaId = mediaType.name();
        this.port = port;
        this.formats = new int[codecCount];
    }

    /**
     * Creates a media field with an empty format list.
     *
     * @param mediaType media type
     * @param port transport port
     * @param protocol transport protocol
     * @param codecCount number of formats to allocate
     */
    public MediaField(SDPMediaType mediaType, int port, String protocol, int codecCount) {
        this.mediaType = mediaType;
        // default the media id on creation so subscribe will work properly
        this.mediaId = mediaType.name();
        this.port = port;
        this.protocol = protocol;
        this.formats = new int[codecCount];
    }

    /**
     * Creates a media field with the given formats.
     *
     * @param mediaType media type
     * @param port transport port
     * @param protocol transport protocol
     * @param formats payload type numbers
     */
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
     * @param key attribute key
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
     * @param key attribute key
     * @param value text the attribute value must contain
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
     * @param key attribute key
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
     * @param key attribute key
     * @param withId identifier that the attribute value must contain
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
     * @param key attribute key
     * @param withString text that the attribute value must contain
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
     * @param key attribute key
     * @param codecs set of codecs in-order of preference
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
     * @param attr attribute to read
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
     * @param key attribute key
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
     * @param attr attribute to read
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
     * @param key attribute key
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

    /**
     * Collects all non-binary attributes with the given key whose codec name matches one of the codecs, ignoring case.
     *
     * @param key attribute key
     * @param codecs codecs to match, in order of preference
     * @return matching attributes, possibly empty
     */
    public List<AttributeField> getAttributeSelections(AttributeKey key, EnumSet<RTPCodecEnum> codecs) {
        List<AttributeField> ret = new ArrayList<>();
        for (RTPCodecEnum codec : codecs) {
            if (attributes != null && attributes.length > 0) {
                for (AttributeField attr : attributes) {
                    // skip binary attributes as they are not codec related
                    if (attr != null && attr.getAttribute().equals(key) && !attr.isBinary()) {
                        String[] vals = attr.getValue().split("[\\s|/]");
                        if (vals.length >= 2) {
                            // ensure its trimmed
                            String codecName = vals[1].trim();
                            // allow any casing
                            if (codec.encodingName.equalsIgnoreCase(codecName)) {
                                ret.add(attr);
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Returns the media type.
     *
     * @return media type
     */
    public SDPMediaType getMediaType() {
        return mediaType;
    }

    /**
     * Sets the media type.
     *
     * @param mediaType media type
     */
    public void setMediaType(SDPMediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Returns the transport port.
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the transport port.
     *
     * @param port port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns the transport protocol.
     *
     * @return protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the transport protocol.
     *
     * @param protocol protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Appends a payload format to the format list.
     *
     * @param format payload type number
     */
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

    /**
     * Returns the payload formats.
     *
     * @return payload type numbers
     */
    public int[] getFormats() {
        return formats;
    }

    /**
     * Sets the payload formats, copying the given array.
     *
     * @param formats payload type numbers
     */
    public void setFormats(int[] formats) {
        this.formats = new int[formats.length];
        System.arraycopy(formats, 0, this.formats, 0, formats.length);
    }

    /**
     * Returns the media-level connection field.
     *
     * @return connection field, may be null
     */
    public ConnectionField getConnection() {
        return connection;
    }

    /**
     * Sets the media-level connection field.
     *
     * @param connection connection field
     */
    public void setConnection(ConnectionField connection) {
        this.connection = connection;
    }

    /**
     * Appends an attribute; a mid attribute also sets the media identifier.
     *
     * @param attr attribute to add
     */
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

    /**
     * Removes the first attribute with the given key.
     *
     * @param key attribute key to remove
     */
    // XXX(paul) this is not thread-safe
    public void removeAttributeField(AttributeKey key) {
        List<AttributeField> attrs = List.of(attributes);
        if (!attrs.isEmpty()) {
            for (AttributeField attr : attrs) {
                if (attr != null && attr.getAttribute().equals(key)) {
                    // remove it
                    attrs.remove(attr);
                    // reset attributes
                    setAttributes(attrs.toArray(new AttributeField[0]));
                    break;
                }
            }
        }
    }

    /**
     * Returns all attributes with the given key.
     *
     * @param key attribute key
     * @return matching attributes, possibly empty
     */
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

    /**
     * Returns all media-level attributes.
     *
     * @return attributes, may be null
     */
    public AttributeField[] getAttributes() {
        return attributes;
    }

    /**
     * Sets the media-level attributes.
     *
     * @param attributes attributes
     */
    public void setAttributes(AttributeField[] attributes) {
        this.attributes = attributes;
    }

    /**
     * Returns the media-level bandwidth field.
     *
     * @return bandwidth field, may be null
     */
    public BandwidthField getBandwidth() {
        return bandwidth;
    }

    /**
     * Sets the media-level bandwidth field.
     *
     * @param bandwidth bandwidth field
     */
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
     * @param mediaId media identifier
     */
    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    /**
     * Sets whether or not the media is active.
     *
     * @param active true to activate, false to mark inactive
     */
    public void setActive(boolean active) {
        if (active) {
            removeAttributeField(AttributeKey.inactive);
        } else {
            addAttributeField(new AttributeField(AttributeKey.inactive, null));
        }
    }

    /**
     * Whether or not the media is active.
     *
     * @return true if active and false if inactive
     */
    public boolean isActive() {
        return attributes == null || !hasAttribute(AttributeKey.inactive);
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
    @SuppressWarnings("incomplete-switch")
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
                final AttributeKey attrKey = attribute.getAttribute();
                // only include all attributes if the media is active
                if (isActive()) {
                    switch (attrKey) {
                        case fmtp:
                        case rtpmap:
                        case rtcpfb:
                            if (Arrays.binarySearch(formats, Integer.valueOf(attribute.getValue().split("\\s")[0])) == -1) {
                                break;
                            }
                        default:
                            sb.append(attribute);
                    }
                } else {
                    // only include certain attributes if the media is inactive
                    switch (attrKey) {
                        case rtpmap:
                            if (Arrays.binarySearch(formats, Integer.valueOf(attribute.getValue().split("\\s")[0])) == -1) {
                                break;
                            }
                        case mid:
                        case iceufrag:
                        case icepwd:
                        case fingerprint:
                        case setup:
                        case sendonly:
                        case recvonly:
                        case rtcpmux:
                        case inactive:
                        case candidate:
                        case endofcandidates:
                            sb.append(attribute);
                    }
                }
            }
        }
        return sb.toString();
    }

    @Override
    public int compareTo(MediaField that) {
        int thisIndex = StringUtils.isNumeric(mediaId) ? Integer.valueOf(mediaId) : -1;
        int thatIndex = StringUtils.isNumeric(that.getMediaId()) ? Integer.valueOf(that.getMediaId()) : -1;
        if (thisIndex != -1 && thatIndex != -1) {
            return Integer.compare(thisIndex, thatIndex);
        } else {
            // both are non-numeric
            // prefer audio and video over application
            if (SDPMediaType.application.equals(mediaType) && SDPMediaType.audioVideo.contains(that.mediaType)) {
                return 1;
            } else if (SDPMediaType.audioVideo.contains(mediaType) && SDPMediaType.application.equals(that.mediaType)) {
                return -1;
            }
            // compare as strings
            return mediaId.compareTo(that.getMediaId());
        }
    }

}

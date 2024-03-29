package javax.media.format;

import java.awt.Dimension;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.media.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.media.VideoConstants;

/**
 * Implements a <pre>VideoFormat</pre> with parameters.
 *
 * @author Lyubomir Marinov
 */
public class ParameterizedVideoFormat extends VideoFormat {

    private static final Logger log = LoggerFactory.getLogger(ParameterizedVideoFormat.class);

    private static final long serialVersionUID = 0L;

    /**
     * The format parameters of this <pre>ParameterizedVideoFormat</pre> instance.
     */
    private Map<String, String> fmtps;

    /**
     * Constructs a new <pre>ParametizedVideoFormat</pre>.
     *
     * @param encoding
     *            encoding
     * @param size
     *            video size
     * @param maxDataLength
     *            maximum data length
     * @param dataType
     *            data type
     * @param frameRate
     *            frame rate
     * @param fmtps
     *            format parameters
     */
    public ParameterizedVideoFormat(String encoding, Dimension size, int maxDataLength, Class<?> dataType, float frameRate, Map<String, String> fmtps) {
        super(encoding, size, maxDataLength, dataType, frameRate);
        this.fmtps = ((fmtps == null) || fmtps.isEmpty()) ? Collections.emptyMap() : new HashMap<>(fmtps);
    }

    /**
     * Initializes a new <pre>ParameterizedVideoFormat</pre> with a specific encoding
     * and a specific set of format parameters.
     *
     * @param encoding
     *            the encoding of the new instance
     * @param fmtps
     *            the format parameters of the new instance
     */
    public ParameterizedVideoFormat(String encoding, Map<String, String> fmtps) {
        super(encoding);
        this.fmtps = ((fmtps == null) || fmtps.isEmpty()) ? Collections.emptyMap() : new HashMap<>(fmtps);
    }

    /**
     * Initializes a new <pre>ParameterizedVideoFormat</pre> with a specific encoding
     * and a specific set of format parameters.
     *
     * @param encoding
     *            the encoding of the new instance
     * @param fmtps
     *            the format parameters of the new instance in the form of an array
     *            of <pre>String</pre>s in which the key and the value of an
     *            association are expressed as consecutive elements
     */
    public ParameterizedVideoFormat(String encoding, String... fmtps) {
        this(encoding, toMap(fmtps));
    }

    /**
     * Initializes a new <pre>ParameterizedVideoFormat</pre> instance which has the
     * same properties as this instance.
     *
     * @return a new <pre>ParameterizedVideoFormat</pre> instance which has the same
     *         properties as this instance
     */
    @Override
    public Object clone() {
        ParameterizedVideoFormat f = new ParameterizedVideoFormat(getEncoding(), getSize(), getMaxDataLength(), getDataType(), getFrameRate(), null);
        f.copy(this);
        return f;
    }

    /**
     * Copies the properties of the specified <pre>Format</pre> into this instance.
     *
     * @param f
     *            the <pre>Format</pre> the properties of which are to be copied into
     *            this instance
     */
    @Override
    protected void copy(Format f) {
        super.copy(f);
        if (f instanceof ParameterizedVideoFormat) {
            ParameterizedVideoFormat pvf = (ParameterizedVideoFormat) f;
            Map<String, String> pvfFmtps = pvf.getFormatParameters();
            fmtps = ((pvfFmtps == null) || pvfFmtps.isEmpty()) ? Collections.emptyMap() : new HashMap<>(pvfFmtps);
        }
    }

    /**
     * Determines whether a specific <pre>Object</pre> represents a value that is
     * equal to the value represented by this instance.
     *
     * @param obj
     *            the <pre>Object</pre> to be determined whether it represents a value
     *            that is equal to the value represented by this instance
     * @return <pre>true</pre> if the specified <pre>obj</pre> represents a value that
     *         is equal to the value represented by this instance; otherwise,
     *         <pre>false</pre>
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Map<String, String> objFmtps = null;
        if (obj instanceof ParameterizedVideoFormat) {
            objFmtps = ((ParameterizedVideoFormat) obj).getFormatParameters();
        }
        return VideoConstants.formatParametersAreEqual(getEncoding(), getFormatParameters(), objFmtps);
    }

    /**
     * Returns whether or not the format parameters match.
     *
     * @param format
     *            format to test
     * @return true if the format parameters match
     */
    public boolean formatParametersMatch(Format format) {
        Map<String, String> formatFmtps = null;
        if (format instanceof ParameterizedVideoFormat) {
            formatFmtps = ((ParameterizedVideoFormat) format).getFormatParameters();
        }
        return VideoConstants.formatParametersMatch(getEncoding(), getFormatParameters(), formatFmtps);
    }

    /**
     * Returns the format parameters value for the specified name.
     *
     * @param name
     *            format parameters name
     * @return value for the specified format parameters name
     */
    public String getFormatParameter(String name) {
        return fmtps.get(name);
    }

    /**
     * Returns the format parameters <pre>Map</pre>.
     *
     * @return the format parameters map
     */
    public Map<String, String> getFormatParameters() {
        return new HashMap<String, String>(fmtps);
    }

    /**
     * Finds the attributes shared by two matching <pre>Format</pre>s. If the
     * specified <pre>Format</pre> does not match this one, the result is undefined.
     *
     * @param format
     *            the matching <pre>Format</pre> to intersect with this one
     * @return a <pre>Format</pre> with its attributes set to the attributes common to
     *         this instance and the specified format
     */
    @Override
    public Format intersects(Format format) {
        Format intersection = super.intersects(format);
        if (intersection == null) {
            return null;
        }
        ((ParameterizedVideoFormat) intersection).fmtps = fmtps.isEmpty() ? Collections.emptyMap() : getFormatParameters();
        return intersection;
    }

    /**
     * Determines whether a specific format matches this instance i.e. whether their
     * attributes match according to the definition of "match" given by
     * {@link Format#matches(Format)}.
     *
     * @param format
     *            the <pre>Format</pre> to compare to this instance
     * @return <pre>true</pre> if the specified <pre>format</pre> matches this one;
     *         otherwise, <pre>false</pre>
     */
    @Override
    public boolean matches(Format format) {
        boolean matches = super.matches(format) && formatParametersMatch(format);
        if (log.isTraceEnabled()) {
            log.trace("matches? {}\n{}\n{}", matches, this, format);
        }
        return matches;
    }

    /**
     * Initializes a new <pre>Map</pre> from an array in which the key and the value
     * of an association are expressed as consecutive elements.
     *
     * @param <T>
     *            the very type of the keys and the values to be associated in the
     *            new <pre>Map</pre>
     * @param entries
     *            the associations to be created in the new <pre>Map</pre> where the
     *            key and value of an association are expressed as consecutive
     *            elements
     * @return a new <pre>Map</pre> with the associations specified by
     *         <pre>entries</pre>
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<T, T> toMap(T... entries) {
        Map<T, T> map;
        if ((entries == null) || (entries.length == 0)) {
            map = null;
        } else {
            map = new HashMap<T, T>();
            for (int i = 0; i < entries.length; i++)
                map.put(entries[i++], entries[i]);
        }
        return map;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(super.toString());
        // fmtps
        {
            s.append(", fmtps={");
            for (Map.Entry<String, String> fmtp : fmtps.entrySet()) {
                s.append(fmtp.getKey());
                s.append('=');
                s.append(fmtp.getValue());
                s.append(',');
            }
            int lastIndex = s.length() - 1;

            if (s.charAt(lastIndex) == ',')
                s.setCharAt(lastIndex, '}');
            else
                s.append('}');
        }
        return s.toString();
    }
}

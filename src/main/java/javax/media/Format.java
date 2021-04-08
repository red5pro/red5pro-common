package javax.media;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <pre>Format</pre> abstracts an exact media format. It carries no
 * encoding-specific parameters or timing information global to the
 * presentation. <br>
 * <h3>Comparing different formats</h3> Not all of the attributes in a
 * <pre>Format</pre> object have to be specified. This enables selected attributes
 * to be specified, making it possible to locate a supported <pre>Format</pre>
 * that meets certain requirements without needing to find an exact match. <br>
 * Two methods are provided for comparing <pre>Formats</pre>. The <pre>equals</pre>
 * method returns <pre>true</pre> if two <pre>Format</pre> objects are exactly the
 * same--they're the same type and all of their attributes are the same. The
 * <pre>matches</pre> method relaxes the comparison, comparing only the attributes
 * that are explicitly specified in the <pre>Format</pre> you are comparing.
 */
public class Format implements Cloneable, Serializable {

    private static final Logger log = LoggerFactory.getLogger(Format.class);

    private static final long serialVersionUID = 5612854984030969319L;

    public static final int NOT_SPECIFIED = -1;

    public static final int TRUE = 1;

    public static final int FALSE = 0;

    protected String encoding;

    /**
     * The data object required by the <pre>Format</pre> is an integer array.
     */
    public static final Class<?> intArray = (new int[0]).getClass();

    /**
     * The data object required by the <pre>Format</pre> is a short array.
     */
    public static final Class<?> shortArray = (new short[0]).getClass();

    /**
     * The data object required by the <pre>Format</pre> is a byte array.
     */
    public static final Class<?> byteArray = (new byte[0]).getClass();

    /**
     * The data object required by the <pre>Format</pre> is an array of
     * <pre>Format</pre> objects.
     */
    public static final Class<?> formatArray = (new Format[0]).getClass();

    protected Class<?> dataType = byteArray;

    // Cache the to optimize on equals, matches & intersect
    protected Class<?> clz = getClass();

    private long encodingCode = 0;

    /**
     * Constructs a <pre>Format</pre> that has the specified encoding type.
     *
     * @param encoding
     *            A <pre>String</pre> that contains the encoding type of the
     *            <pre>Format</pre> to be constructed.
     */
    public Format(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Constructs a <pre>Format</pre> that has the specified encoding and data types.
     *
     * @param encoding
     *            A <pre>String</pre> that contains the encoding type of the
     *            <pre>Format</pre> to be constructed.
     * @param dataType
     *            The type of data object required by the <pre>Format</pre> to be
     *            constructed, such as: <pre>byteArray</pre>, <pre>intArray</pre>, or
     *            <pre>shortArray</pre>. For example, for a byte array the data type
     *            would be " <pre>Format.byteArray</pre>".
     */
    public Format(String encoding, Class<?> dataType) {
        this(encoding);
        this.dataType = dataType;
    }

    /**
     * Creates a clone of this <pre>Format</pre>.
     *
     * @return A clone of this format.
     */
    @Override
    public Object clone() {
        Format f = new Format(encoding);
        f.copy(this);
        return f;
    }

    /**
     * Copies the attributes from the specified <pre>Format</pre> into this
     * <pre>Format</pre>.
     *
     * @param f
     *            The <pre>Format</pre> to copy the attributes from.
     */
    protected void copy(Format f) {
        dataType = f.dataType;
    }

    /**
     * Checks whether or not the specified <pre>Format</pre> is the same as this
     * <pre>Format</pre>. To be equal, the two <pre>Formats</pre> must be of the same
     * type and all of their attributes must be the same.
     *
     * @param format
     *            The <pre>Format</pre> to compare with this one.
     * @return <pre>true</pre> if the specified <pre>Format</pre> is the same as this
     *         one, <pre>false</pre> if it is not.
     */
    @Override
    public boolean equals(Object format) {
        if (format == null || clz != ((Format) format).clz)
            return false;

        String otherEncoding = ((Format) format).encoding;
        Class<?> otherType = ((Format) format).dataType;

        return (dataType == otherType) && (encoding == otherEncoding || ((encoding != null && otherEncoding != null) && isSameEncoding((Format) format)));
    }

    /**
     * Gets the type of the data that this <pre>Format</pre> requires. For example,
     * for byte array it returns "<pre>byte[].class</pre>".
     *
     * @return The data type of this <pre>Format</pre>.
     */
    public Class<?> getDataType() {
        return dataType;
    }

    /**
     * Gets the uniquely-qualified encoding name for this <pre>Format</pre>.
     * <p>
     * In the reference implementation of JMF, these strings follow the QuickTime
     * codec strings.
     *
     * @return The encoding of the <pre>Format</pre>.
     */
    public String getEncoding() {
        return encoding;
    }

    private long getEncodingCode(String enc) {
        byte chars[] = enc.getBytes();
        byte b;
        long code = 0;
        for (int i = 0; i < enc.length(); i++) {
            b = chars[i];
            if (b > 96 && b < 123)
                b -= 32; // lower to upper
            b -= 32;
            if (b > 63)
                return -1;
            code = (code << 6) | b;
        }
        return code;
    }

    /**
     * Intersects the attributes of this format and the specified format to create a
     * new <pre>Format</pre> object. The two objects being intersected should either
     * be of the same type or one should be a subclass of the other. The resulting
     * object will be the same type as the subclass.
     * <P>
     * Common attributes are intersected as follows: If both objects have
     * NOT_SPECIFIED values for an attribute, the result will also have a
     * NOT_SPECIFIED value. If one of them has a NOT_SPECIFIED value then the result
     * will have the value that is specified in the other object. If both objects
     * have specified values then the value in this object will be used.
     * <P>
     * Attributes that are specific to the subclass will be carried forward to the
     * result.
     *
     * @param other
     *            The <pre>Format</pre> object to intersect with this <pre>Format</pre>.
     * @return A <pre>Format</pre> object with its attributes set to those attributes
     *         common to both <pre>Format</pre> objects.
     * @see #matches
     */
    public Format intersects(Format other) {
        Format res;
        if (clz.isAssignableFrom(other.clz))
            res = (Format) other.clone();
        else if (other.clz.isAssignableFrom(clz))
            res = (Format) clone();
        else
            return null;
        if (res.encoding == null)
            res.encoding = (encoding != null ? encoding : other.encoding);
        if (res.dataType == null)
            res.dataType = (dataType != null ? dataType : other.dataType);
        return res;
    }

    /**
     * Checks if the encodings of both format objects are the same. Its faster than
     * calling String.equalsIgnoreCase to compare the two encodings.
     *
     * @param other
     *            format to check
     * @return true if the encodings are the same, false otherwise.
     */
    public boolean isSameEncoding(Format other) {
        if (encoding == null || other == null || other.encoding == null)
            return false;
        // Quick checks
        if (encoding == other.encoding)
            return true;
        if (encodingCode > 0 && other.encodingCode > 0)
            return encodingCode == other.encodingCode;

        // Works faster only for shorter strings of 10 chars or less.
        if (encoding.length() > 10)
            return encoding.equalsIgnoreCase(other.encoding);
        if (encodingCode == 0) {
            encodingCode = getEncodingCode(encoding);
        }
        // If the encoding code cannot be computed (out of bounds chars)
        // or in the off chance that its all spaces.
        if (encodingCode <= 0)
            return encoding.equalsIgnoreCase(other.encoding);

        if (other.encodingCode == 0)
            return other.isSameEncoding(this);
        else
            return encodingCode == other.encodingCode;
    }

    /**
     * Checks if the encoding of this format is same as the parameter. Its faster
     * than calling String.equalsIgnoreCase to compare the two encodings.
     * 
     * @param encoding
     *            encoding to check
     * @return true if the encodings are the same, false otherwise.
     */
    public boolean isSameEncoding(String encoding) {
        if (this.encoding == null || encoding == null)
            return false;
        // Quick check
        if (this.encoding == encoding)
            return true;
        // Works faster only for shorter strings of 10 chars or less.
        if (this.encoding.length() > 10)
            return this.encoding.equalsIgnoreCase(encoding);
        // Compute encoding code only once
        if (encodingCode == 0) {
            encodingCode = getEncodingCode(this.encoding);
        }
        // If the encoding code cannot be computed (out of bounds chars)
        if (encodingCode < 0)
            return this.encoding.equalsIgnoreCase(encoding);
        long otherEncodingCode = getEncodingCode(encoding);
        return encodingCode == otherEncodingCode;
    }

    /**
     * Checks whether or not the specified <pre>Format</pre> <EM>matches</EM> this
     * <pre>Format</pre>. Matches only compares the attributes that are defined in the
     * specified <pre>Format</pre>, unspecified attributes are ignored.
     * <p>
     * The two <pre>Format</pre> objects do not have to be of the same class to match.
     * For example, if "A" are "B" are being compared, a match is possible if "A" is
     * derived from "B" or "B" is derived from "A". (The compared attributes must
     * still match, or <pre>matches</pre> fails.)
     *
     * @param format
     *            The <pre>Format</pre> to compare with this one.
     * @return <pre>true</pre> if the specified <pre>Format</pre> matches this one,
     *         <pre>false</pre> if it does not.
     */
    public boolean matches(Format format) {
        // log.error("matches?\n{}\n{}", this, format);
        if (format == null) {
            return false;
        }
        // log.error("Encoding matches?: {} {} same format? {}", encoding,
        // format.encoding, isSameEncoding(format));
        boolean matches = (format.encoding == null || encoding == null || isSameEncoding(format)) && (format.dataType == null || dataType == null || format.dataType == dataType) && (clz.isAssignableFrom(format.clz) || format.clz.isAssignableFrom(clz));
        if (log.isTraceEnabled()) {
            log.trace("matches? {}\n{}\n{}", matches, this, format);
        }
        return matches;
    }

    /**
     * Generate a format that's less restrictive than this format but contains the
     * basic attributes that will make this resulting format useful for format
     * matching.
     *
     * @return A <pre>Format</pre> that's less restrictive than the this format.
     */
    public Format relax() {
        return (Format) clone();
    }

    /**
     * Gets a <pre>String</pre> representation of the <pre>Format</pre> attributes. For
     * example: "PCM, 44.1 KHz, Stereo, Signed".
     *
     * @return A <pre>String</pre> that describes the <pre>Format</pre> attributes.
     */
    @Override
    public String toString() {
        return getEncoding();
    }
}

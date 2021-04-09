package javax.media.format;

import java.awt.Dimension;

import javax.media.Format;

/**
 * Encapsulates format information for video data. The attributes of a
 * <pre>VideoFormat</pre> include the encoding type, frame size, frame rate, and
 * the data type.
 */
public class VideoFormat extends Format {

    private static final long serialVersionUID = 3595293544666171102L;

    protected Dimension size;

    protected int maxDataLength = NOT_SPECIFIED;

    protected float frameRate = NOT_SPECIFIED;

    /** Raw RGB format. */
    public static final String RGB = "rgb";

    /** Raw YUV or YCrCb format. */
    public static final String YUV = "yuv";

    /** 8-bit Indexed RGB format. */
    public static final String IRGB = "irgb";

    /** Motion JPEG format. */
    public static final String MJPG = "mjpg";

    /**
     * Constructs a <pre>VideoFormat</pre> with the specified encoding type.
     *
     * @param encoding
     *            A <pre>String</pre> that describes the encoding type for this
     *            <pre>VideoFormat</pre>
     */
    public VideoFormat(String encoding) {
        super(encoding);
    }

    /**
     * Constructs a <pre>VideoFormat</pre> with the specified attributes.
     *
     * @param encoding
     *            A <pre>String</pre> that describes the encoding type for this
     *            <pre>VideoFormat</pre>
     * @param size
     *            The size of a video frame
     * @param maxDataLength
     *            The maximum length of a data chunk
     * @param dataType
     *            The type of data. For example, byte array
     * @param frameRate
     *            The frame rate
     */
    public VideoFormat(String encoding, Dimension size, int maxDataLength, Class<?> dataType, float frameRate) {
        this(encoding);
        if (size != null)
            this.size = new Dimension(size);
        this.maxDataLength = maxDataLength;
        this.dataType = dataType;
        this.frameRate = frameRate;
    }

    /**
     * Creates a clone of this <pre>VideoFormat</pre> by copying each field to the
     * clone.
     *
     * @return A clone of this <pre>VideoFormat</pre>
     */
    @Override
    public Object clone() {
        VideoFormat f = new VideoFormat(encoding, size, maxDataLength, dataType, frameRate);
        f.copy(this);
        return f;
    }

    /**
     * Copies the attributes from the specified <pre>Format</pre> into this
     * <pre>VideoFormat</pre>.
     *
     * @param f
     *            The <pre>Format</pre> to copy the attributes from
     */
    @Override
    protected void copy(Format f) {
        super.copy(f);
        VideoFormat vf = (VideoFormat) f;
        if (vf.size != null)
            size = new Dimension(vf.size);
        maxDataLength = vf.maxDataLength;
        frameRate = vf.frameRate;
    }

    /**
     * Compares the specified <pre>Format</pre> with this <pre>VideoFormat</pre>.
     * Returns <pre>true</pre> only if the specified <pre>Format</pre> is a
     * <pre>VideoFormat</pre> object and all of its attributes are identical to the
     * attributes in this <pre>VideoFormat</pre>.
     *
     * @param format
     *            The <pre>Format</pre> to compare
     * @return true if the specified <pre>Format</pre> is the same as this one
     */
    @Override
    public boolean equals(Object format) {
        if (format instanceof VideoFormat) {
            VideoFormat vf = (VideoFormat) format;

            if (size == null || vf.size == null) {
                if (size != vf.size)
                    return false;
            } else {
                if (!size.equals(vf.size))
                    return false;
            }

            return super.equals(format) && maxDataLength == vf.maxDataLength && frameRate == vf.frameRate;
        }
        return false;
    }

    /**
     * Gets the frame rate associated with this <pre>VideoFormat</pre>.
     *
     * @return The frame rate
     */
    public float getFrameRate() {
        return frameRate;
    }

    /**
     * Gets the length of the largest data chunk associated with this
     * <pre>VideoFormat</pre>.
     *
     * @return The maximum length of a data chunk in this <pre>VideoFormat</pre>
     */
    public int getMaxDataLength() {
        return maxDataLength;
    }

    /**
     * Gets the dimensions of a video frame in this <pre>VideoFormat</pre>.
     *
     * @return A <pre>Dimension</pre> that specifies the frame size
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * Finds the attributes shared by two matching <pre>Format</pre> objects. If the
     * specified <pre>Format</pre> does not match this one, the result is undefined.
     *
     * @param format
     *            The matching <pre>Format</pre> to intersect with this
     *            <pre>VideoFormat</pre>
     * @return A <pre>Format</pre> object with its attributes set to those attributes
     *         common to both <pre>Format</pre> objects
     * @see #matches
     */
    @Override
    public Format intersects(Format format) {
        Format fmt;
        if ((fmt = super.intersects(format)) == null)
            return null;
        if (!(format instanceof VideoFormat))
            return fmt;
        VideoFormat other = (VideoFormat) format;
        VideoFormat res = (VideoFormat) fmt;
        res.size = (size != null ? size : other.size);
        res.maxDataLength = (maxDataLength != NOT_SPECIFIED ? maxDataLength : other.maxDataLength);
        res.frameRate = (frameRate != NOT_SPECIFIED ? frameRate : other.frameRate);
        return res;
    }

    /**
     * Checks whether or not the specified <pre>Format</pre> <EM>matches</EM> this
     * <pre>VideoFormat</pre>. Matches only compares the attributes that are defined
     * in the specified <pre>Format</pre>, unspecified attributes are ignored.
     * <p>
     * The two <pre>Format</pre> objects do not have to be of the same class to match.
     * For example, if "A" are "B" are being compared, a match is possible if "A" is
     * derived from "B" or "B" is derived from "A". (The compared attributes must
     * still match, or <pre>matches</pre> fails.)
     *
     * @param format
     *            The <pre>Format</pre> to compare with this one
     * @return <pre>true</pre> if the specified <pre>Format</pre> matches this one,
     *         <pre>false</pre> if it does not
     */
    @Override
    public boolean matches(Format format) {
        if (!super.matches(format))
            return false;
        if (!(format instanceof VideoFormat))
            return true;

        VideoFormat vf = (VideoFormat) format;

        return (size == null || vf.size == null || size.equals(vf.size)) && (frameRate == NOT_SPECIFIED || vf.frameRate == NOT_SPECIFIED || frameRate == vf.frameRate);
    }

    /**
     * Generate a format that's less restrictive than this format but contains the
     * basic attributes that will make this resulting format useful for format
     * matching.
     *
     * @return A <pre>Format</pre> that's less restrictive than the this format
     */
    @Override
    public Format relax() {
        VideoFormat fmt;
        if ((fmt = (VideoFormat) super.relax()) == null)
            return null;

        fmt.size = null;
        fmt.maxDataLength = NOT_SPECIFIED;
        fmt.frameRate = NOT_SPECIFIED;

        return fmt;
    }

    /**
     * Gets a <pre>String</pre> representation of the attributes of this
     * <pre>VideoFormat</pre>. For example: "RGB, 352x240, ...".
     *
     * @return A <pre>String</pre> that describes the <pre>VideoFormat</pre> attributes
     */
    @Override
    public String toString() {
        String s = "";
        if (getEncoding() != null)
            s += getEncoding().toUpperCase();
        else
            s += "N/A";
        if (size != null)
            s += ", " + size.width + "x" + size.height;
        if (frameRate != NOT_SPECIFIED)
            s += ", FrameRate=" + ((int) (frameRate * 10) / 10f);
        if (maxDataLength != NOT_SPECIFIED)
            s += ", Length=" + maxDataLength;
        if (dataType != null && dataType != Format.byteArray)
            s += ", " + dataType;
        return s;
    }
}

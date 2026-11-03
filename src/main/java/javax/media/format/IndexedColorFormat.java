package javax.media.format;

import javax.media.Format;

import com.red5pro.media.FormatUtils;

/**
 * Standard JMF class.
 *
 * @see <a href=
 *      "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media">javax.media</a>
 *
 * @author Ken Larson
 */
public class IndexedColorFormat extends VideoFormat {

    private static final long serialVersionUID = 1122845796602684323L;

    /** Number of bytes per line of the image. */
    protected int lineStride;

    /** Red component of the color map. */
    protected byte[] redValues;

    /** Green component of the color map. */
    protected byte[] greenValues;

    /** Blue component of the color map. */
    protected byte[] blueValues;

    /** Number of entries in the color map. */
    protected int mapSize;

    private static String ENCODING = IRGB;

    static { // for Serializable compatibility.
    }

    /**
     * Creates an indexed color format.
     *
     * @param size frame dimensions
     * @param maxDataLength maximum data length
     * @param dataType class of the data
     * @param frameRate frame rate
     * @param lineStride number of bytes per line
     * @param mapSize number of entries in the color map
     * @param red red color map values
     * @param green green color map values
     * @param blue blue color map values
     */
    public IndexedColorFormat(java.awt.Dimension size, int maxDataLength, Class<?> dataType, float frameRate, int lineStride, int mapSize, byte[] red, byte[] green, byte[] blue) {
        super(ENCODING, size, maxDataLength, dataType, frameRate);
        this.lineStride = lineStride;
        this.mapSize = mapSize;
        this.redValues = red;
        this.greenValues = green;
        this.blueValues = blue;

    }

    @Override
    public Object clone() {
        return new IndexedColorFormat(FormatUtils.clone(size), maxDataLength, dataType, frameRate, lineStride, mapSize, redValues, greenValues, blueValues);
    }

    @Override
    protected void copy(Format f) {
        super.copy(f);
        final IndexedColorFormat oCast = (IndexedColorFormat) f; // it has to be
                                                                 // a
                                                                 // IndexedColorFormat,
                                                                 // or
                                                                 // ClassCastException
                                                                 // will be
                                                                 // thrown.
        this.lineStride = oCast.lineStride;
        this.mapSize = oCast.mapSize;
        this.redValues = oCast.redValues;
        this.greenValues = oCast.greenValues;
        this.blueValues = oCast.blueValues;
    }

    @Override
    public boolean equals(Object format) {
        if (!super.equals(format))
            return false;

        if (!(format instanceof IndexedColorFormat)) {
            return false;
        }

        final IndexedColorFormat oCast = (IndexedColorFormat) format;
        return this.lineStride == oCast.lineStride && this.mapSize == oCast.mapSize && this.redValues == oCast.redValues && // strange, compare arrays using ==. This makes it so that
                                                                                                                            // serializing and deserializing this obj results in an obj
                                                                                                                            // that is not "equal"
                this.greenValues == oCast.greenValues && this.blueValues == oCast.blueValues;
    }

    /**
     * Returns the blue color map values.
     *
     * @return blue values
     */
    public byte[] getBlueValues() {
        return blueValues;
    }

    /**
     * Returns the green color map values.
     *
     * @return green values
     */
    public byte[] getGreenValues() {
        return greenValues;
    }

    /**
     * Returns the line stride.
     *
     * @return number of bytes per line
     */
    public int getLineStride() {
        return lineStride;
    }

    /**
     * Returns the color map size.
     *
     * @return number of color map entries
     */
    public int getMapSize() {
        return mapSize;
    }

    /**
     * Returns the red color map values.
     *
     * @return red values
     */
    public byte[] getRedValues() {
        return redValues;
    }

    @Override
    public Format intersects(Format other) {
        final Format result = super.intersects(other);

        if (other instanceof IndexedColorFormat) {
            final IndexedColorFormat resultCast = (IndexedColorFormat) result;

            final IndexedColorFormat oCast = (IndexedColorFormat) other;
            if (getClass().isAssignableFrom(other.getClass())) {
                // "other" was cloned.

                if (FormatUtils.specified(this.lineStride))
                    resultCast.lineStride = this.lineStride;
                if (FormatUtils.specified(this.mapSize))
                    resultCast.mapSize = this.mapSize;
                if (FormatUtils.specified(this.redValues))
                    resultCast.redValues = this.redValues;
                if (FormatUtils.specified(this.greenValues))
                    resultCast.greenValues = this.greenValues;
                if (FormatUtils.specified(this.blueValues))
                    resultCast.blueValues = this.blueValues;

            } else if (other.getClass().isAssignableFrom(getClass())) { // this was cloned

                if (!FormatUtils.specified(resultCast.lineStride))
                    resultCast.lineStride = oCast.lineStride;
                if (!FormatUtils.specified(resultCast.mapSize))
                    resultCast.mapSize = oCast.mapSize;
                if (!FormatUtils.specified(resultCast.redValues))
                    resultCast.redValues = oCast.redValues;
                if (!FormatUtils.specified(resultCast.greenValues))
                    resultCast.greenValues = oCast.greenValues;
                if (!FormatUtils.specified(resultCast.blueValues))
                    resultCast.blueValues = oCast.blueValues;

            }
        }
        return result;
    }

    @Override
    public boolean matches(Format format) {
        if (!super.matches(format)) {
            return false;
        }
        if (!(format instanceof IndexedColorFormat)) {
            final boolean result = true;
            return result;
        }
        final IndexedColorFormat oCast = (IndexedColorFormat) format;
        final boolean result = FormatUtils.matches(oCast.lineStride, this.lineStride) && FormatUtils.matches(oCast.mapSize, this.mapSize) && FormatUtils.matches(oCast.redValues, this.redValues) && FormatUtils.matches(oCast.greenValues, this.greenValues) && FormatUtils.matches(oCast.blueValues, this.blueValues);
        return result;
    }

    @Override
    public Format relax() {
        final IndexedColorFormat result = (IndexedColorFormat) super.relax();
        result.lineStride = NOT_SPECIFIED;
        return result;
    }
}

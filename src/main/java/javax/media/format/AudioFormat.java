package javax.media.format;

import javax.media.Format;

import com.red5pro.media.AudioConstants;

/**
 * Encapsulates format information for audio data. The attributes of an
 * <pre>AudioFormat</pre> include the sample rate, bits per sample, and number of
 * channels.
 */
public class AudioFormat extends Format {

    private static final long serialVersionUID = -9207570564778637264L;

    public static final int BIG_ENDIAN = 1;

    public static final int LITTLE_ENDIAN = 0;

    public static final int SIGNED = 1;

    public static final int UNSIGNED = 0;

    protected double sampleRate = NOT_SPECIFIED;

    protected int sampleSizeInBits = NOT_SPECIFIED;

    protected int channels = NOT_SPECIFIED;

    protected int endian = NOT_SPECIFIED;

    protected int signed = NOT_SPECIFIED;

    protected double frameRate = NOT_SPECIFIED;

    protected int frameSizeInBits = NOT_SPECIFIED;

    /**
     * For computing the duration of the sample.
     */
    double multiplier = -1f;

    int margin = 0;

    boolean init = false;

    /**
     * Constructs an <pre>AudioFormat</pre> with the specified encoding type.
     *
     * @param encoding
     *            The audio encoding type.
     */

    public AudioFormat(String encoding) {
        super(encoding);
    }

    /**
     * Constructs an <pre>AudioFormat</pre> with the specified attributes.
     *
     * @param encoding
     *            A <pre>String</pre> that describes the encoding type for this
     *            <pre>AudioFormat</pre>.
     * @param sampleRate
     *            The sample rate.
     * @param sampleSizeInBits
     *            The sample size in bits.
     * @param channels
     *            The number of channels as an integer. For example, 1 for mono, 2
     *            for stereo.
     */
    public AudioFormat(String encoding, double sampleRate, int sampleSizeInBits, int channels) {
        this(encoding);
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channels = channels;
    }

    /**
     * Constructs an <pre>AudioFormat</pre> with the specified attributes.
     *
     * @param encoding
     *            A <pre>String</pre> that describes the encoding type for this
     *            <pre>AudioFormat</pre>.
     * @param sampleRate
     *            The sample rate.
     * @param sampleSizeInBits
     *            The sample size in bits.
     * @param channels
     *            The number of channels.
     * @param endian
     *            The sample byte ordering used for this <pre>AudioFormat</pre>--
     *            <pre>BIG_ENDIAN</pre> or <pre>LITTLE_ENDIAN</pre>.
     * @param signed
     *            Indicates whether the samples are stored in a signed or unsigned
     *            format. Specify <CITE><pre>true</pre></CITE> if the
     *            <pre>AudioFormat</pre> is signed, <pre>false</pre> if the
     *            <pre>AudioFormat</pre> is unsigned.
     */
    public AudioFormat(String encoding, double sampleRate, int sampleSizeInBits, int channels, int endian, int signed) {
        this(encoding, sampleRate, sampleSizeInBits, channels);
        this.endian = endian;
        this.signed = signed;
    }

    /**
     * Constructs an <pre>AudioFormat</pre> with the specified attributes.
     *
     * @param encoding
     *            A <pre>String</pre> that describes the encoding type for this
     *            <pre>AudioFormat</pre>.
     * @param sampleRate
     *            The sample rate.
     * @param sampleSizeInBits
     *            The sample size.
     * @param channels
     *            The number of channels.
     * @param endian
     *            The sample byte ordering used for this <pre>AudioFormat</pre>--
     *            <pre>BIG_ENDIAN</pre> or <pre>LITTLE_ENDIAN</pre>.
     * @param signed
     *            Indicates whether the samples are stored in a signed or unsigned
     *            format. Specify <CITE><pre>true</pre></CITE> if the
     *            <pre>AudioFormat</pre> is signed, <pre>false</pre> if the
     *            <pre>AudioFormat</pre> is unsigned.
     * @param frameSizeInBits
     *            The frame size.
     * @param frameRate
     *            The frame rate.
     * @param dataType
     *            The type of the data. For example, byte array.
     */
    public AudioFormat(String encoding, double sampleRate, int sampleSizeInBits, int channels, int endian, int signed, int frameSizeInBits, double frameRate, Class<?> dataType) {
        this(encoding, sampleRate, sampleSizeInBits, channels, endian, signed);
        this.frameSizeInBits = frameSizeInBits;
        this.frameRate = frameRate;
        this.dataType = dataType;
    }

    /**
     * Creates a clone of this <pre>AudioFormat</pre> by copying each field to the
     * clone.
     *
     * @return A clone of this <pre>AudioFormat</pre>.
     */
    @Override
    public Object clone() {
        AudioFormat f = new AudioFormat(encoding);
        f.copy(this);
        return f;
    }

    /**
     * Returns the duration of the media based on the given length of the data.
     *
     * @param length
     *            length of the data in this format.
     * @return the duration in nanoseconds computed from the length of the data in
     *         this format. Returns -1 if the duration cannot be computed.
     */
    public long computeDuration(long length) {
        if (init) {
            // We don't know how to compute this format
            if (multiplier < 0) {
                return -1;
            }
            return (long) ((length - margin) * multiplier) * 1000;
        }
        if (encoding == null) {
            init = true;
            return -1;
        } else if (encoding.equalsIgnoreCase(AudioConstants.LINEAR) || encoding.equalsIgnoreCase(AudioConstants.ULAW)) {
            if (sampleSizeInBits > 0 && channels > 0 && sampleRate > 0) {
                multiplier = (1000000 * 8) / sampleSizeInBits / channels / sampleRate;
            }
        } else if (encoding.equalsIgnoreCase(AudioConstants.ULAW_RTP)) {
            if (sampleSizeInBits > 0 && channels > 0 && sampleRate > 0) {
                multiplier = (1000000 * 8) / sampleSizeInBits / channels / sampleRate;
            }
        } else if (frameSizeInBits != Format.NOT_SPECIFIED && frameRate != Format.NOT_SPECIFIED) {
            // We don't know this codec, but we can compute the rate by using the frame rate
            // and size.
            if (frameSizeInBits > 0 && frameRate > 0) {
                multiplier = (1000000 * 8) / frameSizeInBits / frameRate;
            }
        }
        init = true;
        if (multiplier > 0) {
            return (long) ((length - margin) * multiplier) * 1000;
        } else {
            return -1;
        }
    }

    /**
     * Copies the attributes from the specified <pre>Format</pre> into this
     * <pre>AudioFormat</pre>.
     *
     * @param f
     *            The <pre>Format</pre> to copy the attributes from.
     */
    @Override
    protected void copy(Format f) {
        super.copy(f);
        AudioFormat other = (AudioFormat) f;
        sampleRate = other.sampleRate;
        sampleSizeInBits = other.sampleSizeInBits;
        channels = other.channels;
        endian = other.endian;
        signed = other.signed;
        frameSizeInBits = other.frameSizeInBits;
        frameRate = other.frameRate;
    }

    /**
     * Compares the specified <pre>Format</pre> with this <pre>AudioFormat</pre>.
     * Returns <pre>true</pre> only if the specified <pre>Format</pre> is an
     * <pre>AudioFormat</pre> and all of its attributes are identical to this
     * <pre>AudioFormat</pre>.
     *
     * @param format
     *            The <pre>Format</pre> to compare with this one.
     * @return <pre>true</pre> if the specified <pre>Format</pre> is the same,
     *         <pre>false</pre> if it is not.
     */
    @Override
    public boolean equals(Object format) {
        if (format instanceof AudioFormat) {
            AudioFormat other = (AudioFormat) format;
            return super.equals(format) && sampleRate == other.sampleRate && sampleSizeInBits == other.sampleSizeInBits && channels == other.channels && endian == other.endian && signed == other.signed && frameSizeInBits == other.frameSizeInBits && frameRate == other.frameRate;
        }
        return false;
    }

    /**
     * Gets the number of channels.
     *
     * @return The number of channels as an integer.
     */
    public int getChannels() {
        return channels;
    }

    /**
     * Sets the number of channels.
     *
     * @param channels
     *            The number of channels as an integer
     */
    public void setChannels(int channels) {
        this.channels = channels;
    }

    /**
     * Gets an integer that indicates whether the sample byte order is big endian or
     * little endian.
     *
     * @return The sample byte order of this <pre>AudioFormat</pre>,
     *         <pre>BIG_ENDIAN</pre> or <pre>LITTLE_ENDIAN</pre>.
     */
    public int getEndian() {
        return endian;
    }

    /**
     * Gets the frame rate of this <pre>AudioFormat</pre>.
     *
     * @return The frame rate.
     */
    public double getFrameRate() {
        return frameRate;
    }

    /**
     * Gets the frame size of this <pre>AudioFormat</pre>. This method is used
     * primarily for compressed audio.
     *
     * @return The frame size of this <pre>AudioFormat</pre> in bits.
     */
    public int getFrameSizeInBits() {
        return frameSizeInBits;
    }

    /**
     * Gets the audio sample rate.
     *
     * @return The sample rate.
     */
    public double getSampleRate() {
        return sampleRate;
    }

    /**
     * Gets the size of a sample.
     *
     * @return The sample size in bits.
     */
    public int getSampleSizeInBits() {
        return sampleSizeInBits;
    }

    /**
     * Gets a boolean that indicates whether the samples are stored in signed format
     * or an unsigned format.
     *
     * @return <pre>SIGNED</pre> if this <pre>VideoFormat</pre> is signed,
     *         <pre>UNSIGNED</pre> if it is not.
     */
    public int getSigned() {
        return signed;
    }

    /**
     * Finds the attributes shared by two matching <pre>Format</pre> objects. If the
     * specified <pre>Format</pre> does not match this one, the result is undefined.
     *
     * @param format
     *            The matching <pre>Format</pre> to intersect with this
     *            <pre>AudioFormat</pre>.
     * @return A <pre>Format</pre> object with its attributes set to those attributes
     *         common to both <pre>Format</pre> objects.
     * @see #matches
     */
    @Override
    public Format intersects(Format format) {
        Format fmt;
        if ((fmt = super.intersects(format)) == null)
            return null;
        if (!(fmt instanceof AudioFormat))
            return fmt;
        AudioFormat other = (AudioFormat) format;
        AudioFormat res = (AudioFormat) fmt;
        res.sampleRate = (sampleRate != NOT_SPECIFIED ? sampleRate : other.sampleRate);
        res.sampleSizeInBits = (sampleSizeInBits != NOT_SPECIFIED ? sampleSizeInBits : other.sampleSizeInBits);
        res.channels = (channels != NOT_SPECIFIED ? channels : other.channels);
        res.endian = (endian != NOT_SPECIFIED ? endian : other.endian);
        res.signed = (signed != NOT_SPECIFIED ? signed : other.signed);
        res.frameSizeInBits = (frameSizeInBits != NOT_SPECIFIED ? frameSizeInBits : other.frameSizeInBits);
        res.frameRate = (frameRate != NOT_SPECIFIED ? frameRate : other.frameRate);
        return res;
    }

    /**
     * Checks whether or not the specified <pre>Format</pre> <EM>matches</EM> this
     * <pre>AudioFormat</pre>. Matches only compares the attributes that are defined
     * in the specified <pre>Format</pre>, unspecified attributes are ignored.
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
    @Override
    public boolean matches(Format format) {
        if (!super.matches(format))
            return false;
        if (!(format instanceof AudioFormat))
            return true;

        AudioFormat other = (AudioFormat) format;

        return (sampleRate == NOT_SPECIFIED || other.sampleRate == NOT_SPECIFIED || sampleRate == other.sampleRate) && (sampleSizeInBits == NOT_SPECIFIED || other.sampleSizeInBits == NOT_SPECIFIED || sampleSizeInBits == other.sampleSizeInBits) && (channels == NOT_SPECIFIED || other.channels == NOT_SPECIFIED || channels == other.channels)
                && (endian == NOT_SPECIFIED || other.endian == NOT_SPECIFIED || endian == other.endian) && (signed == NOT_SPECIFIED || other.signed == NOT_SPECIFIED || signed == other.signed) && (frameSizeInBits == NOT_SPECIFIED || other.frameSizeInBits == NOT_SPECIFIED || frameSizeInBits == other.frameSizeInBits) && (frameRate == NOT_SPECIFIED || other.frameRate == NOT_SPECIFIED || frameRate == other.frameRate);
    }

    /**
     * Gets a <pre>String</pre> representation of the attributes of this
     * <pre>AudioFormat</pre>. For example: "PCM, 44.1 KHz, Stereo, Signed".
     *
     * @return A <pre>String</pre> that describes the <pre>AudioFormat</pre> attributes.
     */
    @Override
    public String toString() {
        String strChannels = "";
        String strEndian = "";

        if (channels == 1)
            strChannels = ", Mono";
        else if (channels == 2)
            strChannels = ", Stereo";
        else if (channels != NOT_SPECIFIED)
            strChannels = ", " + channels + "-channel";

        if (sampleSizeInBits > 8) {
            if (endian == BIG_ENDIAN)
                strEndian = ", BigEndian";
            else if (endian == LITTLE_ENDIAN)
                strEndian = ", LittleEndian";
        }

        return getEncoding() + ((sampleRate != NOT_SPECIFIED) ? (", " + sampleRate + " Hz") : ", Unknown Sample Rate") + ((sampleSizeInBits != NOT_SPECIFIED) ? (", " + sampleSizeInBits + "-bit") : "") + strChannels + strEndian + ((signed != NOT_SPECIFIED) ? ((signed == SIGNED ? ", Signed" : ", Unsigned")) : "") + ((frameRate != NOT_SPECIFIED) ? (", " + frameRate + " frame rate") : "")
                + ((frameSizeInBits != NOT_SPECIFIED) ? (", FrameSize=" + frameSizeInBits + " bits") : "") + ((dataType != Format.byteArray && dataType != null) ? ", " + dataType : "");
    }

}

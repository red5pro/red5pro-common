package com.red5pro.media;

import java.awt.Dimension;
import java.util.Map;

import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;

/**
 * Utilities to help implement Format subclasses. Cannot be part of Format class
 * because then serialization becomes incompatible with reference impl.
 *
 * @author Ken Larson
 */
public class FormatUtils {

    public static final Class<?> byteArray = byte[].class;

    public static final Class<?> shortArray = short[].class;

    public static final Class<?> intArray = int[].class;

    public static final Class<?> formatArray = Format[].class;

    // here to avoid messing up the serialization signature in the format classes.
    // The Eclipse compiler
    // will insert anonymous fields for these:
    public static final Class<?> videoFormatClass = VideoFormat.class;

    public static final Class<?> audioFormatClass = AudioFormat.class;

    public static boolean byteArraysEqual(byte[] ba1, byte[] ba2) {
        if (ba1 == null && ba2 == null)
            return true;
        if (ba1 == null || ba2 == null)
            return false;

        if (ba1.length != ba2.length)
            return false;
        for (int i = 0; i < ba1.length; ++i) {
            if (ba1[i] != ba2[i])
                return false;
        }
        return true;
    }

    private static int charEncodingCodeVal(char c) {
        if (c <= (char) 95)
            return c - 32;
        if (c == 96)
            return -1;
        if (c <= 122)
            return c - 64;
        if (c <= 127)
            return -1;
        if (c <= 191)
            return -94;
        if (c <= 255)
            return -93;

        return -1;
    }

    public static Dimension clone(Dimension d) {
        if (d == null)
            return null;
        return new Dimension(d);
    }

    public static String frameRateToString(float frameRate) {
        // hack to get frame rates to print out same as JMF: 1 decimal place, but NO
        // rounding.
        frameRate = (((long) (frameRate * 10))) / 10.f;
        String s = "" + frameRate;
        return s;
    }

    public static boolean isOneAssignableFromTheOther(Class<?> a, Class<?> b) {
        return a == b || b.isAssignableFrom(a) || a.isAssignableFrom(b);
    }

    /**
     * Is a a subclass of b? Strict.
     * 
     * @param a
     *            class a
     * @param b
     *            class b
     * @return true if subclass and false otherwise
     */
    public static boolean isSubclass(Class<?> a, Class<?> b) {
        if (a == b)
            return false;
        if (!(b.isAssignableFrom(a)))
            return false;
        return true;
    }

    public static boolean matches(double v1, double v2) {
        if (v1 == Format.NOT_SPECIFIED || v2 == Format.NOT_SPECIFIED)
            return true;
        return v1 == v2;
    }

    public static boolean matches(float v1, float v2) {
        if (v1 == Format.NOT_SPECIFIED || v2 == Format.NOT_SPECIFIED)
            return true;
        return v1 == v2;
    }

    public static boolean matches(int v1, int v2) {
        if (v1 == Format.NOT_SPECIFIED || v2 == Format.NOT_SPECIFIED)
            return true;
        return v1 == v2;
    }

    public static boolean matches(Object o1, Object o2) {
        if (o1 == null || o2 == null)
            return true;
        return o1.equals(o2);
    }

    public static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == null && o2 == null)
            return true;
        if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);
    }

    public static boolean nullSafeEqualsIgnoreCase(String o1, String o2) {
        if (o1 == null && o2 == null)
            return true;
        if (o1 == null || o2 == null)
            return false;
        return o1.equalsIgnoreCase(o2);
    }

    public static boolean specified(double v) {
        return v != Format.NOT_SPECIFIED;
    }

    public static boolean specified(float v) {
        return v != Format.NOT_SPECIFIED;
    }

    public static boolean specified(int v) {
        return v != Format.NOT_SPECIFIED;
    }

    public static boolean specified(Object o) {
        return o != null;
    }

    public static long stringEncodingCodeVal(String s) {
        long result = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            result *= 64;
            result += charEncodingCodeVal(c);

        }
        return result;
    }

    /**
     * Determines whether a specific set of format parameters is equal to another
     * set of format parameters in the sense that they define an equal number of
     * parameters and assign them equal values. Since the values are Strings,
     * presumes that a value of <pre>null</pre> is equal to the empty String. <br>
     * The two <pre>Map</pre> instances of format parameters to be checked for
     * equality are presumed to be modifiable in the sense that if the lack of a
     * format parameter in a given <pre>Map</pre> is equivalent to it having a
     * specific value, an association of the format parameter to the value in
     * question may be added to or removed from the respective <pre>Map</pre> instance
     * for the purposes of determining equality. <br>
     * 
     * @param encoding
     *            the encoding (name) related to the two sets of format parameters
     *            to be tested for equality
     * @param fmtps1
     *            the first set of format parameters to be tested for equality
     * @param fmtps2
     *            the second set of format parameters to be tested for equality
     * @return <pre>true</pre> if the specified sets of format parameters are equal;
     *         <pre>false</pre>, otherwise
     */
    public static boolean formatParametersAreEqual(String encoding, Map<String, String> fmtps1, Map<String, String> fmtps2) {
        if (fmtps1 == null)
            return (fmtps2 == null) || fmtps2.isEmpty();
        if (fmtps2 == null)
            return (fmtps1 == null) || fmtps1.isEmpty();
        if (fmtps1.size() == fmtps2.size()) {
            for (Map.Entry<String, String> fmtp1 : fmtps1.entrySet()) {
                String key1 = fmtp1.getKey();
                if (!fmtps2.containsKey(key1)) {
                    return false;
                }
                String value1 = fmtp1.getValue();
                String value2 = fmtps2.get(key1);
                // Since the values are strings, allow null to be equal to the empty string.
                if ((value1 == null) || (value1.length() == 0)) {
                    if ((value2 != null) && (value2.length() != 0)) {
                        return false;
                    }
                } else if (!value1.equals(value2)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

}

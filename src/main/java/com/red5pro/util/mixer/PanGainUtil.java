package com.red5pro.util.mixer;

/**
 * Util for pan/gain calculations.
 * <p/>
 * These calculations are here in he Java side for use at the API border. The intent is to avoid these
 * calculations during the native mix, and only recalculate when settings change.
 *
 * @author Nate Roe
 */
public class PanGainUtil {
    /**
     * Convert the gain in decibels to amplitude factor.
     *
     * http://www.sengpielaudio.com/calculator-FactorRatioLevelDecibel.htm
     *
     * @param decibels
     * @return
     */
    public static double gainToFactor(double decibels) {
        return Math.pow(10.0, decibels * 0.05); // decibels * 0.05 == decibels / 20.0
    }

    /**
     * Convert the given amplitude factor to gain in decibels.
     *
     * @param factor
     * @return
     */
    public static double factorToGain(double factor) {
        return 20.0 * Math.log10(factor);
    }

    /**
     * @param panSetting
     *            from -100 to 100
     * @return
     */
    public static double leftFactor(double panSetting) {
        return Math.min(1.0, (100.0 - panSetting) / 100.0);
    }

    public static double rightFactor(double panSetting) {
        return Math.min(1.0, (panSetting + 100.0) / 100.0);
    }
}

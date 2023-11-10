package com.red5pro.mixer.nodes.video;

import com.red5pro.mixer.nodes.AbstractNode;

/**
 * Draw a solid field of color. Values range 0-255.
 *
 * @author Nate Roe
 */
public class SolidColorNode extends AbstractNode {
    private float red;

    private float green;

    private float blue;

    private float alpha;

    public SolidColorNode() {
        super();

        red = 0.0f;
        green = 0.0f;
        blue = 0.0f;
        alpha = 1.0f;
    }

    public SolidColorNode(float red, float green, float blue, float alpha) {
        super();
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public SolidColorNode(byte red, byte green, byte blue, byte alpha) {
        super();
        this.red = (float) red / 255.0f;
        this.green = (float) green / 255.0f;
        this.blue = (float) blue / 255.0f;
        this.alpha = (float) alpha / 255.0f;
    }

    public Float getRed() {
        return red;
    }

    public void setRed(Float red) {
        this.red = red;
    }

    public Float getGreen() {
        return green;
    }

    public void setGreen(Float green) {
        this.green = green;
    }

    public Float getBlue() {
        return blue;
    }

    public void setBlue(Float blue) {
        this.blue = blue;
    }

    public Float getAlpha() {
        return alpha;
    }

    public void setAlpha(Float alpha) {
        this.alpha = alpha;
    }
}

package com.red5pro.mixer.nodes.audio;

import com.red5pro.mixer.nodes.AbstractNode;
import com.red5pro.mixer.nodes.ISourceNode;
import com.red5pro.util.mixer.PanGainUtil;

/**
 * A source of audio data, a video stream.
 * The streamGuid includes its entire context path and the stream name.
 *
 * @author Nate Roe
 */
public class AudioSourceNode extends AbstractNode implements ISourceNode {
    private String streamGuid;

    // -100 to 100 (left to right)
    private Float pan;

    // -100 to 0 (dB)
    private Float gain;

    // accessed by native code
    @SuppressWarnings("unused")
    private transient float ampFactorL;

    // accessed by native code
    @SuppressWarnings("unused")
    private transient float ampFactorR;

    public AudioSourceNode() {
        ampFactorL = ampFactorR = 1.0f;
    }

    public AudioSourceNode(String streamGuid) {
        this.streamGuid = streamGuid;
        ampFactorL = ampFactorR = 1.0f;
    }

    @Override
    public String getStreamGuid() {
        return streamGuid;
    }

    public void setStreamGuid(String streamGuid) {
        this.streamGuid = streamGuid;
    }

    public Float getPan() {
        return pan;
    }

    public void setPan(Float pan) {
        this.pan = pan;
    }

    public Float getGain() {
        return gain;
    }

    public void setGain(Float gain) {
        this.gain = gain;
    }

    // called from native code during prepareNodeGraph(...)
    @SuppressWarnings("unused")
    private void calculate() {
        if (pan != null && gain != null) {
            ampFactorL = (float) (PanGainUtil.leftFactor(pan) * PanGainUtil.gainToFactor(gain));
            ampFactorR = (float) (PanGainUtil.rightFactor(pan) * PanGainUtil.gainToFactor(gain));
        } else {
            ampFactorL = ampFactorR = 1.0f;
        }
    }
}

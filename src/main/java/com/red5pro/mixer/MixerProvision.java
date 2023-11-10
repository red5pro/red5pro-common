package com.red5pro.mixer;

import java.awt.Dimension;

public class MixerProvision {
    private String event;

    private String path;

    private String streamName;

    private String originIp;

    private int port;

    private boolean doForward;

    private String digest;

    private Dimension frameSize;

    private int framerate;

    private int bitrate;

    private int qpMin;

    private int qpMax;

    private int maxBitrate;

    private int sampleRate;

    private int audioChannels;

    private int submixes;

    public MixerProvision() {
    }

    public MixerProvision(String event, String path, String streamName, String originIp, int port, boolean doForward, String digest, Dimension frameSize, int framerate, int bitrate, int qpMin, int qpMax, int maxBitrate, int sampleRate, int audioChannels, int submixes) {
        this.event = event;
        this.path = path;
        this.streamName = streamName;
        this.originIp = originIp;
        this.port = port;
        this.doForward = doForward;
        this.digest = digest;
        this.frameSize = frameSize;
        this.framerate = framerate;
        this.bitrate = bitrate;
        this.qpMin = qpMin;
        this.qpMax = qpMax;
        this.maxBitrate = maxBitrate;
        this.sampleRate = sampleRate;
        this.audioChannels = audioChannels;
        this.submixes = submixes;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getOriginIp() {
        return originIp;
    }

    public void setOriginIp(String originIp) {
        this.originIp = originIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isDoForward() {
        return doForward;
    }

    public void setDoForward(boolean doForward) {
        this.doForward = doForward;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Dimension getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(Dimension frameSize) {
        this.frameSize = frameSize;
    }

    public int getFramerate() {
        return framerate;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getQpMin() {
        return qpMin;
    }

    public void setQpMin(int qpMin) {
        this.qpMin = qpMin;
    }

    public int getQpMax() {
        return qpMax;
    }

    public void setQpMax(int qpMax) {
        this.qpMax = qpMax;
    }

    public int getMaxBitrate() {
        return maxBitrate;
    }

    public void setMaxBitrate(int maxBitrate) {
        this.maxBitrate = maxBitrate;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getAudioChannels() {
        return audioChannels;
    }

    public void setAudioChannels(int audioChannels) {
        this.audioChannels = audioChannels;
    }

    public int getSubmixes() {
        return submixes;
    }

    public void setSubmixes(int submixes) {
        this.submixes = submixes;
    }
}

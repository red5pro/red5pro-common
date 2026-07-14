package com.red5pro.media;

import com.red5pro.group.IParticipant;

/**
 * Container for multiple MediaSample instances. This is meant to contain one or
 * more MediaSamples of the same instant, segment, frame, slice...
 *
 * @author Paul Gregoire
 *
 */
public class CompositeMediaSample extends MediaSampleAdapter {

    private IMediaSample[] samples;

    private int trackNum;

    public IMediaSample[] getSamples() {
        return samples;
    }

    public void setSamples(IMediaSample[] samples) {
        this.samples = samples;
    }

    public int size() {
        return samples.length;
    }

    private IParticipant source;

    /**
     * Returns a MediaSample matching the FourCC given or null if not found; this
     * does not remove the sample.
     *
     * @param fourCC
     * @return MediaSample or null if not found
     */
    public IMediaSample get(int fourCC) {
        for (IMediaSample sample : samples) {
            if (sample.getFourCC() == fourCC) {
                return sample;
            }
        }
        return null;
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    public int getTrackNum() {
        return trackNum;
    }

    @Override
    public void setTrackNum(int id) {
        trackNum = id;
    }

    public IParticipant getSource() {
        return source;
    }

    public void setSource(IParticipant source) {
        this.source = source;
    }
}

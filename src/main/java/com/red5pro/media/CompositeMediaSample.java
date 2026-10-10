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

    /** Creates an empty composite sample. */
    public CompositeMediaSample() {
    }

    private IMediaSample[] samples;

    private int trackNum;

    /** Returns the contained samples. */
    public IMediaSample[] getSamples() {
        return samples;
    }

    /** Sets the contained samples. */
    public void setSamples(IMediaSample[] samples) {
        this.samples = samples;
    }

    /** Returns the number of contained samples. */
    public int size() {
        return samples.length;
    }

    private IParticipant source;

    /**
     * Returns a MediaSample matching the FourCC given or null if not found; this
     * does not remove the sample.
     *
     * @param fourCC media FourCC identifier
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
    /** {@inheritDoc} */
    public boolean isComposite() {
        return true;
    }

    @Override
    /** {@inheritDoc} */
    public int getTrackNum() {
        return trackNum;
    }

    @Override
    /** {@inheritDoc} */
    public void setTrackNum(int id) {
        trackNum = id;
    }

    /** Returns the source participant. */
    public IParticipant getSource() {
        return source;
    }

    /** Sets the source participant. */
    public void setSource(IParticipant source) {
        this.source = source;
    }
}

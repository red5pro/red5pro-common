package com.red5pro.media;

import javax.media.Buffer;

/**
 * Adapter for MediaSample implementations.
 * 
 * @author Paul Gregoire
 *
 */
public class MediaSampleAdapter implements IMediaSample {

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public boolean isCritical() {
        return false;
    }

    @Override
    public void setBuffer(Object buffer) {
    }

    @Override
    public Object getBuffer() {
        return null;
    }

    @Override
    public boolean hasBuffer() {
        return false;
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void setDecoded(boolean decoded) {
    }

    @Override
    public boolean isDecoded() {
        return false;
    }

    @Override
    public void setEncoding(String encoding) {
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public void setFlags(int flags) {
    }

    @Override
    public int getFlags() {
        return 0;
    }

    @Override
    public boolean isKeyframe() {
        return false;
    }

    @Override
    public void setSequenceNumber(long sequenceNumber) {
    }

    @Override
    public long getSequenceNumber() {
        return 0;
    }

    @Override
    public Buffer toBuffer() {
        return null;
    }

    @Override
    public Buffer toBuffer(boolean stripRtmp) {
        return null;
    }

    @Override
    public byte[] bufferAsBytes() {
        return null;
    }

    @Override
    public short[] bufferAsShorts() {
        return null;
    }

    @Override
    public void setTimestamp(long startTime) {
        // TODO Auto-generated method stub

    }

    @Override
    public long getTimestamp() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getSourceName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTrackNum() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setTrackNum(int id) {
        // TODO Auto-generated method stub

    }

    @Override
    public FourCC getFourCC() {
        return null;
    }

    @Override
    public FourCC getContainer() {
        // TODO Auto-generated method stub
        return null;
    }

}

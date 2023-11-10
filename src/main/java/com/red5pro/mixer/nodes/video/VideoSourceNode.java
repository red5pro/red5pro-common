package com.red5pro.mixer.nodes.video;

import com.red5pro.mixer.nodes.AbstractNode;

/**
 * A video source, using the latest from from the given stream.
 * The streamGuid includes its entire context path and the stream name.
 * The rectangle of pixels from the source frame buffer
 * at (sourceX, sourceY) to (sourceX + sourceWidth, sourceY + sourceHeight)
 * will be drawn (cropped/scaled as necessary, preserving original aspect)
 * to the rectangle at (destX, destY) to (destX + destWidth, destY + destHeight).
 *
 * @author Nate Roe
 */
public class VideoSourceNode extends AbstractNode {
    private String streamGuid;

    private Integer sourceX;

    private Integer sourceY;

    private Integer sourceWidth;

    private Integer sourceHeight;

    private Integer destX;

    private Integer destY;

    private Integer destWidth;

    private Integer destHeight;

    public VideoSourceNode() {
        super();
    }

    public String getStreamGuid() {
        return streamGuid;
    }

    public void setStreamGuid(String streamGuid) {
        this.streamGuid = streamGuid;
    }

    public Integer getSourceX() {
        return sourceX;
    }

    public void setSourceX(Integer sourceX) {
        this.sourceX = sourceX;
    }

    public Integer getSourceY() {
        return sourceY;
    }

    public void setSourceY(Integer sourceY) {
        this.sourceY = sourceY;
    }

    public Integer getSourceWidth() {
        return sourceWidth;
    }

    public void setSourceWidth(Integer sourceWidth) {
        this.sourceWidth = sourceWidth;
    }

    public Integer getSourceHeight() {
        return sourceHeight;
    }

    public void setSourceHeight(Integer sourceHeight) {
        this.sourceHeight = sourceHeight;
    }

    public Integer getDestX() {
        return destX;
    }

    public void setDestX(Integer destX) {
        this.destX = destX;
    }

    public Integer getDestY() {
        return destY;
    }

    public void setDestY(Integer destY) {
        this.destY = destY;
    }

    public Integer getDestWidth() {
        return destWidth;
    }

    public void setDestWidth(Integer destWidth) {
        this.destWidth = destWidth;
    }

    public Integer getDestHeight() {
        return destHeight;
    }

    public void setDestHeight(Integer destHeight) {
        this.destHeight = destHeight;
    }
}

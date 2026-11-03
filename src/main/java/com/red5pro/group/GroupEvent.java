package com.red5pro.group;

import org.red5.server.api.event.IEvent;
import org.red5.server.api.event.IEventListener;

import com.red5pro.media.FourCC;

/**
 * Model for events occurring within a group.
 *
 * @author Paul Gregoire
 *
 */
public class GroupEvent implements IEvent {

    // STREAM_DATA for media
    // STREAM_ACTION for stream actions
    // SYSTEM for general actions
    private final Type type;

    /**
     * Event source. Originator / owner identifier can be retrieved here as
     * IParticipant.getId()
     */
    private final IEventListener source;

    /**
     * Event target object. If an event carries more than one version of data, use
     * CompositeMediaSample.
     */
    private final Object data;

    /**
     * Event FourCC designation. This can be transport, container, media type,
     * etc...
     */
    private final FourCC fourCC;

    /**
     * Event timestamp.
     */
    private final long timestamp;

    private GroupEvent(Type type, IEventListener source, Object data) {
        this.type = type;
        this.source = source;
        this.data = data;
        this.fourCC = FourCC.UNDEFINED;
        this.timestamp = System.currentTimeMillis();
    }

    private GroupEvent(Type type, IEventListener source, Object data, FourCC fourCC) {
        this.type = type;
        this.source = source;
        this.data = data;
        this.fourCC = fourCC;
        this.timestamp = System.currentTimeMillis();
    }

    private GroupEvent(Type type, Object data, FourCC fourCC, String sourceId) {
        this.type = type;
        this.source = null;
        this.data = data;
        this.fourCC = fourCC;
        this.timestamp = System.currentTimeMillis();
    }

    private GroupEvent(Type type, IEventListener source, Object data, FourCC fourCC, long timestamp) {
        this.type = type;
        this.source = source;
        this.data = data;
        this.fourCC = fourCC;
        this.timestamp = timestamp;
    }

    private GroupEvent(Type type, Object data, FourCC fourCC, long timestamp, String sourceId) {
        this.type = type;
        this.source = null;
        this.data = data;
        this.fourCC = fourCC;
        this.timestamp = timestamp;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Object getObject() {
        // this will represent the encapsulated data content
        return data;
    }

    @Override
    public boolean hasSource() {
        return source != null;
    }

    /**
     * Returns the FourCC designation of the event data (transport, container or media type).
     *
     * @return the FourCC
     */
    public FourCC getFourCC() {
        return fourCC;
    }

    @Override
    public IEventListener getSource() {
        return source;
    }
    // /**
    // * PublisherId or iparticipant id of source.
    // * @return
    // */
    // public String getSourceId() {
    // return sourceId;
    // }

    /**
     * Returns the event timestamp.
     *
     * @return creation time in milliseconds, or the explicitly supplied timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fourCC == null) ? 0 : fourCC.hashCode());
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GroupEvent other = (GroupEvent) obj;
        if (fourCC != other.fourCC)
            return false;
        if (timestamp != other.timestamp)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GroupEvent [type=" + type + ", fourCC=" + fourCC + ", timestamp=" + timestamp + ", source=" + source + ", data=" + data + "]";
    }

    /**
     * Builds an event with no source and undefined FourCC.
     *
     * @param type the event type
     * @param data the event payload
     * @return the new event
     */
    public static GroupEvent build(Type type, Object data) {
        return new GroupEvent(type, null, data);
    }

    /**
     * Builds a STREAM_DATA event with no source.
     *
     * @param data the event payload
     * @param fourCC the data FourCC
     * @return the new event
     */
    public static GroupEvent build(Object data, FourCC fourCC) {
        return new GroupEvent(Type.STREAM_DATA, null, data, fourCC);
    }

    /**
     * Builds a STREAM_DATA event with no source; the source id is currently not retained.
     *
     * @param data the event payload
     * @param fourCC the data FourCC
     * @param sourceId the id of the originating publisher
     * @return the new event
     */
    public static GroupEvent build(Object data, FourCC fourCC, String sourceId) {
        return new GroupEvent(Type.STREAM_DATA, data, fourCC, sourceId);
    }

    /**
     * Builds an event with the given source and undefined FourCC.
     *
     * @param type the event type
     * @param source the originator of the event
     * @param data the event payload
     * @return the new event
     */
    public static GroupEvent build(Type type, IEventListener source, Object data) {
        return new GroupEvent(type, source, data);
    }

    /**
     * Builds an event with the given source and FourCC.
     *
     * @param type the event type
     * @param source the originator of the event
     * @param data the event payload
     * @param fourCC the data FourCC
     * @return the new event
     */
    public static GroupEvent build(Type type, IEventListener source, Object data, FourCC fourCC) {
        return new GroupEvent(type, source, data, fourCC);
    }

    /**
     * Builds a STREAM_DATA event with the given source.
     *
     * @param source the originator of the event
     * @param data the event payload
     * @param fourCC the data FourCC
     * @return the new event
     */
    public static GroupEvent build(IEventListener source, Object data, FourCC fourCC) {
        return new GroupEvent(Type.STREAM_DATA, source, data, fourCC);
    }

    /**
     * Builds an event with an explicit timestamp.
     *
     * @param type the event type
     * @param source the originator of the event
     * @param data the event payload
     * @param fourCC the data FourCC
     * @param timestamp the event timestamp in milliseconds
     * @return the new event
     */
    public static GroupEvent build(Type type, IEventListener source, Object data, FourCC fourCC, long timestamp) {
        return new GroupEvent(type, source, data, fourCC, timestamp);
    }

    /**
     * Builds a STREAM_DATA event with an explicit timestamp.
     *
     * @param source the originator of the event
     * @param data the event payload
     * @param fourCC the data FourCC
     * @param timestamp the event timestamp in milliseconds
     * @return the new event
     */
    public static GroupEvent build(IEventListener source, Object data, FourCC fourCC, long timestamp) {
        return new GroupEvent(Type.STREAM_DATA, source, data, fourCC, timestamp);
    }

    /**
     * Builds a STREAM_DATA event with no source and an explicit timestamp; the source id is currently not retained.
     *
     * @param data the event payload
     * @param fourCC the data FourCC
     * @param timestamp the event timestamp in milliseconds
     * @param sourceId the id of the originating publisher
     * @return the new event
     */
    public static GroupEvent build(Object data, FourCC fourCC, long timestamp, String sourceId) {
        return new GroupEvent(Type.STREAM_DATA, data, fourCC, timestamp, sourceId);
    }

}

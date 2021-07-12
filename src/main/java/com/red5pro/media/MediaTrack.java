package com.red5pro.media;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

import org.red5.server.api.event.IEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.group.GroupEvent;
import com.red5pro.util.IdGenerator;

/**
 * A source or sink for media. Designed to be similar to a JMF Track.
 * 
 * @author Paul Gregoire
 *
 */
public class MediaTrack {

    private static Logger log = LoggerFactory.getLogger(MediaTrack.class);

    // type of media in this track
    protected final MediaType type;

    // fourcc code for the media in the track
    protected final FourCC fourCC;

    // string identifier for this track
    protected final String id;

    // track listeners
    protected final CopyOnWriteArraySet<MediaTrackListener> listeners = new CopyOnWriteArraySet<>();

    // track events in order of addition to the deque
    protected final ConcurrentLinkedDeque<IEvent> events = new ConcurrentLinkedDeque<>();

    // clock rate for audio, video, or other
    protected int clockRate;

    public MediaTrack(MediaType type, FourCC fourCC) {
        this(type, fourCC, String.format("%s%d", type.name().toLowerCase(), IdGenerator.generateId(8)));
    }

    public MediaTrack(MediaType type, FourCC fourCC, String id) {
        this.type = type;
        this.fourCC = fourCC;
        this.id = id;
        if (type.equals(MediaType.AUDIO)) {
            // most likely value for any audio pushed in
            this.clockRate = 48000;
        } else {
            // value to base everything else on, video, meta, etc...
            this.clockRate = 90000;
        }
    }

    /**
     * Pushes an event to the tail of the track.
     * 
     * @param event
     * @return true if successfully pushed and false otherwise
     */
    public boolean push(IEvent event) {
        return events.add(event);
    }

    /**
     * Pops an event off the track.
     * 
     * @return event at the head of the track or null if empty
     */
    public IEvent pop() {
        return events.pop();
    }

    /**
     * Pops an event off the track matching the given fourcc code.
     * 
     * @param fourCC
     *            to match
     * @return IEvent or null if no match is found
     */
    public IEvent pop(FourCC fourCC) {
        for (IEvent event : events) {
            if (fourCC.equals(((GroupEvent) event).getFourCC())) {
                log.debug("Found target event: {}", fourCC);
                if (events.remove(event)) {
                    return event;
                }
            }
        }
        return null;
    }

    /**
     * Adds a track listener.
     * 
     * @param listener
     * @return true if added and false otherwise
     */
    public boolean addListener(MediaTrackListener listener) {
        return listeners.add(listener);
    }

    /**
     * Removes an existing track listener.
     * 
     * @param listener
     * @return true if removed and false otherwise
     */
    public boolean removeListener(MediaTrackListener listener) {
        return listeners.remove(listener);
    }

    public MediaType getType() {
        return type;
    }

    public FourCC getFourCC() {
        return fourCC;
    }

    public String getId() {
        return id;
    }

    public CopyOnWriteArraySet<MediaTrackListener> getListeners() {
        return listeners;
    }

    public int getClockRate() {
        return clockRate;
    }

    public void setClockRate(int clockRate) {
        this.clockRate = clockRate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + fourCC.hashCode();
        result = prime * result + type.hashCode();
        result = prime * result + id.hashCode();
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
        MediaTrack other = (MediaTrack) obj;
        if (fourCC != other.fourCC)
            return false;
        if (type != other.type)
            return false;
        if (id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MediaTrack [id=" + id + ", type=" + type + ", fourCC=" + fourCC + ", listeners=" + listeners.size() + "]";
    }

    // predicate for audio tracks
    public final static Predicate<MediaTrack> isAudioTrack() {
        return p -> (p.getType() == MediaType.AUDIO);
    }

    // predicate for video tracks
    public final static Predicate<MediaTrack> isVideoTrack() {
        return p -> (p.getType() == MediaType.VIDEO);
    }

}

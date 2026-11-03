package com.red5pro.media.sdp.model;

/**
 * The timing "t=" lines specify the start and stop times for a session.
 * Multiple "t=" lines MAY be used if a session is active at multiple
 * irregularly spaced times; each additional "t=" line specifies an additional
 * period of time for which the session will be active. If the session is active
 * at regular times, an "r=" line (see below) should be used in addition to, and
 * following, a "t=" line -- in which case the "t=" line specifies the start and
 * stop times of the repeat sequence.
 *
 * <pre>
      t=&lt;start-time&gt; &lt;stop-time&gt;
 * </pre>
 *
 * @author Paul Gregoire
 */
public class TimingField {

    /**
     * Creates a timing field with start and stop times of zero.
     */
    public TimingField() {
    }

    private long start = 0l;

    private long stop = 0l;

    /**
     * Returns the session start time.
     *
     * @return start time, 0 if unbounded
     */
    public long getStart() {
        return start;
    }

    /**
     * Sets the session start time.
     *
     * @param start start time
     */
    public void setStart(long start) {
        this.start = start;
    }

    /**
     * Returns the session stop time.
     *
     * @return stop time, 0 if unbounded
     */
    public long getStop() {
        return stop;
    }

    /**
     * Sets the session stop time.
     *
     * @param stop stop time
     */
    public void setStop(long stop) {
        this.stop = stop;
    }

    @Override
    public String toString() {
        return String.format("t=%d %d\n", start, stop);
    }

}

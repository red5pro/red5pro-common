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
      t=<start-time> <stop-time>
 * </pre>
 *
 * @author Paul Gregoire
 */
public class TimingField {

    private long start = 0l;

    private long stop = 0l;

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }

    @Override
    public String toString() {
        return String.format("t=%d %d\n", start, stop);
    }

}

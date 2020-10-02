package com.red5pro.interstitial.api;

/**
 * This class determines when an interstitial is activated and deactivated via timestamp input.
 * @author Andy
 *
 */
public class InterstitialDurationControl {
    private InterstitialDurationControlType lifeCycle;

    private long start;

    private long duration;

    private boolean loop;

    private boolean paused;

    /**
     * 
     * @param type
     * @param duration
     * @param utcStart
     * @param streamStart
     */
    public InterstitialDurationControl(InterstitialDurationControlType type, long duration, long streamStart) {
        this.lifeCycle = type;
        paused = true;
        this.duration = duration;
        this.start = streamStart;
    }

    /**
     * Call this to resume program at any time.
     * Indefinite sessions use this to resume via RPC.
     * StreamClock and WallClock sessions can override scheduled time to resume earlier. 
     */
    public void resumeProgram() {
        paused = false;
    }

    /**
     * Called to check if its time to start.
     * @param streamTime current timestamp of stream.
     * @return true if stream time or wall clock is equal or greater than start time.
     */
    public boolean startNow(long streamTime) {
        switch (lifeCycle) {
            case StreamClock:
                return streamTime >= start;
            case Indefinite:
                return paused;
            case WallClock:
                return System.currentTimeMillis() >= start;
        }
        return false;
    }

    /**
     * Called after activation. Returns true if session is still active according to start and duration.
     * @param streamTime current stream time in milliseconds.
     * @return true if stream time or wall-clock is less than start plus duration.
     */
    public boolean isActive(long streamTime) {
        if (!paused) {
            //api overide via RPC.
            return false;
        }

        switch (lifeCycle) {
            case StreamClock:
                return streamTime > (start + duration) ? false : true;
            case Indefinite:
                return paused;
            case WallClock:
                return System.currentTimeMillis() - start < duration;
        }

        return false;
    }

    /**
     * Milliseconds
     * @param streamTimeStart Milliseconds
     */
    public void setStart(long streamTimeStart) {
        this.start = streamTimeStart;
    }

    public InterstitialDurationControlType getLifeCycle() {
        return lifeCycle;
    }

    /**
     * Milliseconds
     * @return
     */
    public long getStart() {
        return start;
    }

    /**
     * Milliseconds
     * @return
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Interstitial can loop to fill out duration.
     * @return loop enabled
     */
    public boolean canLoop() {
        return loop;
    }

    /**
     * Loop Interstitial can loop to fill out duration
     * @param 
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    /**
     * How time is measured. WallClock,StreamClock, or Indefinite.
     * @param lifeCycle type of clock or indefinite.
     */
    public void setLifeCycle(InterstitialDurationControlType lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    /**
     * Milliseconds
     * @param duration Milliseconds
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

}

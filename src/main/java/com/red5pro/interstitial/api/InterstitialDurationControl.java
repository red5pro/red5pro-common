package com.red5pro.interstitial.api;

/**
 * This class determines when an interstitial is activated and deactivated via
 * timestamp input.
 *
 * @author Andy
 */
public class InterstitialDurationControl {
	private InterstitialDurationControlType lifeCycle;

	private long start;

	private long duration;

	private boolean isLoop;

	private boolean isPaused;

	/**
	 * @param type
	 *            duration control type
	 * @param duration
	 *            milliseconds
	 * @param streamStart
	 *            milliseconds
	 */
	public InterstitialDurationControl(InterstitialDurationControlType type, long duration, long streamStart) {
		this.lifeCycle = type;
		isPaused = true;
		this.duration = duration;
		this.start = streamStart;
	}

	/**
	 * Call this to resume program at any time. Indefinite sessions use this to
	 * resume via RPC. StreamClock and WallClock sessions can override scheduled
	 * time to resume earlier.
	 */
	public void resumeProgram() {
		isPaused = false;
	}

	/**
	 * Called to check if its time to start.
	 *
	 * @param streamTime
	 *            current timestamp of stream.
	 * @return true if stream time or wall clock is equal or greater than start
	 *         time.
	 */
	public boolean startNow(long streamTime) {
		switch (lifeCycle) {
			case STREAM_CLOCK :
				return streamTime >= start;
			case INDEFINITE :
				return isPaused;
			case WALL_CLOCK :
				return System.currentTimeMillis() >= start;
		}
		return false;
	}

	/**
	 * Called after activation. Returns true if session is still active according to
	 * start and duration.
	 *
	 * @param streamTime
	 *            current stream time in milliseconds.
	 * @return true if stream time or wall-clock is less than start plus duration.
	 */
	public boolean isActive(long streamTime) {
		if (!isPaused) {
			// api overide via RPC.
			return false;
		}

		switch (lifeCycle) {
			case STREAM_CLOCK :
				return streamTime > (start + duration) ? false : true;
			case INDEFINITE :
				return isPaused;
			case WALL_CLOCK :
				return System.currentTimeMillis() - start < duration;
		}

		return false;
	}

	/**
	 * Milliseconds
	 *
	 * @param streamTimeStart
	 *            Milliseconds
	 */
	public void setStart(long streamTimeStart) {
		this.start = streamTimeStart;
	}

	public InterstitialDurationControlType getLifeCycle() {
		return lifeCycle;
	}

	/**
	 * Milliseconds
	 *
	 * @return milliseconds
	 */
	public long getStart() {
		return start;
	}

	/**
	 * Milliseconds
	 *
	 * @return millsecionds
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Interstitial can loop to fill out duration.
	 *
	 * @return true if loop enabled
	 */
	public boolean canLoop() {
		return isLoop;
	}

	/**
	 * Loop Interstitial can loop to fill out duration
	 *
	 * @param canLoop
	 *            true if loop enabled
	 */
	public void setLoop(boolean canLoop) {
		this.isLoop = canLoop;
	}

	/**
	 * How time is measured. WallClock,StreamClock, or Indefinite.
	 *
	 * @param lifeCycle
	 *            type of clock or indefinite.
	 */
	public void setLifeCycle(InterstitialDurationControlType lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	/**
	 * Milliseconds
	 *
	 * @param duration
	 *            Milliseconds
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

}

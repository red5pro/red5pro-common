package com.red5pro.interstitial.api;

/**
 * This interface allows you to adjust the URI target after video resolution and
 * audio sample rates are discovered. It also allows you to be notified that an
 * insert is next in schedule so you can acquire and prepare any remote or
 * unusual media sources.
 * 
 * @author Andy
 *
 */
public interface InterstitialConfiguration {
	/**
	 * Close session and return null to cancel insertion.
	 * 
	 * @param stream
	 *            ClientBroadcastStream instance.
	 * @param session
	 *            Currently starting insertion.
	 * @return Adjusted, new, or as-is InterstitialSession.
	 */
	public InterstitialSession configure(IInterstitialStream stream, InterstitialSession session);

	/**
	 * Called prior to activation when session is next in schedule. Make any needed
	 * adjustments and invoke session-queue(). Session will be configured with
	 * current audio and video parameters.
	 * 
	 * @param session
	 *            session to acquire and prepare.
	 */
	public void queueSession(InterstitialSession session);
}

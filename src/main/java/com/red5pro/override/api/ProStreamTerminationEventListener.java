package com.red5pro.override.api;

import com.red5pro.override.IProStream;
/**
 * Listener for instance specific termination. 
 * @author Andy Shaules
 *
 */
public interface ProStreamTerminationEventListener {
	/**
	 * Called when stream instance is stopped.
	 * @param stream the stopped stream.
	 */
	void streamStopped(IProStream stream);
	
}

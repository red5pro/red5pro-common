/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.override.cauldron;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface notifies external handlers of process life cycle events.
 * <p />Register a listener to access live video processing API.
 * @author Andy Shaules
 */
public interface MediaProcessor {
	
	static List<MediaProcessorAware> listeners =new ArrayList<>();
	/**
	 * Add a listener to access the processing API
	 * @param listener
	 */
	static void addProcessListener(MediaProcessorAware listener){
		listeners.add(listener);
	}	
	/**
	 * Remove a listener.
	 * @param listener
	 */
	static void removeProcessListener(MediaProcessorAware listener){
		listeners.remove(listeners);
	}
}

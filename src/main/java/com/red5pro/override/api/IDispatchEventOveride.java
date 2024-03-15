package com.red5pro.override.api;

import java.util.Set;

import org.red5.server.api.stream.IStreamListener;
import org.red5.server.messaging.IPipe;
import org.red5.server.net.rtmp.event.IRTMPEvent;

import com.red5pro.override.IProStream;
/**
 * This interface allows an application to filter and control the routing of media and metadata frames.
 * @author Andy
 *
 */
public interface IDispatchEventOveride {
	/**
	 * Dispatch events to rtmp connections.
	 * @param stream The source stream
	 * @param pipe The Red5 IPipe interface.
	 * @param event The media or metadata event.
	 */
    public void dispatchToClients(IProStream stream, IPipe pipe, IRTMPEvent event);

    /**
     * Dispatch events to plugins. Each plugin supports a different type of client.
     * @param stream
     * @param listeners
     * @param event
     */
    public void dispatchToPlugins(IProStream stream, Set<IStreamListener> listeners, IRTMPEvent event);

}

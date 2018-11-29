/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.override;

import java.io.IOException;
import java.util.Map;

import org.red5.server.api.event.IEvent;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IClientStream;
import org.red5.server.api.stream.IClientBroadcastStream;
import org.red5.server.api.stream.IStream;
import org.red5.server.api.stream.StreamState;
import org.red5.server.net.rtmp.event.Notify;

import com.red5pro.override.api.ProStreamTerminationEventListener;
import com.red5pro.override.cauldron.ProcessConfiguration;
import com.red5pro.override.cauldron.brews.Potion;

/**
 * Red5 Pro server-side stream.
 * 
 * @author Paul Gregoire
 * @author Andy Shaules
 */
public interface IProStream extends IStream, IClientStream, IBroadcastStream, IClientBroadcastStream {

	/** {@inheritDoc} */
	void close();

	/** {@inheritDoc} */
	void dispatchEvent(IEvent event);

	/** {@inheritDoc} */
	Notify getMetaData();

	/** {@inheritDoc} */
	StreamState getState();

	/** {@inheritDoc} */
	long getBytesReceived();

	/** {@inheritDoc} */
	void saveAs(String name, boolean append) throws IOException;

	/**
	 * Returns the recording state of the stream.
	 *
	 * @return true if stream is recording, false otherwise
	 */
	boolean isRecording();

	/**
	 * Stops any currently active recording.
	 */
	void stopRecording();

	/**
	 * Sets the guid of the native module and queue of 'Ingredients'. <br>
	 * Use within an implementation of MediaProcessorAware streamProcessorStart
	 * 
	 * <pre>
	 * streamProcessorStart(IProStream stream) {
	 * 	Potion p = new Potion("face");
	 * 	p.add(new Ingredient("background", 0xFFFFFFFF));
	 * 	p.add(new Ingredient("maskShape", "rect"));
	 * 	stream.setPotion(p);
	 * }
	 * </pre>
	 * 
	 * Since Potion extends Queue, later in the application, you can add/update
	 * 'Ingredients'. The ProStream empties the Ingredient queue before processing
	 * each frame.
	 * 
	 * <pre>
	 * stream.getPotion().add(new Ingredient("maskShape", "round"));
	 * </pre>
	 * 
	 * @param potion
	 *            Potion
	 */
	void setPotion(Potion potion);

	/**
	 * Gets the Parameter Queue which the native processor is polling 'Ingrediants'
	 * from. <br>
	 * Since Potion extends Queue, in an application, you can add 'Ingredients'. The
	 * ProStream empties the Ingredient queue before processing each frame.
	 * 
	 * <pre>
	 * stream.getPotion().add(new Ingredient("maskShape", "round"));
	 * </pre>
	 * 
	 * @return Potion
	 */
	Potion getPotion();

	/**
	 * Sets the core processor class. Required to activate API.
	 * <p>
	 * This can also be set in red5-commons. file
	 * 
	 * <pre>
	 * com.red5pro.media.transform.codec.AVCProcessor
	 * </pre>
	 * 
	 * @param clazz
	 *            The class with core native bindings.
	 * 
	 */
	void setProcessorClass(String clazz);
	/**
	 * Sets the output parameters for the processor.
	 * 
	 * @param config
	 *            output parameters
	 */
	void setProcessConfiguration(ProcessConfiguration config);
	/**
	 * Add a Listener to be called at stream stop.
	 * 
	 * @param handler
	 *            the callee at stream-stop event.
	 */
	void addTerminationEventListener(ProStreamTerminationEventListener handler);
	/**
	 * 
	 * @param clazz
	 *            processor class. Use null for default.
	 * @param params
	 *            processor parameters. Use null for default.
	 */
	void usePreprocessor(String clazz, Map<String, Object> params);

}

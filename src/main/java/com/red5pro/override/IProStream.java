//
// Copyright © 2015 Infrared5, Inc. All rights reserved.
//
// The accompanying code comprising examples for use solely in conjunction with Red5 Pro (the "Example Code")
// is  licensed  to  you  by  Infrared5  Inc.  in  consideration  of  your  agreement  to  the  following
// license terms  and  conditions.  Access,  use,  modification,  or  redistribution  of  the  accompanying
// code  constitutes your acceptance of the following license terms and conditions.
//
// Permission is hereby granted, free of charge, to you to use the Example Code and associated documentation
// files (collectively, the "Software") without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The Software shall be used solely in conjunction with Red5 Pro. Red5 Pro is licensed under a separate end
// user  license  agreement  (the  "EULA"),  which  must  be  executed  with  Infrared5,  Inc.
// An  example  of  the EULA can be found on our website at: https://account.red5pro.com/assets/LICENSE.txt.
//
// The above copyright notice and this license shall be included in all copies or portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,  INCLUDING  BUT
// NOT  LIMITED  TO  THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR  A  PARTICULAR  PURPOSE  AND
// NONINFRINGEMENT.   IN  NO  EVENT  SHALL INFRARED5, INC. BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM,  OUT  OF  OR  IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
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

/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.override;

import java.io.IOException;

import org.red5.server.api.event.IEvent;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IClientStream;
import org.red5.server.api.stream.IClientBroadcastStream;
import org.red5.server.api.stream.IStream;
import org.red5.server.api.stream.StreamState;
import org.red5.server.net.rtmp.event.Notify;

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
     * Sets the guid of the native module and queue of 'Ingredients'.
     * @param potion
     * <p />Use within an implementation of MediaProcessorAware streamProcessorStart
     * <pre>
     * streamProcessorStart(IProStream stream){
     *     Potion p = new Potion("face");
     *     p.add(new Ingredient("background",0xFFFFFFFF));
     *     p.add(new Ingredient("maskShape","rect"));
     *     stream.setPotion(p);
     * }
     * </pre>
     * Since Potion extends Queue, later in the application, you can add/update 'Ingredients'. 
     * The ProStream empties the Ingredient queue before processing each frame. 
     * <pre>
     * stream.getPotion().add(new Ingredient("maskShape","round"));
     * </pre>
     * 
     */
    void setPotion(Potion potion);
    /**
     * Gets the Parameter Queue which the native processor is polling 'Ingrediants' from.
     * 
     * <p />
     * Since Potion extends Queue, in an application, you can add 'Ingredients'. 
     * The ProStream empties the Ingredient queue before processing each frame. 
     * <pre>
     * stream.getPotion().add(new Ingredient("maskShape","round"));
     * </pre>
     * @return Potion
     */
    Potion getPotion();	
    

}

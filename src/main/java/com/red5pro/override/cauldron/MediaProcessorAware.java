/*
 * Red5 Professional - http://www.infrared5.com Copyright (c) 2014-2017 by respective authors (see below). 
 * All rights reserved.
 */
package com.red5pro.override.cauldron;

import com.red5pro.override.IProStream;

/**
 * Processor handlers which are configuring the native components implement this interface.
 * <br>
 * @author Andy Shaules
 *
 */
public interface MediaProcessorAware {
    /**
     * Called when the native library is initiated. 
     * Use the IProcess object to load native plugins dll/so.
     * <p>Although the IProcess instance is not attached to an active stream, 
     * use the 'loadLib' method to globally register native plugins for 
     * IProStream instances to utilize.
     * <pre>
     *{@code
     * public void cauldronLibStarted(IProcess loader) {
     *     // if the corresponding potion guid was "face"
     *     // Potion p = new Potion("face");
     *     // guid as number is little-endian chars. 
     *     long expected =  'f' | ('a'<<8) | ('c'<<16) | ('e'<<24);
     *     int guid=loader.loadLibrary("plugins/native/CauldronTest.dll");
     *     if(expected!=guid)
     *         //something is not right
     *     
     * }
     * }
     * </pre> 
     * @param loader IProcess
     */
    void cauldronLibStarted(IProcess  loader);

    /**
     * Called when a native processor is about to be created.
     * <pre>
     * streamProcessorStart(IProStream stream){
     *     Potion p = new Potion("face");
     *     p.add(new Ingredient("background",0xFFFFFFFF));
     *     p.add(new Ingredient("maskShape","rect"));
     *     stream.setPotion(p);
     * }
     * </pre>
     * 
     * @param stream ProStream instance
     */
    void streamProcessorStart(IProStream stream);

    /**
     * Stream broadcast was closed. Processor was stopped.
     * 
     * @param stream ProStream instance
     */
    void streamProcessorStop(IProStream stream);

    /**
     * There was an error during the process life cycle.
     * 
     * @param stream ProStream instance
     * @param error Exception
     */
    void streamProcessorError(IProStream stream, Exception error);
}

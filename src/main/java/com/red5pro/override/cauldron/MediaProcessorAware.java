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
 * Red5 Professional - http://www.infrared5.com Copyright (c) 2014-2017 by respective authors (see below). 
 * All rights reserved.
 */
package com.red5pro.override.cauldron;

import com.red5pro.override.IProStream;

/**
 * Processor handlers which are configuring the native components implement this
 * interface. <br>
 * 
 * @author Andy Shaules
 *
 */
public interface MediaProcessorAware {
	/**
	 * Called when the native library is initiated. Use the IProcess object to load
	 * native plugins dll/so.
	 * <p>
	 * Although the IProcess instance is not attached to an active stream, use the
	 * 'loadLib' method to globally register native plugins for IProStream instances
	 * to utilize.
	 * 
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
	 * 
	 * @param loader
	 *            IProcess
	 */
	void cauldronLibStarted(IProcess loader);

	/**
	 * Called when a native processor is about to be created.
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
	 * @param stream
	 *            ProStream instance
	 */
	void streamProcessorStart(IProStream stream);

	/**
	 * Stream broadcast was closed. Processor was stopped.
	 * 
	 * @param stream
	 *            ProStream instance
	 */
	void streamProcessorStop(IProStream stream);

	/**
	 * There was an error during the process life cycle.
	 * 
	 * @param stream
	 *            ProStream instance
	 * @param error
	 *            Exception
	 */
	void streamProcessorError(IProStream stream, Exception error);
}

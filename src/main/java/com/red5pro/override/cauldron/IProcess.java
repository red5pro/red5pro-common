//
// Copyright © 2020 Infrared5, Inc. All rights reserved.
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
package com.red5pro.override.cauldron;

/**
 * Core native bindings.
 * 
 * @author Andy Shaules
 *
 */
public interface IProcess {
    /**
     * Load a dynamic process library into the cauldron cache.
     * 
     * @param path
     *            library path
     * @return guid
     */
    public long loadLibrary(String path);

    /**
     * Open process.
     * 
     * @param framerate
     *            target framerate
     * @param bitrate
     *            target bitrate
     * @param type
     *            type
     * @param timing
     *            timing
     * @param format
     *            format
     * @param returnType
     *            type to return
     * @return id of process
     */
    public long open(int framerate, int bitrate, int type, int timing, int format, int returnType);

    /**
     * Brew.
     * 
     * @param guid
     *            globally unique id
     * @param id
     *            instance id
     * @return code
     */
    public long brew(int guid, long id);

    /**
     * Close.
     * 
     * @param id
     *            instance id
     * @return code
     */
    public long close(long id);

    /**
     * Process video.
     * 
     * @param time
     *            milliseconds
     * @param nals
     *            packets
     * @param frameReturn
     *            information about available data
     * @param id
     *            instance id
     * @return code
     */
    public int write(long time, byte[] nals, byte[] frameReturn, long id);

    /**
     * Retrieve processed video.
     * 
     * @param frameReturn
     *            timestamps
     * @param packetData
     *            nal packets
     * @param id
     *            instance id
     * @return code
     */
    public int read(byte[] frameReturn, byte[] packetData, long id);

    /**
     * Apply.
     * 
     * @param guid
     *            module guid
     * @param key
     *            module property
     * @param value
     *            module property value
     * @param id
     *            instance id
     * @return code
     */
    public int apply(long guid, String key, String value, long id);
}

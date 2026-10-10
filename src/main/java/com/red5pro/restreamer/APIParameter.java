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
package com.red5pro.restreamer;

/**
 * Enum constants for use in the restreamer API.
 *
 * @author Paul Gregoire
 */
public enum APIParameter {

    /** API parameter for the request type. */
    type,
    /** API parameter for the requested action. */
    action,
    /** Create action. */
    create,
    /** Update action. */
    update,
    /** Terminate action. */
    kill,
    /** List action. */
    list,
    /** Block action. */
    block,
    /** Request timestamp. */
    timestamp,
    /** Request signature. */
    signature,
    /** Context path. */
    contextpath,
    /** Scope name. */
    scopename,
    /** Stream or resource name. */
    name,
    /** Output stream name. */
    outputname,
    /** IP address. */
    ip,
    /** Host name. */
    host,
    /** Port number. */
    port,
    /** Cast or conversion option. */
    cast,
    /** Resource ID. */
    id,
    /** Globally unique ID. */
    guid,
    /** Metadata. */
    meta,
    /** Recording option. */
    recording,
    /** Authentication type. */
    authtype,
    /** User name. */
    username,
    /** Password. */
    password,
    /** Authentication token. */
    token,
    /** Persistence option. */
    persist,
    /** Remote context path. */
    remoteContextPath,
    /** Remote stream name. */
    remoteStreamName,
    /** Stream name. */
    streamname,
    /** Resource state. */
    state,
    /** Response information. */
    info;

}

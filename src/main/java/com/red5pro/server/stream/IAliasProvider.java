//
// Copyright © 2023 Infrared5, Inc. All rights reserved.
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
package com.red5pro.server.stream;

import org.red5.net.websocket.WebSocketConnection;
import org.red5.server.api.IConnection;

/**
 * Interface for stream name alias providers.
 *
 * @author Paul Gregoire
 */
public interface IAliasProvider {

    /**
     * Returns whether or not the given name is an alias.
     *
     * @param alias stream name alias
     * @return true if alias, false otherwise
     */
    boolean isAlias(String alias);

    /**
     * Resolves the playback alias for a given stream name.
     *
     * @param alias stream name alias
     * @param conn connection
     * @return stream name and null if not an alias
     */
    String resolvePlayAlias(String alias, IConnection conn);

    /**
     * Resolves the publish alias for a given stream name.
     *
     * @param alias stream name alias
     * @param conn connection
     * @return stream name and null if not an alias
     */
    String resolveStreamAlias(String alias, IConnection conn);

    /**
     * Resolves the playback alias for a given stream name.
     *
     * @param alias stream name alias
     * @param webSocket websocket connection
     * @return stream name and null if not an alias
     */
    String resolvePlayAlias(String alias, WebSocketConnection webSocket);

    /**
     * Resolves the publish alias for a given stream name.
     *
     * @param alias stream name alias
     * @param webSocket websocket connection
     * @return stream name and null if not an alias
     */
    String resolveStreamAlias(String alias, WebSocketConnection webSocket);

    /**
     * Adds a stream alias.
     *
     * @param alias stream name alias
     * @param streamName stream name
     * @return true if added, false otherwise
     */
    boolean addStreamAlias(String alias, String streamName);

    /**
     * Removes a stream alias.
     *
     * @param alias stream name alias
     * @return true if removed, false otherwise
     */
    boolean removeStreamAlias(String alias);

    /**
     * Adds a playback alias.
     *
     * @param alias stream name alias
     * @param streamName stream name
     * @return true if added, false otherwise
     */
    boolean addPlayAlias(String alias, String streamName);

    /**
     * Removes a playback alias.
     *
     * @param alias stream name alias
     * @return true if removed, false otherwise
     */
    boolean removePlayAlias(String alias);

    /**
     * Removes all aliases for a given stream name.
     *
     * @param streamName stream name
     * @return true if removed, false otherwise
     */
    boolean removeAllAliases(String streamName);

}

package com.red5pro.server.stream;

import org.red5.net.websocket.WebSocketConnection;
import org.red5.server.api.IConnection;

/**
 * Adapter for stream name alias provider interface.
 *
 * @author Paul Gregoire
 */
public class AliasProviderAdapter implements IAliasProvider {

    @Override
    public boolean isAlias(String alias) {
        return false;
    }

    @Override
    public String resolvePlayAlias(String alias, IConnection conn) {
        return null;
    }

    @Override
    public String resolveStreamAlias(String alias, IConnection conn) {
        return null;
    }

    @Override
    public String resolvePlayAlias(String alias, WebSocketConnection webSocket) {
        return null;
    }

    @Override
    public String resolveStreamAlias(String alias, WebSocketConnection webSocket) {
        return null;
    }

    @Override
    public boolean addStreamAlias(String alias, String streamName) {
        return false;
    }

    @Override
    public boolean removeStreamAlias(String alias) {
        return false;
    }

    @Override
    public boolean addPlayAlias(String alias, String streamName) {
        return false;
    }

    @Override
    public boolean removePlayAlias(String alias) {
        return false;
    }

    @Override
    public boolean removeAllAliases(String streamName) {
        return false;
    }

}

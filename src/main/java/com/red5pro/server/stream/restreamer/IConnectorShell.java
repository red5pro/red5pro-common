package com.red5pro.server.stream.restreamer;

import org.red5.server.api.stream.IStreamCapableConnection;

public interface IConnectorShell extends IStreamCapableConnection {

    Object[] getConnectCallParams();

}

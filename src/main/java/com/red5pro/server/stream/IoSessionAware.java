package com.red5pro.server.stream;

import org.apache.mina.core.session.IoSession;

/**
 * Indicates that the implementation supports the <tt>getIoSession()</tt> accessor.
 * 
 * @author Paul Gregoire
 */
public interface IoSessionAware {

    /**
     * Returns an IoSession or null if it does not exist.
     * 
     * @return session or null
     */
    IoSession getIoSession();

}

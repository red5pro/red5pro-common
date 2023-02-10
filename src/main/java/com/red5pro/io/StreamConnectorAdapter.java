package com.red5pro.io;

import org.apache.commons.lang3.RandomStringUtils;
import org.ice4j.socket.IceSocketWrapper;

/**
 * Adapter for making StreamConnector usage easier.
 *
 * @author Paul Gregoire
 */
public abstract class StreamConnectorAdapter implements StreamConnector {

    protected Protocol protocol;

    /**
     * Name or identifier for this connector.
     */
    protected String name = RandomStringUtils.randomAlphanumeric(8);

    @Override
    public IceSocketWrapper getSocket() {
        return null;
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
    }

    /** {@inheritDoc} */
    @Override
    public abstract boolean isClosed();

    @Override
    public void started() {
    }

    @Override
    public void stopped() {
    }

}

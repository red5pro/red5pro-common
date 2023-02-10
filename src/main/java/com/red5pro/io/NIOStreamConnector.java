package com.red5pro.io;

import java.util.Queue;

import org.ice4j.ice.LocalCandidate;
import org.ice4j.socket.IceSocketWrapper;
import org.ice4j.socket.IceUdpSocketWrapper;
import org.ice4j.stack.RawMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.io.rtp.IRTPConnectorOutputStream;

/**
 * Represents a default implementation of <pre>StreamConnector</pre> utilizing NIO structures.
 *
 * @author Paul Gregoire
 */
public class NIOStreamConnector extends StreamConnectorAdapter {

    private final static Logger logger = LoggerFactory.getLogger(NIOStreamConnector.class);

    private static final boolean isDebug = logger.isDebugEnabled();

    /**
     * The <pre>IceSocketWrapper</pre> that a stream should use for control and data traffic.
     */
    protected final IceSocketWrapper socket;

    /**
     * The output stream for this connector.
     */
    protected IRTPConnectorOutputStream outputStream;

    /**
     * Container queue for incoming raw messages.
     */
    protected Queue<RawMessage> incomingMessageQueue;

    /**
     * Initializes a new <pre>NIOStreamConnector</pre> instance.
     *
     * @param socket the <pre>IceSocketWrapper</pre> to be used for data (e.g. RTP) and control data (e.g. RTCP) traffic
     */
    public NIOStreamConnector(IceSocketWrapper socket) {
        this.socket = socket;
        this.protocol = (socket instanceof IceUdpSocketWrapper) ? Protocol.UDP : Protocol.TCP;
        // get the message queue to observe incoming messages on the channel
        incomingMessageQueue = socket.getRawMessageQueue();
        if (logger.isTraceEnabled()) {
            logger.trace("Socket: {} protocol: {} acceptor: {}", socket, protocol, socket.getId());
        }
    }

    /**
     * Initializes a new <pre>NIOStreamConnector</pre> instance.
     *
     * @param socket the <pre>IceSocketWrapper</pre> to be used for data (e.g. RTP) and control data (e.g. RTCP) traffic
     * @param id identifier / name this socket belongs to
     */
    public NIOStreamConnector(IceSocketWrapper socket, String id) {
        this(socket);
        setName(id);
    }

    /**
     * Initializes a new <pre>NIOStreamConnector</pre> instance.
     *
     * @param candidate the <pre>LocalCandidate</pre> containing the channel
     */
    public NIOStreamConnector(LocalCandidate candidate) {
        this(candidate.getCandidateIceSocketWrapper());
        if (logger.isTraceEnabled()) {
            logger.trace("Socket from candidate: {} protocol: {}", socket, protocol);
        }
    }

    /**
     * Releases the resources allocated by this instance in the course of its execution and prepares it to be garbage collected.
     *
     * @see StreamConnector#close()
     */
    @Override
    public void close() {
        //logger.debug("close");
        if (socket != null) {
            if (isDebug) {
                logger.debug("close - channel is already closed? {} input queue size: {}", socket.isClosed(), incomingMessageQueue.size());
            }
            socket.close();
        } else {
            logger.debug("close - channel was null");
        }
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    /**
     * Returns a reference to the <pre>SelectableChannel</pre> that a stream should use for control and data traffic.
     *
     * @return a reference to the selectable channel
     * @see StreamConnector#getChannel()
     */
    @Override
    public IceSocketWrapper getSocket() {
        return socket;
    }

    /** {@inheritDoc} */
    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    /** {@inheritDoc} */
    @Override
    public Queue<RawMessage> getIncomingMessageQueue() {
        return incomingMessageQueue;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasOutputStream() {
        return outputStream != null;
    }

    /** {@inheritDoc} */
    @Override
    public void setOutputStream(IRTPConnectorOutputStream outputStream) {
        // no overwrite allowed
        if (outputStream == null) {
            this.outputStream = outputStream;
        }
    }

    /** {@inheritDoc} */
    @Override
    public IRTPConnectorOutputStream getOutputStream() {
        return outputStream;
    }

    /** {@inheritDoc} */
    @Override
    public void started() {
        logger.info("started");
    }

    /** {@inheritDoc} */
    @Override
    public void stopped() {
        logger.info("stopped");
    }

}

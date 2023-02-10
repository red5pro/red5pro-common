package com.red5pro.io;

import java.util.Queue;

import org.ice4j.socket.IceSocketWrapper;
import org.ice4j.stack.RawMessage;

import com.red5pro.io.rtp.IRTPConnectorOutputStream;

/**
 * The <pre>StreamConnector</pre> interface represents a datagram socket used by a media stream for RTP / RTCP traffic.
 * <br>
 * The reason why this media service makes sockets visible through this <pre>StreamConnector</pre> is so that they
 * could be shared among media and other libraries that may need to use them like an ICE implementation for example.
 *
 * @author Emil Ivov
 * @author Paul Gregoire
 */
public interface StreamConnector {
    /**
     * Enumerates the protocols supported by <pre>StreamConnector</pre>.
     */
    public enum Protocol {
        // UDP protocol
        UDP,
        // TCP protocol
        TCP
    }

    /**
     * Returns a reference to the <pre>IceSocketWrapper</pre> that the stream should use for control or data traffic.
     *
     * @return a reference to the IceSocketWrapper
     */
    public IceSocketWrapper getSocket();

    /**
     * Returns the protocol of this <pre>StreamConnector</pre>.
     *
     * @return the protocol
     */
    public Protocol getProtocol();

    /**
     * Returns the incoming message queue for the underlying communication channel.
     *
     * @return queue of incoming raw messages
     */
    public Queue<RawMessage> getIncomingMessageQueue();

    /**
     * Releases the resources allocated by this instance and prepares it to be garbage collected.
     */
    public void close();

    /**
     * Notifies this instance its <pre>DatagramSocket</pre> has started.
     */
    public void started();

    /**
     * Notifies this instance that utilization of its <pre>DatagramSocket</pre> has temporarily stopped. This instance
     * should be prepared to be started at a later time again though.
     */
    public void stopped();

    /**
     * Returns a name for this connector instance.
     *
     * @return name
     */
    public String getName();

    /**
     * Sets the name for this instance.
     *
     * @param name identifying string
     */
    public void setName(String name);

    /**
     * Sets the output stream for the connector.
     *
     * @param rtpConnectorOutputStream
     */
    public void setOutputStream(IRTPConnectorOutputStream rtpConnectorOutputStream);

    /**
     * Returns the output stream.
     *
     * @return RTPConnectorOutputStream
     */
    public IRTPConnectorOutputStream getOutputStream();

    /**
     * Returns whether or not an output stream exists in this connector.
     *
     * @return true if output stream exists and false otherwise
     */
    public boolean hasOutputStream();

    /**
     * Returns the closed state of the underlying socket.
     *
     * @return true if closed and false otherwise
     */
    public boolean isClosed();

}

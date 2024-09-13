package com.red5pro.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.http.impl.conn.Wire;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.event.IEventListener;

/**
 * Generic base type for messages destined to go out.
 *
 * @author Paul Gregoire
 */
public class WireMessage implements Serializable {

    private static final long serialVersionUID = 403903737133L;

    // use for serialization in amf / notify with custom
    public transient final static byte BINARY = 0x33, STRING = 0x34;

    protected String sender, destination, label;

    // can be byte[] or String
    protected Object message;

    // whether or not message is binary
    protected boolean binary;

    // base hash code generated in ctor
    protected transient int hashCode;

    // default ctor for serialization
    public WireMessage() {
    }

    /**
     * Create a wire message for the message bus containing binary data.
     *
     * @param sender id of the content sender
     * @param destination locator for routing
     * @param label location within a destination
     * @param message
     */
    public WireMessage(String sender, String destination, String label, byte[] message) {
        this.sender = sender;
        this.destination = destination;
        this.label = label;
        this.message = message;
        this.binary = true;
        generateBaseHashCode();
    }

    /**
     * Create a wire message for the message bus containing text data.
     *
     * @param sender id of the content sender
     * @param destination locator for routing
     * @param label location within a destination
     * @param message
     */
    public WireMessage(String sender, String destination, String label, String message) {
        this.sender = sender;
        this.destination = destination;
        this.label = label;
        this.message = message;
        this.binary = false;
        generateBaseHashCode();
    }

    /**
     * Create a wire message for the message bus containing data which may be binary or string.
     *
     * @param sender id of the content sender
     * @param destination locator for routing
     * @param label location within a destination
     * @param message
     */
    public WireMessage(boolean isBinary, String sender, String destination, String label, Object message) {
        this.sender = sender;
        this.destination = destination;
        this.label = label;
        if (isBinary) {
            this.message = (byte[]) message;
        } else {
            this.message = (String) message;
        }
        this.binary = isBinary;
        generateBaseHashCode();
    }

    /**
     * Generates the base hash code from the final ctor vars.
     */
    protected void generateBaseHashCode() {
        hashCode += ((sender == null) ? 0 : sender.hashCode());
        hashCode = 33 * hashCode + ((destination == null) ? 0 : destination.hashCode());
        hashCode = 33 * hashCode + ((label == null) ? 0 : label.hashCode());
    }

    public String getSender() {
        return sender;
    }

    public String getDestination() {
        return destination;
    }

    public String getLabel() {
        return label;
    }

    public Object getMessage() {
        return message;
    }

    public boolean isBinary() {
        return binary;
    }

    public byte[] getBinaryMessage() {
        return (byte[]) message;
    }

    public String getStringMessage() {
        return (String) message;
    }

    @Override
    public int hashCode() {
        int result = hashCode + ((message == null) ? 0 : message.hashCode());
        result = hashCode * result + (binary ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WireMessage other = (WireMessage) obj;
        if (sender == null) {
            if (other.sender != null)
                return false;
        } else if (!sender.equals(other.sender))
            return false;
        if (destination == null) {
            if (other.destination != null)
                return false;
        } else if (!destination.equals(other.destination))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (binary != other.binary)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WireMessage [sender=" + sender + ", destination=" + destination + ", label=" + label + ", message=" + message + "]";
    }

    /**
     * Wrap a wire message in an event.
     *
     * @param wireMessage
     * @return event containing the wire message
     */
    public static IEvent toEvent(WireMessage wireMessage) {
        IEvent event = new IEvent() {
            @Override
            public Object getObject() {
                return wireMessage;
            }

            @Override
            public Type getType() {
                return Type.CLIENT_NOTIFY;
            }

            @Override
            public boolean hasSource() {
                return false;
            }

            @Override
            public IEventListener getSource() {
                return null;
            }
        };
        return event;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeBoolean(binary);
        out.writeUTF(sender);
        out.writeUTF(destination);
        out.writeUTF(label);
        if (binary) {
            out.writeObject((byte[]) message);
        } else {
            out.writeUTF((String) message);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // determine if the message is binary first
        binary = in.readBoolean();
        // read the sender
        sender = in.readUTF();
        // read the destination
        destination = in.readUTF();
        // read the label
        label = in.readUTF();
        // read the message
        if (binary) {
            message = (byte[]) in.readObject();
        } else {
            message = in.readUTF();
        }
        // generate the base hash code
        generateBaseHashCode();
    }

}

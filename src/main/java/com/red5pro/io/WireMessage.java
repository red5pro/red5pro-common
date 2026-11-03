package com.red5pro.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.red5.server.api.event.IEvent;
import org.red5.server.api.event.IEventListener;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Generic base type for messages destined to go out.
 *
 * @author Paul Gregoire
 */
public class WireMessage implements Serializable {

    private static final long serialVersionUID = 403903837133L;

    /** Type markers used for serialization in amf / notify with custom: binary payload. */
    public transient final static byte BINARY = 0x33;

    /** Type marker for a string payload. */
    public transient final static byte STRING = 0x34;

    /** Type marker for a json payload. */
    public transient final static byte JSON = 0x35;

    /** Id of the content sender. */
    protected String sender;

    /** Locator used for routing. */
    protected String destination;

    /** Location within a destination. */
    protected String label;

    /** Message payload; can be byte[] or String or JsonElement. */
    protected Object message;

    /** Whether or not the message is binary. */
    protected boolean binary;

    /** Whether or not the message is json. */
    protected boolean json;

    /** Base hash code generated in the constructor. */
    protected transient int hashCode;

    /**
     * Default constructor, used for serialization.
     */
    public WireMessage() {
    }

    /**
     * Create a wire message for the message bus containing binary data.
     *
     * @param sender id of the content sender
     * @param destination locator for routing
     * @param label location within a destination
     * @param message the binary payload
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
     * @param message the text payload; treated as json if wrapped in braces
     */
    public WireMessage(String sender, String destination, String label, String message) {
        this.sender = sender;
        this.destination = destination;
        this.label = label;
        // check for json and set flag as needed
        if (message.startsWith("{") && message.endsWith("}")) {
            this.message = JsonParser.parseString(message).getAsJsonObject();
            json = true;
        } else {
            this.message = message;
        }
        this.binary = false;
        generateBaseHashCode();
    }

    /**
     * Create a wire message for the message bus containing data which may be binary or string.
     *
     * @param sender id of the content sender
     * @param destination locator for routing
     * @param label location within a destination
     * @param isBinary true if the message is a byte[] payload
     * @param message the payload as byte[], JsonElement, or String
     */
    public WireMessage(boolean isBinary, String sender, String destination, String label, Object message) {
        this.sender = sender;
        this.destination = destination;
        this.label = label;
        if (isBinary) {
            this.message = (byte[]) message;
        } else if (message instanceof JsonElement) {
            this.message = (JsonElement) message;
            json = true;
        } else {
            String tmpMessage = (String) message;
            // check for json and set flag as needed
            if (tmpMessage.startsWith("{") && tmpMessage.endsWith("}")) {
                this.message = JsonParser.parseString(tmpMessage).getAsJsonObject();
                json = true;
            } else {
                this.message = message;
            }
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

    /**
     * Returns the id of the content sender.
     *
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Returns the routing destination.
     *
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Returns the location within the destination.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the raw message payload.
     *
     * @return a byte[], String, or JsonElement
     */
    public Object getMessage() {
        return message;
    }

    /**
     * Indicates whether the payload is binary.
     *
     * @return true if binary
     */
    public boolean isBinary() {
        return binary;
    }

    /**
     * Indicates whether the payload is json.
     *
     * @return true if json
     */
    public boolean isJson() {
        return json;
    }

    /**
     * Returns the payload as binary.
     *
     * @return the payload bytes
     */
    public byte[] getBinaryMessage() {
        return (byte[]) message;
    }

    /**
     * Returns the payload as a string.
     *
     * @return the payload string
     */
    public String getStringMessage() {
        return (String) message;
    }

    /**
     * Returns the payload as a json object.
     *
     * @return the payload json object
     */
    public JsonObject getJsonMessage() {
        return (JsonObject) message;
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
     * @param wireMessage the message to wrap
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

    /**
     * Serializes this message to the given stream.
     *
     * @param out the output stream
     * @throws IOException if writing fails
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeBoolean(binary);
        out.writeBoolean(json);
        out.writeUTF(sender);
        out.writeUTF(destination);
        out.writeUTF(label);
        if (binary) {
            out.writeObject((byte[]) message);
        } else if (json) {
            out.writeUTF(((JsonObject) message).toString());
        } else {
            out.writeUTF((String) message);
        }
    }

    /**
     * Restores this message from the given stream.
     *
     * @param in the input stream
     * @throws IOException if reading fails
     * @throws ClassNotFoundException if a serialized class cannot be found
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // determine if the message is binary first
        binary = in.readBoolean();
        // determine if the message is json
        json = in.readBoolean();
        // read the sender
        sender = in.readUTF();
        // read the destination
        destination = in.readUTF();
        // read the label
        label = in.readUTF();
        // read the message
        if (binary) {
            message = (byte[]) in.readObject();
        } else if (json) {
            message = JsonParser.parseString(in.readUTF()).getAsJsonObject();
        } else {
            message = in.readUTF();
        }
        // generate the base hash code
        generateBaseHashCode();
    }

}

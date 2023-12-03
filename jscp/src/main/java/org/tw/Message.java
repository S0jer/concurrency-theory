package org.tw;

import org.jcsp.lang.One2OneChannel;

public class Message {
    private final MessageType type;
    private Status status;
    private int payload;
    private One2OneChannel directResponseChannel;
    private int ownerBufferId = -1;

    public Message(MessageType type) {
        this.type = type;
    }

    public Message(MessageType type, int payload) {
        this(type);
        this.payload = payload;
    }

    public Message(MessageType type, int payload, One2OneChannel channel) {
        this(type, payload);
        this.directResponseChannel = channel;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setOwner(int id) {
        this.ownerBufferId = id;
    }

    public MessageType getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public int getPayload() {
        return payload;
    }

    public One2OneChannel getDirectResponseChannel() {
        return directResponseChannel;
    }

    public int getOwnerBufferId() {
        return ownerBufferId;
    }

    @Override
    public String toString() {
        return "Message: " +
                type +
                ", " +
                status +
                ", " +
                payload;
    }
}

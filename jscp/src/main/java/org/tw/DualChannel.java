package org.tw;

import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.One2OneChannel;


public class DualChannel {
    private final One2OneChannel requestChannel;
    private final One2OneChannel responseChannel;

    public DualChannel(One2OneChannel requestChannel, One2OneChannel responseChannel) {
        this.requestChannel = requestChannel;
        this.responseChannel = responseChannel;
    }

    public void sendRequest(Message payload) {
        requestChannel.out().write(payload);
    }

    public void sendResponse(Message payload) {
        responseChannel.out().write(payload);
    }

    public Message receiveRequest() {
        return (Message) requestChannel.in().read();
    }

    public Message receiveResponse() {
        return (Message) responseChannel.in().read();
    }

    public AltingChannelInput getRequestChannelInput() {
        return requestChannel.in();
    }

    public One2OneChannel getResponseChannel() {
        return responseChannel;
    }
}

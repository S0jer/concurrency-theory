package org.tw;

import org.jcsp.lang.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Buffer implements CSProcess {
    private static final String WAITING_FOR_CLIENT = "Buffer %d: Waiting for client...";
    private static final String RECEIVED_FROM_CLIENT = "Buffer %d: Received from client -> %s";
    private static final String MESSAGE_RETURNED = "Buffer %d: Message returned - marking as FAILURE";
    private static final String ADDING_TO_QUEUE = "Buffer %d: Adding to queue. Queue size: %d | Task: %d";
    private static final String TOKEN_RECEIVED = "Buffer %d: Token received";
    private static final String SENDING_TOKEN = "Buffer %d: Sending token to next buffer...";
    private static final String SENDING_MESSAGE = "Buffer %d: Sending message to next buffer... Message = %s";
    private static final String PROCESSING_END = "Buffer %d: Processing completed";
    private static final String WAITING_FOR_MESSAGE = "Buffer %d: Waiting for a message from the back buffer...";
    private final int bufferSize;
    private int bufferValue;
    private final One2OneChannel forwardBuffer;
    private final One2OneChannel backwardBuffer;
    private final List<DualChannel> clients;
    private final int bufferIndex;
    private final Deque<Message> queue;
    private boolean hasToken;
    private int successCount = 0;
    private int failureCount = 0;

    public Buffer(One2OneChannel forwardBuffer, One2OneChannel backwardBuffer, List<DualChannel> clients, int size, int index) {
        this.forwardBuffer = forwardBuffer;
        this.backwardBuffer = backwardBuffer;
        this.clients = clients;
        this.bufferValue = 0;
        this.bufferSize = size;
        this.bufferIndex = index;
        this.queue = new ArrayDeque<>();
        this.hasToken = index == 0;
    }

    public void run() {
        Guard[] guards = new Guard[clients.size()];

        for (int i = 0; i < clients.size(); i++) {
            guards[i] = clients.get(i).getRequestChannelInput();
        }

        Alternative alt = new Alternative(guards);

        while (true) {
            processClientRequests(alt);
            handleBufferOperations();
        }
    }

    private void processClientRequests(Alternative alt) {
        while (queue.isEmpty()) {
            log(String.format(WAITING_FOR_CLIENT, bufferIndex));
            int index = alt.select();
            Message message = clients.get(index).receiveRequest();
            log(String.format(RECEIVED_FROM_CLIENT, bufferIndex, message));
            message.setOwner(bufferIndex);
            processOrder(message);
        }
    }

    private void handleBufferOperations() {
        log(String.format("Buffer %d: Exiting while, queue size = %d", bufferIndex, queue.size()));
        if (!hasToken) {
            waitForToken();
            Message message = waitForMessageFromBackBuffer();
            if (message.getOwnerBufferId() == this.bufferIndex) {
                log(String.format(MESSAGE_RETURNED, bufferIndex));
                message.setStatus(Status.FAILURE);
                message.getDirectResponseChannel().out().write(message);
            } else {
                processOrder(message);
            }
        }
        sendTokenToNextBuffer();
        sendFirstQueueItemToNextBuffer();
    }

    private void processOrder(Message message) {
        if (message.getType() == MessageType.ORDER_POST) {
            if (canAddToBuffer(message.getPayload())) {
                processProduction(message);
            } else {
                queue.add(message);
                failureCount++;
                log(String.format(ADDING_TO_QUEUE, bufferIndex, queue.size(), message.getPayload()));
            }
        } else if (message.getType() == MessageType.ORDER_GET) {
            if (canTakeFromBuffer(message.getPayload())) {
                processConsumption(message);
            } else {
                queue.add(message);
                failureCount++;
                log(String.format(ADDING_TO_QUEUE, bufferIndex, queue.size(), message.getPayload()));
            }
        } else if (message.getType() == MessageType.TOKEN) {
            log(String.format(TOKEN_RECEIVED, bufferIndex));
            hasToken = true;
        }
    }


    private void processProduction(Message message) {
        log(String.format("Buffer %d: Adding -> %d", bufferIndex, message.getPayload()));
        bufferValue += message.getPayload();
        Message response = new Message(MessageType.RESPONSE_POST);
        response.setStatus(Status.SUCCESS);
        message.getDirectResponseChannel().out().write(response);
        successCount++;
        log(String.format(PROCESSING_END, bufferIndex));
        log(getBalance());
    }

    private void processConsumption(Message message) {
        log(String.format("Buffer %d: Subtracting -> %d", bufferIndex, message.getPayload()));
        bufferValue -= message.getPayload();
        Message response = new Message(MessageType.RESPONSE_GET, message.getPayload());
        response.setStatus(Status.SUCCESS);
        message.getDirectResponseChannel().out().write(response);
        successCount++;
        log(String.format(PROCESSING_END, bufferIndex));
        log(getBalance());
    }

    private void waitForToken() {
        log(String.format("Buffer %d: Waiting for token...", bufferIndex));
        Message token = (Message) backwardBuffer.in().read();
        processOrder(token);
    }


    private Message waitForMessageFromBackBuffer() {
        log(String.format(WAITING_FOR_MESSAGE, bufferIndex));
        return (Message) backwardBuffer.in().read();
    }


    private void sendTokenToNextBuffer() {
        log(String.format(SENDING_TOKEN, bufferIndex));
        sendMessageToNextBuffer(new Message(MessageType.TOKEN));
        hasToken = false;
    }

    private void sendMessageToNextBuffer(Message message) {
        log(String.format(SENDING_MESSAGE, bufferIndex, message));
        forwardBuffer.out().write(message);
    }

    private void sendFirstQueueItemToNextBuffer() {
        Message messageToSendForward = queue.poll();
        if (messageToSendForward != null) {
            sendMessageToNextBuffer(messageToSendForward);
        }
    }

    private boolean canAddToBuffer(int value) {
        return this.bufferValue + value <= this.bufferSize;
    }

    private boolean canTakeFromBuffer(int value) {
        return this.bufferValue - value >= 0;
    }

    public One2OneChannel getForwardBuffer() {
        return forwardBuffer;
    }

    public One2OneChannel getBackwardBuffer() {
        return backwardBuffer;
    }

    public String getBalance() {
        return String.format("Buffer %d: Balance [Success:Failure]: %d:%d", bufferIndex, successCount, failureCount);
    }

    private void log(String message) {
        System.out.println(message);
    }
}

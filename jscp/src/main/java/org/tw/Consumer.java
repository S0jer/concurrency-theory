package org.tw;

import org.jcsp.lang.CSProcess;

public class Consumer implements CSProcess {
    private static final int MIN_CONSUME_UNIT = 1;
    private final DualChannel bufferGate;
    private final int maxToConsume;
    private final int consumerIndex;
    private int successCount = 0;
    private int failureCount = 0;

    public Consumer(DualChannel bufferGate, int maxToConsume, int index) {
        this.bufferGate = bufferGate;
        this.maxToConsume = maxToConsume;
        this.consumerIndex = index;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int toConsume = determineConsumption();
            Message request = createRequest(toConsume);
            log("Requesting: " + request);
            bufferGate.sendRequest(request);

            Message response = bufferGate.receiveResponse();
            log("Received: " + response);
            processResponse(response);
        }
    }

    private void processResponse(Message response) {
        if (response.getStatus() == Status.SUCCESS) {
            successCount++;
        } else {
            failureCount++;
        }
        log(getBalance());
    }

    private int determineConsumption() {
        return (int) (Math.random() * (maxToConsume - MIN_CONSUME_UNIT)) + MIN_CONSUME_UNIT;
    }

    private Message createRequest(int toConsume) {
        return new Message(MessageType.ORDER_GET, toConsume, bufferGate.getResponseChannel());
    }

    public DualChannel getBufferGate() {
        return bufferGate;
    }

    private String getBalance() {
        return "Consumer " + consumerIndex + ": Balance [Success:Failure]: " + successCount + ":" + failureCount;
    }

    private void log(String message) {
        System.out.println("Consumer " + consumerIndex + ": " + message);
    }
}

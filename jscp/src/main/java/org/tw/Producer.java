package org.tw;


import org.jcsp.lang.CSProcess;

public class Producer implements CSProcess {
    private final DualChannel bufferGate;
    private final int maxToProduce;
    private final int producerIndex;
    private int successCount = 0;
    private int failureCount = 0;

    public Producer(DualChannel bufferGate, int maxToProduce, int index) {
        this.bufferGate = bufferGate;
        this.maxToProduce = maxToProduce;
        this.producerIndex = index;
    }

    public void run() {
        while (true) {
            int payload = generateProductionPayload();
            Message request = createProductionRequest(payload);
            log("Sending: " + request);
            bufferGate.sendRequest(request);

            Message response = bufferGate.receiveResponse();
            log("Received: " + response);
            processResponse(response);
        }
    }

    private void processResponse(Message response) {
        if (response.getStatus() == Status.SUCCESS) {
            this.successCount++;
        } else {
            this.failureCount++;
        }
        System.out.println(getBalance());
    }

    public DualChannel getBufferGate() {
        return this.bufferGate;
    }

    private int generateProductionPayload() {
        return (int) (Math.random() * maxToProduce) + 1;
    }

    private Message createProductionRequest(int payload) {
        return new Message(MessageType.ORDER_POST, payload, bufferGate.getResponseChannel());
    }

    public String getBalance() {
        return "Producer " + producerIndex + ": Balance [Success:Failure]: " + this.successCount + ":" + this.failureCount + ")";

    }

    private void log(String message) {
        System.out.println("Consumer " + producerIndex + ": " + message);
    }
}


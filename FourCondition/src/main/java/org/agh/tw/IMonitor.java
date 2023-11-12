package org.agh.tw;

public interface IMonitor {
    void produce(int threadId, int cargo, int value) throws InterruptedException;
    void consume(int threadId, int cargo) throws InterruptedException;
    int getProducerOperationsCount();
    int getConsumerOperationsCount();

    int getHalfCapacity();
}

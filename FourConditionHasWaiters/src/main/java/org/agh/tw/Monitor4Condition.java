package org.agh.tw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor4Condition {
    private LinkedList<Integer> bufor = new LinkedList<>();
    private int buforCapacity;
    private int maxProducerWaitingCount = 0;
    private int maxConsumerWaitingCount = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition firstProducer = lock.newCondition();
    private final Condition otherProducers = lock.newCondition();
    private final Condition firstConsumer = lock.newCondition();
    private final Condition otherConsumers = lock.newCondition();
    private final List<Integer> producersWaitCount;
    private final List<Integer> consumersWaitCount;

    public Monitor4Condition(int producersNumber, int consumersNumber, int buforCapacity) {
        this.producersWaitCount = new ArrayList<>(Collections.nCopies(producersNumber, 0));
        this.consumersWaitCount = new ArrayList<>(Collections.nCopies(consumersNumber, 0));
        this.buforCapacity = buforCapacity * 2;
    }

    public void produce(int threadId, int cargo, int value) throws InterruptedException {
        lock.lock();
        try {
            while (lock.hasWaiters(firstProducer)) {
                this.increaseWaitingProducerCount(threadId);
                maxProducerWaitingCount = Math.max(maxProducerWaitingCount, this.producersWaitCount.get(threadId));
//                System.out.println("Producer" + threadId + " waits " + this.producersWaitCount.get(threadId) + " times to add: " + cargo);
//                printQueues();
                otherProducers.await();
                System.out.println("producer - first While");
            }
            this.clearWaitingProducerCount(threadId);


            while (buforCapacity - bufor.size() < cargo) {
                System.out.println("producer - second While");
                firstProducer.await();
            }

            for (int i = 0; i < cargo; i++) {
                bufor.add(value);
            }

            otherProducers.signal();
            firstConsumer.signal();
        } finally {
//            System.out.println("MaxProducerWaitingCount: " + this.maxProducerWaitingCount);
            lock.unlock();
        }
    }

    public void consume(int threadId, int cargo) throws InterruptedException {
        lock.lock();
        try {
            while (lock.hasWaiters(firstConsumer)) {
                this.increaseWaitingConsumerCount(threadId);
                maxConsumerWaitingCount = Math.max(maxConsumerWaitingCount, this.consumersWaitCount.get(threadId));
//                System.out.println("Consumer" + threadId + " waits " + this.consumersWaitCount.get(threadId) + " times to remove: " + cargo);
//                printQueues();
                System.out.println("consume - first While");
                otherConsumers.await();
            }
            this.clearWaitingConsumerCount(threadId);

            while (bufor.size() < cargo) {
                System.out.println("consume - second While");
                firstConsumer.await();
            }

            for (int i = 0; i < cargo; i++) {
                bufor.removeFirst();
            }

            otherConsumers.signal();
            firstProducer.signal();
        } finally {
//            System.out.println("MaxConsumerWaitingCount: " + this.maxConsumerWaitingCount);
            lock.unlock();
        }
    }

    public int getHalfCapacity() {
        return buforCapacity / 2;
    }

    private void increaseWaitingProducerCount(int index) {
        this.producersWaitCount.set(index, this.producersWaitCount.get(index) + 1);
    }

    private void clearWaitingProducerCount(int index) {
        this.producersWaitCount.set(index, 0);
    }

    private void increaseWaitingConsumerCount(int index) {
        this.consumersWaitCount.set(index, this.consumersWaitCount.get(index) + 1);
    }

    private void clearWaitingConsumerCount(int index) {
        this.consumersWaitCount.set(index, 0);
    }

    private void printQueues() {
        System.out.println("FirstProducers " + this.lock.getWaitQueueLength(this.firstProducer) + " threads");
        System.out.println("OtherProducers " + this.lock.getWaitQueueLength(this.otherProducers) + " threads");
        System.out.println("FirstConsumers " + this.lock.getWaitQueueLength(this.firstConsumer) + " threads");
        System.out.println("OtherConsumers " + this.lock.getWaitQueueLength(this.otherConsumers) + " threads");
    }
}


package org.agh.tw.monitors;

import org.agh.tw.SharedResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class MonitorNestedLocks implements IMonitor {
    private LinkedList<Integer> bufor = new LinkedList<>();
    private int buforCapacity;
    private int maxProducerWaitingCount = 0;
    private int maxConsumerWaitingCount = 0;
    private final ReentrantLock outside_producers = new ReentrantLock();
    private final ReentrantLock outside_consumers = new ReentrantLock();
    private final ReentrantLock inside_lock = new ReentrantLock();
    private final Condition common_condition = inside_lock.newCondition();
    private final List<Integer> producersWaitCount;
    private final List<Integer> consumersWaitCount;
    private final SharedResource resource;
    private int producerOperationsCount = 0;
    private int consumerOperationsCount = 0;
    private int operationsCountLimit;   // -1 means there's no limit

    public MonitorNestedLocks(int producersNumber, int consumersNumber, int buforCapacity, int operationsCountLimit, SharedResource resource) {
        this.producersWaitCount = new ArrayList<>(Collections.nCopies(producersNumber, 0));
        this.consumersWaitCount = new ArrayList<>(Collections.nCopies(consumersNumber, 0));
        this.buforCapacity = buforCapacity;
        this.operationsCountLimit = operationsCountLimit;
        this.resource = resource;
    }

    @Override
    public void produce(int threadId, int cargo, int value) {
        outside_producers.lock();
        try {
            inside_lock.lock();

            while (buforCapacity - bufor.size() < cargo) {
                common_condition.await();
            }

            for (int i = 0; i < cargo; i++) {
                bufor.add(value);
            }

            operationsCountLimit -= 1;
            producerOperationsCount += 1;

            if (operationsCountLimit <= 0) {
                resource.shouldStop = true;
            }
            common_condition.signal();
            inside_lock.unlock();
        } catch(InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } finally {
            outside_producers.unlock();
        }
    }

    @Override
    public void consume(int threadId, int cargo) {
        outside_consumers.lock();
        try {
            inside_lock.lock();
            while (bufor.size() < cargo) {
                common_condition.await();
            }

            for (int i = 0; i < cargo; i++) {
                bufor.removeFirst();
            }

            operationsCountLimit -= 1;
            consumerOperationsCount += 1;

            if (operationsCountLimit <= 0) {
                resource.shouldStop = true;
            }

            common_condition.signal();
            inside_lock.unlock();
        } catch(InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } finally {
            outside_consumers.unlock();
        }
    }

    @Override
    public int getHalfCapacity() {
        return buforCapacity / 2;
    }

    @Override
    public int getProducerOperationsCount() {
        return producerOperationsCount;
    }

    @Override
    public int getConsumerOperationsCount() {
        return consumerOperationsCount;
    }
}

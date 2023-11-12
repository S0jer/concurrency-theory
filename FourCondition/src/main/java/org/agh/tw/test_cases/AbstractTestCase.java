package org.agh.tw.test_cases;

import org.agh.tw.Consumer;
import org.agh.tw.Producer;
import org.agh.tw.SharedResource;
import org.agh.tw.generators.IGenerator;
import org.agh.tw.monitors.IMonitor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTestCase {
    protected final IMonitor monitor;
    protected final IGenerator generator;
    protected final int nProducers;
    protected final int nConsumers;
    protected final SharedResource resource;

    protected final List<Thread> threads;


    public AbstractTestCase(IMonitor monitor, IGenerator generator, int nProducers, int nConsumers, SharedResource resource) {
        this.monitor = monitor;
        this.generator = generator;
        this.nProducers = nProducers;
        this.nConsumers = nConsumers;
        this.resource = resource;
        this.threads = new ArrayList<>();
    }


    public void run() {
        initializeThreads();
        beforeStart();
        startThreads();
        stopCondition();
        saveResults();
        interruptThreads();
    }

    private void initializeThreads() {
        for (int i = 0; i < nProducers; i++) {
            Thread producerThread = new Thread(new Producer(i, monitor, generator, resource));
            threads.add(producerThread);
        }

        for (int i = 0; i < nConsumers; i++) {
            Thread consumerThread = new Thread(new Consumer(i, monitor, generator, resource));
            threads.add(consumerThread);
        }
        java.util.Collections.shuffle(threads);
    }

    protected void beforeStart() {}

    abstract void stopCondition();

    private void startThreads() {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    private void interruptThreads() {
        resource.shouldStop = true;
        for (Thread t : threads) {
            t.interrupt();
        }
    }

    abstract void saveResults();
}

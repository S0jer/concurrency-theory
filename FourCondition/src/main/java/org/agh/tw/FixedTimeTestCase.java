package org.agh.tw;

import java.util.ArrayList;
import java.util.List;


public class FixedTimeTestCase {
    private final IMonitor monitor;

    private final IGenerator generator;

    private final int seconds;
    private final int nProducers;
    private final int nConsumers;
    private final SharedResource resource;

    public FixedTimeTestCase(IMonitor monitor, IGenerator generator, int seconds, int nProducers, int nConsumers, SharedResource resource) {
        this.monitor = monitor;
        this.generator = generator;
        this.seconds = seconds;
        this.nProducers = nProducers;
        this.nConsumers = nConsumers;
        this.resource = resource;
    }

    public int test() {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < nProducers; i++) {
            Thread producerThread = new Thread(new Producer(i, monitor, generator, resource));
            threads.add(producerThread);
        }

        for (int i = 0; i < nConsumers; i++) {
            Thread consumerThread = new Thread(new Consumer(i, monitor, generator, resource));
            threads.add(consumerThread);
        }
        java.util.Collections.shuffle(threads);

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int result = monitor.getConsumerOperationsCount() + monitor.getProducerOperationsCount();

        for (Thread t : threads) {
            t.interrupt();
        }

        return result;
    }
}

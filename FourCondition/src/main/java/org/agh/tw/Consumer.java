package org.agh.tw;

import org.agh.tw.generators.IGenerator;
import org.agh.tw.monitors.IMonitor;

public class Consumer implements Runnable {

    private final IMonitor monitor;
    private final IGenerator generator;
    private final SharedResource resource;

    private final int threadId;


    public Consumer(int threadId, IMonitor monitor, IGenerator generator, SharedResource resource) {
        this.monitor = monitor;
        this.generator = generator;
        this.threadId = threadId;
        this.resource = resource;
    }

    @Override
    public void run() {
        try {
            while (!resource.shouldStop) {
                int cargo = generator.generate(1, monitor.getHalfCapacity());
//                System.out.println("Producer" + threadId + " wants to add " + cargo);
                monitor.consume(threadId, cargo);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

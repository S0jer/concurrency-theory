package org.agh.tw;

public class Producer implements Runnable {
    private final IMonitor monitor;
    private final IGenerator generator;
    private final SharedResource resource;
    private final int threadId;

    public Producer(int threadId, IMonitor monitor, IGenerator generator, SharedResource resource) {
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
                int value = generator.generate(1, 100);
//                System.out.println("Producer" + threadId + " wants to add " + cargo);
                monitor.produce(threadId, cargo, value);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

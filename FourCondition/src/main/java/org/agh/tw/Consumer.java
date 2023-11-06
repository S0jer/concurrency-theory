package org.agh.tw;

public class Consumer implements Runnable {

    private Monitor4Condition monitor4Condition;
    private final Main.SharedResource resource;

    private final int threadId;


    public Consumer(int threadId, Monitor4Condition monitor4Condition, Main.SharedResource resource) {
        this.monitor4Condition = monitor4Condition;
        this.threadId = threadId;
        this.resource = resource;
    }

    @Override
    public void run() {
        try {
            while (!resource.shouldStop) {
                int cargo = (int) (Math.random() * 100) % monitor4Condition.getHalfCapacity() + 1; // Ensure we don't exceed half the capacity.
//                System.out.println("Producer" + threadId + " wants to add " + cargo);
                monitor4Condition.consume(threadId, cargo);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package org.agh.tw;

public class Producer implements Runnable {
    private Monitor2Condition monitor2Condition;
    private final int threadId;

    public Producer(int threadId, Monitor2Condition monitor2Condition) {
        this.monitor2Condition = monitor2Condition;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int cargo = (int) (Math.random() * 100) % monitor2Condition.getHalfCapacity() + 1; // Ensure we don't exceed half the capacity.
                int value = (int) (Math.random() * 100);
                System.out.println("Producer" + threadId + " wants to add " + cargo);
                monitor2Condition.produce(threadId, cargo, value);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

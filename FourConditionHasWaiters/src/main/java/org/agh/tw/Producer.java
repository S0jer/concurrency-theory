package org.agh.tw;

public class Producer implements Runnable {
    private Monitor4Condition monitor4Condition;
    private final int threadId;

    public Producer(int threadId, Monitor4Condition monitor4Condition) {
        this.monitor4Condition = monitor4Condition;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int cargo = (int) (Math.random() * 100) % monitor4Condition.getHalfCapacity() + 1; // Ensure we don't exceed half the capacity.
                int value = (int) (Math.random() * 100);
                System.out.println("Producer" + threadId + " wants to add " + cargo);
                monitor4Condition.produce(threadId, cargo, value);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

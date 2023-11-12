package org.agh.tw;

public class Main {
    public static void main(String[] args) {
        int producersNumber = 5;
        int consumersNumber = 5;
        Monitor monitor = new Monitor(producersNumber, consumersNumber, 10);

        for (int i = 0; i < producersNumber; i++) {
            Thread producerThread = new Thread(new Producer(i, monitor));
            producerThread.start();
        }

        for (int i = 0; i < consumersNumber; i++) {
            Thread consumerThread = new Thread(new Consumer(i, monitor));
            consumerThread.start();
        }
    }
}
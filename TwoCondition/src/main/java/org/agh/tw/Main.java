package org.agh.tw;

public class Main {
    public static void main(String[] args) {
        int producersNumber = 5;
        int consumersNumber = 5;
        Monitor2Condition monitor2Condition = new Monitor2Condition(producersNumber, consumersNumber, 10);

        for (int i = 0; i < producersNumber; i++) {
            Thread producerThread = new Thread(new Producer(i, monitor2Condition));
            producerThread.start();
        }

        for (int i = 0; i < consumersNumber; i++) {
            Thread consumerThread = new Thread(new Consumer(i, monitor2Condition));
            consumerThread.start();
        }
    }}
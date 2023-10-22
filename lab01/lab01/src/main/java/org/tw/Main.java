package org.tw;

public class Main {

    static int i = 0;

    public static void main(String[] args) {

        int numberOfThreads = 10;
        int numberOfIterations = 1000;

        for (int t = 0; t < numberOfThreads; t++) {
            new ThreadPlus(numberOfIterations).start();
            new ThreadMinus(numberOfIterations).start();
        }
    }

    synchronized static void increment() {
        i += 1;
        System.out.println("Incremented to: " + i);
    }

    synchronized static void decrement() {
        i -= 1;
        System.out.println("Decremented to: " + i);
    }

    static class ThreadPlus extends Thread {
        int iterations;

        ThreadPlus(int iterations) {
            this.iterations = iterations;
        }

        @Override
        public void run() {
            for (int t = 0; t < iterations; t++) {
                increment();
            }
        }
    }

    static class ThreadMinus extends Thread {
        int iterations;

        ThreadMinus(int iterations) {
            this.iterations = iterations;
        }

        @Override
        public void run() {
            for (int t = 0; t < iterations; t++) {
                decrement();
            }
        }
    }

    static class taskPlus implements Runnable {


        int iterations;

        taskPlus(int iterations) {
            this.iterations = iterations;
        }

        @Override
        public void run() {
            System.out.println("StartPlus");
            for (int t = 0; t < iterations; t++) {
                increment();
            }
        }
    }

    static class taskMinus implements Runnable {

        int iterations;

        taskMinus(int iterations) {
            this.iterations = iterations;
        }

        @Override
        public void run() {
            System.out.println("StartMinus");
            for (int t = 0; t < iterations; t++) {
                decrement();
            }
        }
    }
}

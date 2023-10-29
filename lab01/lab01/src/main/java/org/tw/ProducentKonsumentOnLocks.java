package org.tw;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducentKonsumentOnLocks {

    public static void main(String[] args) {
        Bufor bufor = new Bufor(4);

        int liczbaProducentow = 100;
        int liczbaKonsumentow = 100;

        for (int i = 0; i < liczbaProducentow; i++) {
            new Producent(bufor).start();
        }

        for (int i = 0; i < liczbaKonsumentow; i++) {
            new Konsument(bufor).start();
        }
    }

    static class Bufor {
        private LinkedList<Integer> lista = new LinkedList<>();
        private final int capacity;


        private final ReentrantLock lock = new ReentrantLock();
        private final Condition consuments = lock.newCondition();
        private final Condition producers = lock.newCondition();

        public Bufor(int capacity) {
            this.capacity = capacity;
        }

        public void add(int ile, int wartosc) throws InterruptedException {
            lock.lock();
            try {
                if (lock.hasWaiters(consuments)) {
                    producers.await();
                }
//                while (lista.size() == capacity) {
//                    wait();
//                }

                for (int i = 0; i < ile; i++) {
                    lista.add(wartosc);
                }
                System.out.println("Producent " + Thread.currentThread().getId() + " dodał: " + wartosc + " , " + ile + " razy.");

                consuments.signal();
            } finally {
                lock.unlock();
            }
        }

        public void remove(int ile) throws InterruptedException {

            lock.lock();
            try {
                if (lock.hasWaiters(producers)) {
                    consuments.await();
                }
//                while (lista.isEmpty()) {
//                    wait();
//                }

                for (int i = 0; i < ile; i++) {
                    int wartosc = lista.removeFirst();
                }
                System.out.println("Konsument " + Thread.currentThread().getId() + " zabrał: x" + " , " + ile + " razy.");

                producers.signal();
            } finally {
                lock.unlock();
            }

        }

        public int getHalfCapacity() {
            return capacity / 2;
        }
    }

    static class Producent extends Thread {
        private Bufor bufor;

        public Producent(Bufor bufor) {
            this.bufor = bufor;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int ile = (int) (Math.random() * 100) % bufor.getHalfCapacity() + 1; // Ensure we don't exceed half the capacity.
                    int wartosc = (int) (Math.random() * 100);
                    bufor.add(ile, wartosc);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Konsument extends Thread {
        private Bufor bufor;

        public Konsument(Bufor bufor) {
            this.bufor = bufor;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int ile = (int) (Math.random() * 100) % bufor.getHalfCapacity() + 1; // Ensure we don't exceed half the capacity.
                    bufor.remove(ile);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}

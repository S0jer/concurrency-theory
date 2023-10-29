package org.tw;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MPNKWB {


    public static void main(String[] args) {
        Bufor bufor = new Bufor(10);
        int liczbaProducentow = 3;
        int liczbaKonsumentow = 3;

        for (int i = 0; i < liczbaProducentow; i++) {
            Thread producentThread = new Thread(new Producent(bufor));
            producentThread.start();
        }

        for (int i = 0; i < liczbaKonsumentow; i++) {
            Thread konsumentThread = new Thread(new Konsument(bufor));
            konsumentThread.start();
        }
    }


    static class Bufor {
        private LinkedList<Integer> lista = new LinkedList<>();
        private int capacity;
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition pierwszyProd = lock.newCondition();
        private final Condition resztaProd = lock.newCondition();
        private final Condition pierwszyKons = lock.newCondition();
        private final Condition resztaKons = lock.newCondition();

        public Bufor(int capacity) {
            this.capacity = capacity * 2;
        }

        public void add(int ile, int wartosc) throws InterruptedException {
            lock.lock();
            try {
                while (lock.hasWaiters(pierwszyProd)) {
                    resztaProd.await();
                }

                while (capacity - lista.size() < ile) {
                    pierwszyProd.await();
                }

                for (int i = 0; i < ile; i++) {
                    lista.add(wartosc);
                }
                System.out.println("Producent " + Thread.currentThread().getId() + " dodał: " + wartosc + " , " + ile + " razy.");


                resztaProd.signal();
                pierwszyKons.signal();
            } finally {
                lock.unlock();
            }
        }

        public void remove(int ile) throws InterruptedException {
            lock.lock();
            try {
                while (lock.hasWaiters(pierwszyKons)) {
                    resztaKons.await();
                }

                while (lista.size() < ile) {
                    pierwszyKons.await();
                }

                for (int i = 0; i < ile; i++) {
                    int wartosc = lista.removeFirst();
                }
                System.out.println("Konsument " + Thread.currentThread().getId() + " zabrał: x" + " , " + ile + " razy.");

                resztaKons.signal();
                pierwszyProd.signal();
            } finally {
                lock.unlock();
            }
        }

        public int getHalfCapacity() {
            return capacity / 2;
        }
    }

    static class Producent implements Runnable {
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

    static class Konsument implements Runnable {
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

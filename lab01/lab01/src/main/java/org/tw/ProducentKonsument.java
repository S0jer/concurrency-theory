package org.tw;

import java.util.LinkedList;

public class ProducentKonsument {

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
        private int capacity;

        public Bufor(int capacity) {
            this.capacity = capacity;
        }

        public synchronized void add(int wartosc) throws InterruptedException {
            while (lista.size() == capacity) {
                wait();
            }

            lista.add(wartosc);
            System.out.println("Producent " + Thread.currentThread().getId() + " dodał: " + wartosc);
            notifyAll();
        }

        public synchronized int remove() throws InterruptedException {
            while (lista.isEmpty()) {
                wait();
            }

            int wartosc = lista.removeFirst();
            System.out.println("Konsument " + Thread.currentThread().getId() + " zabrał: " + wartosc);
            notifyAll();
            return wartosc;
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
                    int wartosc = (int) (Math.random() * 100);
                    bufor.add(wartosc);
//                    Thread.sleep((int) (Math.random() * 100));
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
                    bufor.remove();
//                    Thread.sleep((int) (Math.random() * 100));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


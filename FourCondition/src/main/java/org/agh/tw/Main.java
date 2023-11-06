package org.agh.tw;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static long realTimeStart;
    private static long cpuTimeStart;
    private static ThreadMXBean threadMXBean;

    public static void main(String[] args) {
        int producersNumber = 5;
        int consumersNumber = 5;
        int operationsCount = 1000;
        SharedResource resource = new SharedResource();
        threadMXBean = ManagementFactory.getThreadMXBean();

        Monitor4Condition monitor4Condition = new Monitor4Condition(producersNumber, consumersNumber, 10, operationsCount, resource);
        List<Thread> threads = new ArrayList<>();


        for (int i = 0; i < producersNumber; i++) {
            Thread producerThread = new Thread(new Producer(i, monitor4Condition, resource));
            threads.add(producerThread);
        }


        for (int i = 0; i < consumersNumber; i++) {
            Thread consumerThread = new Thread(new Consumer(i, monitor4Condition, resource));
            threads.add(consumerThread);
        }

        startTimers();

        for (Thread thread : threads) {
            thread.start();
        }

        while (!resource.shouldStop) {

        }
        printElapsedTime();

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        int consumerOperationsCount =  monitor4Condition.getConsumerOperationsCount();
        int producerOperationsCount = monitor4Condition.getProducerOperationsCount();

        System.out.println("producerOperationsCount: " + producerOperationsCount);
        System.out.println("consumerOperationsCount: " + consumerOperationsCount);
        int ops = (producerOperationsCount + consumerOperationsCount) / 10;
        System.out.println("Operations per second: " + ops);


    }

    private static void startTimers() {
        realTimeStart = System.nanoTime();
        cpuTimeStart = threadMXBean.getCurrentThreadCpuTime();
    }

    private static void printElapsedTime() {
        long realTimeEnd = System.nanoTime();
        long cpuTimeEnd = threadMXBean.getCurrentThreadCpuTime();
        System.out.println("Real Time taken: " + (realTimeEnd - realTimeStart) + " nanoseconds");
        System.out.println("CPU Time taken: " + (cpuTimeEnd - cpuTimeStart) + " nanoseconds");
    }

    // efektywne wykonywanie w czasie
    // przygotowanie oba kody do porównawczych obliczeń
    // generatory, żeby losowały w takim samym układzie ciąg danych
    // ile takich operacji zostało wykonanych w tym samym czasie
    // ile w ciagu 5 min udało nam sie wykonac operacji (produkcji/ konsumpcji)
    // ustawiamy iles operacji i ile zajmie czasu// mozna przedłuzac czas w miejscach synchronizowanych
    // (czy jezeli bedziemy czas wydłuzac to proporcje się zmienią między rozwiązaniami)
    // wymyslic sposób porównywaniaa// wykresy na tym własnym sprzecie// ile producentów, ilu konsumentów, jak sie zmieniaja (najpierw 1 1, potem 2 2, potem 100 100...)
    // czy wielkosc bufora ma wpływ na pomiary ?
    // jak bedzie sie wydajnosciowo zachowywac
    // co mierzymy?
    // jakie parametry uzywamy? (bufor, wielkość porcji. np bufor 100 elementów max porcja 10)
    // ile producentów, ilu konsumentów, jak sie zmieniaja (najpierw 1 1, potem 2 2, potem 100 100...)
    // zdefinjowac jak przeprowadzamy eksperymenty// czy generator ma wpływ? Czy jest odporny na wątki? czy jest on efektywny dla wielowątkowosci lib Concurrent -> inny genertator
    // w jaki sposób ten generator sie zachowuje lepiej czy gorzej (generator moze uzywac zmiennych współdzielonych)
    // Na nastepny raz wykresy z pomiarów

    public static class SharedResource {
        public volatile boolean shouldStop = false;
    }

}
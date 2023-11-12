package org.agh.tw;

import org.agh.tw.chart.BarChart;
import org.agh.tw.generators.ConcurrentGenerator;
import org.agh.tw.generators.IGenerator;
import org.agh.tw.generators.RandomGenerator;
import org.agh.tw.model.AllTime;
import org.agh.tw.monitors.IMonitor;
import org.agh.tw.monitors.Monitor4Condition;
import org.agh.tw.monitors.MonitorNestedLocks;
import org.agh.tw.test_cases.FixedOperationsTestCase;
import org.agh.tw.test_cases.FixedTimeTestCase;

import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final double NANO = 1_000_000_000D;
    private final static int NO_OPERATIONS_LIMIT = (int) Math.pow(10, 100);

    private static long realTimeStart;
    private static long cpuTimeStart;
    private static ThreadMXBean threadMXBean;

    public static void main(String[] args) {
//        compareGenerators();
//        compareBufferSize();
//        compareProdConsNumber();
//        compareMonitors();
//        compareOperationsCountToTime();


//        testFixedOperations4Cond(
//            concurrentGenerator,  400_000, 5, 5, 10, -1
//        );
//        System.out.println(Thread.activeCount());


        System.exit(0);
    }

    private static void compareGenerators() {
        ConcurrentGenerator concurrentGenerator = new ConcurrentGenerator();

        // 1. różne generatory stała ilość czasu, producentów, konsumentów, wielkość bufora
        List<Integer> resultsConcurrent = new ArrayList<>();
        List<Integer> resultsRandom = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            resultsConcurrent.add(testFixedTime4Cond(
                    concurrentGenerator, 1, 5, 5, 10, -1
            ));

            resultsRandom.add(testFixedTime4Cond(
                    new RandomGenerator(), 1, 5, 5, 10, -1
            ));
        }
        double avgConcurrent = resultsConcurrent.stream().mapToDouble(a -> a).average().orElse(0.0);
        double avgRandom = resultsRandom.stream().mapToDouble(a -> a).average().orElse(0.0);

        new BarChart(
                "Test", Arrays.asList("Concurrent", "Random"), Arrays.asList(avgConcurrent, avgRandom), "Średnia liczba opercji w 1 sekundzie dla różnych generatorów (4 cond)", "generatorsComp.jpg", "Typ generatora", "Średnia liczba wykonanych operacji"
        );
    }

    private static void compareBufferSize() {
        IGenerator generator = new ConcurrentGenerator();
        List<Integer> bufferSizes = Arrays.asList(2, 3, 4, 5, 10, 25, 50, 100, 250, 500, 1000);
        List<Double> results = new ArrayList<>();

        for (int bufferSize : bufferSizes) {
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += testFixedTime4Cond(
                        generator, 1, 5, 5, bufferSize, -1
                );
            }
            results.add((double) sum / 10);
        }

        new BarChart(
                "Compare Buffer Size",
                bufferSizes.stream().map(String::valueOf).toList(),
                results,
                "Compare Buffer Size",
                "compareBufferSize.jpg",
                "Wielkość buffora",
                "Średnia liczba wykonanych operacji"
        );
    }

    private static void compareProdConsNumber() {
        IGenerator generator = new ConcurrentGenerator();
        List<Integer> consumerProducerSizes = Arrays.asList(2, 3, 4, 5, 10, 25, 50, 100, 250, 500, 1000);
        List<Double> results = new ArrayList<>();

        for (int consumerSize : consumerProducerSizes) {
            for (int producerSize : consumerProducerSizes) {
                int sum = 0;
                for (int i = 0; i < 10; i++) {
                    sum += testFixedTime4Cond(
                            generator, 1, producerSize, consumerSize, 20, -1
                    );
                }
                results.add((double) (sum / 10));
            }
        }

        System.out.println(results);

        new BarChart(
                "Compare Prod Cons Number",
                consumerProducerSizes.stream().map(String::valueOf).toList(),
                results,
                "Compare Prod Cons Number",
                "compareProdConsNumber.jpg",
                "Wielkość buffora",
                "Średnia liczba wykonanych operacji"
        );

    }

    private static void compareMonitors() {
        ConcurrentGenerator concurrentGenerator = new ConcurrentGenerator();

        List<Integer> results4Cond = new ArrayList<>();
        List<Integer> resultsNestedLocks = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            results4Cond.add(testFixedTime4Cond(
                    concurrentGenerator, 1, 5, 5, 10, -1
            ));

            resultsNestedLocks.add(testFixedTimeNestedLocks(
                    concurrentGenerator, 1, 5, 5, 10, -1
            ));
        }

        double avg4Cond = results4Cond.stream().mapToDouble(a -> a).average().orElse(0.0);
        double avgNestedLocks = resultsNestedLocks.stream().mapToDouble(a -> a).average().orElse(0.0);

        new BarChart(
                "Compare monitors", Arrays.asList("4 Conditions", "Nested locks"), Arrays.asList(avg4Cond, avgNestedLocks), "Średnia liczba opercji w 1 sekundzie dla różnych monitorów (concurrent generator)", "monitorsComp.jpg", "Typ monitora", "Średnia liczba wykonanych operacji"
        );
    }

    private static void compareOperationsCountToTime() {
        IGenerator concurrentGenerator = new ConcurrentGenerator();
        List<Integer> resultsOperationsCount = new ArrayList<>();
        List<AllTime> resultsTimeCount = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            resultsOperationsCount.add(testFixedTime4Cond(
                    concurrentGenerator, 2, 5, 5, 10, -1
            ));
        }
        double avgOperationsCount = resultsOperationsCount.stream().mapToDouble(a -> a).average().orElse(0.0);

        for (int i = 0; i < 10; i++) {
            resultsTimeCount.add(testFixedOperations4Cond(
                    concurrentGenerator, (int) avgOperationsCount, 5, 5, 10, -1
            ));
        }

        double avgCpuTimeCount = resultsTimeCount.stream().mapToDouble(AllTime::getCpuTimeElapsed).average().orElse(0.0);
        double avgRealTimeCount = resultsTimeCount.stream().mapToDouble(AllTime::getRealTimeElapsed).average().orElse(0.0);

        new BarChart(
                "Compare Operations Count To Time",
                Arrays.asList("Operations", "Cpu Time", "Real Time"),
                Arrays.asList(avgOperationsCount, avgCpuTimeCount, avgRealTimeCount),
                "Średnia liczba opercji w 1 sekundzie dla różnych monitorów (concurrent generator)",
                "compareOperationsCountToTime.jpg",
                "Rodzaj danych",
                "Średnia liczba wykonanych operacji"
        );
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

    private static int testFixedTime4Cond(
            IGenerator generator, int seconds, int nProducers,
            int nConsumers, int buforCapacity, int maxPortion
    ) {
        SharedResource resource = new SharedResource();
        IMonitor monitor = new Monitor4Condition(
                nProducers, nConsumers, buforCapacity, NO_OPERATIONS_LIMIT, resource
        );

        FixedTimeTestCase testCase = new FixedTimeTestCase(
                monitor, generator, seconds, nProducers, nConsumers, resource
        );
        testCase.run();
        return testCase.operationsCount;
//        System.out.println("Operations count: " + testCase.operationsCount);
    }

    private static AllTime testFixedOperations4Cond(
            IGenerator generator, int operationsCount, int nProducers,
            int nConsumers, int buforCapacity, int maxPortion
    ) {
        SharedResource resource = new SharedResource();
        IMonitor monitor = new Monitor4Condition(
                nProducers, nConsumers, buforCapacity, operationsCount, resource
        );

        FixedOperationsTestCase testCase = new FixedOperationsTestCase(
                monitor, generator, nProducers, nConsumers, resource
        );
        testCase.run();
        return new AllTime(testCase.realTimeElapsed, testCase.cpuTimeElapsed);
//        System.out.println("Real time elapsed: " + testCase.realTimeElapsed / NANO  + " seconds");
//        System.out.println("CPU time elapsed: " + testCase.cpuTimeElapsed / NANO + " seconds");
    }

    private static void testFixedOperationsNestedLocks(
            IGenerator generator, int operationsCount, int nProducers,
            int nConsumers, int buforCapacity, int maxPortion
    ) {
        SharedResource resource = new SharedResource();
        IMonitor monitor = new MonitorNestedLocks(
                nProducers, nConsumers, buforCapacity, operationsCount, resource
        );

        FixedOperationsTestCase testCase = new FixedOperationsTestCase(
                monitor, generator, nProducers, nConsumers, resource
        );
        testCase.run();
        System.out.println("Real time elapsed: " + testCase.realTimeElapsed / NANO + " seconds");
        System.out.println("CPU time elapsed: " + testCase.cpuTimeElapsed / NANO + " seconds");
    }

    private static int testFixedTimeNestedLocks(
            IGenerator generator, int seconds, int nProducers,
            int nConsumers, int buforCapacity, int maxPortion
    ) {
        SharedResource resource = new SharedResource();
        IMonitor monitor = new MonitorNestedLocks(
                nProducers, nConsumers, buforCapacity, NO_OPERATIONS_LIMIT, resource
        );

        FixedTimeTestCase testCase = new FixedTimeTestCase(
                monitor, generator, seconds, nProducers, nConsumers, resource
        );
        testCase.run();
        return testCase.operationsCount;
//        System.out.println("Operations count: " + testCase.operationsCount);
    }
}
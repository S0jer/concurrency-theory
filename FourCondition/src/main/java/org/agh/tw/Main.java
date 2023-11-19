package org.agh.tw;

import org.agh.tw.chart.BarChart;
import org.agh.tw.chart.ScatterPlot;
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
import java.util.*;

public class Main {

    private static final double NANO = 1_000_000_000D;
    private final static int NO_OPERATIONS_LIMIT = (int) Math.pow(10, 100);

    private static long realTimeStart;
    private static long cpuTimeStart;
    private static ThreadMXBean threadMXBean;

    public static void main(String[] args) {
        compareGenerators();
        compareBufferSize();
        compareProdConsNumber();
        compareMonitors();
        compareOperationsCountToTime();

        System.exit(0);
    }

    private static void compareGenerators() {
        ConcurrentGenerator concurrentGenerator = new ConcurrentGenerator();
        List<Integer> resultsConcurrent = new ArrayList<>();
        List<Integer> resultsRandom = new ArrayList<>();
        List<Double> avgResultsConcurrent = new ArrayList<>();
        List<Double> avgResultsRandom = new ArrayList<>();
        List<Integer> nProdConsNumbers = Arrays.asList(5, 50, 100, 250, 500, 750, 1000);

        for (int nProdConsNumber : nProdConsNumbers) {
            for (int i = 0; i < 10; i++) {
                resultsConcurrent.add(testFixedTime4Cond(
                        concurrentGenerator, 1, nProdConsNumber, nProdConsNumber, 10, -1
                ));

                resultsRandom.add(testFixedTime4Cond(
                        new RandomGenerator(), 1, nProdConsNumber, nProdConsNumber, 10, -1
                ));
            }
            double avgConcurrent = resultsConcurrent.stream().mapToDouble(a -> a).average().orElse(0.0);
            double avgRandom = resultsRandom.stream().mapToDouble(a -> a).average().orElse(0.0);

            avgResultsConcurrent.add(avgConcurrent);
            avgResultsRandom.add(avgRandom);
        }

        ScatterPlot scatterPlot = new ScatterPlot(
                "Generator Performance Comparison",
                nProdConsNumbers,
                avgResultsConcurrent,
                avgResultsRandom,
                "Average Operation Count vs. Number of Producers/Consumers",
                "generatorsCompScatterPlot.jpg",
                "Number of Producers/Consumers",
                "Average Number of Operations"
        );
        scatterPlot.pack();
        scatterPlot.setVisible(true);
    }

    private static void compareBufferSize() {
        IGenerator generator = new ConcurrentGenerator();
        List<Integer> bufferSizes = Arrays.asList(2, 3, 4, 5, 10, 25, 50, 100, 250, 500, 1000);
        List<Integer> nProdConsNumbers = Arrays.asList(5, 50, 100, 250, 500, 750, 1000);
        Map<Integer, List<Double>> resultsMap = new HashMap<>();

        for (int bufferSize : bufferSizes) {
            List<Double> resultsForBufferSize = new ArrayList<>();
            for (int nProdConsNumber : nProdConsNumbers) {
                double sum = 0.0;
                for (int i = 0; i < 10; i++) {
                    sum += testFixedTime4Cond(
                            generator, 1, nProdConsNumber, nProdConsNumber, bufferSize, -1
                    );
                }
                double average = sum / 10.0;
                resultsForBufferSize.add(average);
            }
            resultsMap.put(bufferSize, resultsForBufferSize);
        }

        ScatterPlot scatterPlot = new ScatterPlot(
                "Comparison Chart",
                nProdConsNumbers,
                resultsMap,
                "Performance Comparison",
                "CompareBufferSize.jpg",
                "Number of Producers/Consumers",
                "Average Value"
        );
        scatterPlot.pack();
        scatterPlot.setVisible(true);
    }

    private static void compareProdConsNumber() {
        IGenerator generator = new ConcurrentGenerator();
        List<Integer> consumerProducerSizes = Arrays.asList(2, 3, 4, 5, 10, 25, 50, 100, 250, 500, 750, 1000);
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
        List<Integer> nProdConsNumbers = Arrays.asList(5, 50, 100, 250, 500, 750, 1000);
        List<Double> avg4CondsLits = new ArrayList<>();
        List<Double> avgNestedLocksList = new ArrayList<>();

        for (int nProdConsNumber : nProdConsNumbers) {
            for (int i = 0; i < 10; i++) {
                results4Cond.add(testFixedTime4Cond(
                        concurrentGenerator, 1, nProdConsNumber, nProdConsNumber, 10, -1
                ));

                resultsNestedLocks.add(testFixedTimeNestedLocks(
                        concurrentGenerator, 1, nProdConsNumber, nProdConsNumber, 10, -1
                ));
            }
            avg4CondsLits.add(results4Cond.stream().mapToDouble(a -> a).average().orElse(0.0));
            avgNestedLocksList.add(resultsNestedLocks.stream().mapToDouble(a -> a).average().orElse(0.0));
        }

        ScatterPlot scatterPlot = new ScatterPlot(
                "Comparison Chart",
                nProdConsNumbers,
                avg4CondsLits,
                avgNestedLocksList,
                "Performance Comparison",
                "PerformanceChart.jpg",
                "Number of Producers/Consumers",
                "Average Value"
        );
        scatterPlot.pack();
        scatterPlot.setVisible(true);
    }

    private static void compareOperationsCountToTime() {
        IGenerator concurrentGenerator = new ConcurrentGenerator();
        List<Integer> nProdConsNumbers = Arrays.asList(5, 50, 100, 250, 500, 750, 1000);
        Map<Integer, List<Double>> seriesData = new HashMap<>();
        seriesData.put(0, new ArrayList<>());
        seriesData.put(1, new ArrayList<>());

        for (int nProdConsNumber : nProdConsNumbers) {
            List<Integer> resultsOperationsCount = new ArrayList<>();
            List<AllTime> resultsTimeCount = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                resultsOperationsCount.add(testFixedTime4Cond(
                        concurrentGenerator, 2, nProdConsNumber, nProdConsNumber, 10, -1
                ));
            }

            double avgOperationsCount = resultsOperationsCount.stream().mapToDouble(a -> a).average().orElse(0.0);

            for (int i = 0; i < 10; i++) {
                resultsTimeCount.add(testFixedOperations4Cond(
                        concurrentGenerator, (int) avgOperationsCount, nProdConsNumber, nProdConsNumber, 10, -1
                ));
            }

            double avgCpuTime = resultsTimeCount.stream().mapToDouble(AllTime::getCpuTimeElapsed).average().orElse(0.0);
            double avgRealTime = resultsTimeCount.stream().mapToDouble(AllTime::getRealTimeElapsed).average().orElse(0.0);

            seriesData.get(0).add(avgCpuTime / NANO);
            seriesData.get(1).add(avgRealTime / NANO);
        }

        // Creating scatter plot
        ScatterPlot scatterPlot = new ScatterPlot(
                "Time Comparison vs. Number of Producers/Consumers",
                nProdConsNumbers,
                seriesData,
                "Time Comparison",
                "compareOperationsTime.jpg",
                "Number of Producers/Consumers",
                "Time (ns)"
        );
        scatterPlot.pack();
        scatterPlot.setVisible(true);
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
    }
}
package org.agh.tw.test_cases;

import org.agh.tw.SharedResource;
import org.agh.tw.generators.IGenerator;
import org.agh.tw.monitors.IMonitor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class FixedOperationsTestCase extends AbstractTestCase {
    private long realTimeStart;
    private long cpuTimeStart;

    public long realTimeElapsed;
    public long cpuTimeElapsed;

    private final ThreadMXBean threadMXBean;


    public FixedOperationsTestCase(IMonitor monitor, IGenerator generator, int nProducers, int nConsumers, SharedResource resource) {
        super(monitor, generator, nProducers, nConsumers, resource);
        threadMXBean = ManagementFactory.getThreadMXBean();
    }

    @Override
    protected void beforeStart() {
        realTimeStart = System.nanoTime();
        cpuTimeStart = threadMXBean.getCurrentThreadCpuTime();
    }

    @Override
    void stopCondition() {
        while(!resource.shouldStop) {}
    }

    @Override
    void saveResults() {
        realTimeElapsed = System.nanoTime() - realTimeStart;
        cpuTimeElapsed = threadMXBean.getCurrentThreadCpuTime() - cpuTimeStart;
    }
}

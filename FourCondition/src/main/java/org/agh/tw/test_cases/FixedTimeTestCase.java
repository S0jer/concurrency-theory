package org.agh.tw.test_cases;

import org.agh.tw.monitors.IMonitor;
import org.agh.tw.SharedResource;
import org.agh.tw.generators.IGenerator;


public class FixedTimeTestCase extends AbstractTestCase {
    private final int seconds;
    public int operationsCount;

    public FixedTimeTestCase(IMonitor monitor, IGenerator generator, int seconds, int nProducers, int nConsumers, SharedResource resource) {
        super(monitor, generator, nProducers, nConsumers, resource);
        this.seconds = seconds;
    }

    protected void stopCondition() {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void saveResults() {
        operationsCount = monitor.getProducerOperationsCount() + monitor.getConsumerOperationsCount();
    }

}

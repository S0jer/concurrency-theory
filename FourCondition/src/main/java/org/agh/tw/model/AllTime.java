package org.agh.tw.model;

public class AllTime {

    private long realTimeElapsed;
    private long cpuTimeElapsed;

    public AllTime(long realTimeElapsed, long cpuTimeElapsed) {
        this.realTimeElapsed = realTimeElapsed;
        this.cpuTimeElapsed = cpuTimeElapsed;
    }

    public long getRealTimeElapsed() {
        return realTimeElapsed;
    }

    public long getCpuTimeElapsed() {
        return cpuTimeElapsed;
    }
}

package org.agh.tw;

public class SharedResource {
    public volatile boolean shouldStop;

    public SharedResource() {
        this.shouldStop = false;
    }
}
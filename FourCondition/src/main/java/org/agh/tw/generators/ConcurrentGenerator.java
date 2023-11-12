package org.agh.tw.generators;

import java.util.concurrent.ThreadLocalRandom;

public class ConcurrentGenerator implements IGenerator {
    @Override
    public String getName() {
        return "java.util.concurrent.ThreadLocalRandom";
    }

    @Override
    public int generate(int min, int max) {
        // Check if min is greater than or equal to max
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}

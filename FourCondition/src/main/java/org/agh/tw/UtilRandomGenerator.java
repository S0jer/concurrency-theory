package org.agh.tw;

import java.util.Random;

public class UtilRandomGenerator implements IGenerator {

    private final Random random;

    public UtilRandomGenerator() {
        random = new Random(0);
    }
    @Override
    public String getName() {
        return "java.util.Random";
    }

    @Override
    public int generate(int min, int max) {
        // Check if min is greater than or equal to max
        if(min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return random.nextInt((max - min) + 1) + min;
    }
}

package org.agh.tw.generators;

import java.util.Random;

public class RandomGenerator implements IGenerator {

    private final Random random;

    public RandomGenerator() {
        random = new Random(0);
    }
    @Override
    public String getName() {
        return "java.util.Random";
    }

    @Override
    public int generate(int min, int max) {
        // Check if min is greater than or equal to max
        if(min > max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return random.nextInt((max - min) + 1) + min;
    }
}

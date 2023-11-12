package org.agh.tw.generators;

public interface IGenerator {
    String getName();
    int generate(int min, int max);
}

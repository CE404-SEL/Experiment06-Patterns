package edu.sharif.selab.strategy;

public interface TariffStrategy {
    long cost(long units);

    String name();
}
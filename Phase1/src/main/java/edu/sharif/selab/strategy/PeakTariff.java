package edu.sharif.selab.strategy;

public final class PeakTariff implements TariffStrategy {
    @Override
    public long cost(long units) {
        return units * 1000L;
    }

    @Override
    public String name() {
        return "Peak Hours";
    }
}
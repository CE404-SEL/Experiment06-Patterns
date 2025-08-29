package edu.sharif.selab.state;

public final class ShutdownState implements EnergyState {
    @Override
    public String name() {
        return "Shutdown";
    }

    @Override
    public double consumptionMultiplier() {
        return 0.0;
    }

    @Override
    public String enterMessage() {
        return "Shutdown state: all systems are off.";
    }
}
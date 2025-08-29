package edu.sharif.selab.state;


public final class ActiveState implements EnergyState {
    @Override
    public String name() {
        return "Active";
    }

    @Override
    public double consumptionMultiplier() {
        return 1.0;
    }

    @Override
    public String enterMessage() {
        return "Active state: all systems are running.";
    }
}

package edu.sharif.selab.state;


public final class EcoState implements EnergyState {
    @Override
    public String name() {
        return "Eco Mode";
    }

    @Override
    public double consumptionMultiplier() {
        return 0.5;
    }

    @Override
    public String enterMessage() {
        return "Eco state: only essential systems are running.";
    }

}
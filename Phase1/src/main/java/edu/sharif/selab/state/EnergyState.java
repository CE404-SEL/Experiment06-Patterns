package edu.sharif.selab.state;

public interface EnergyState {
    String name();

    double consumptionMultiplier();

    String enterMessage();
}
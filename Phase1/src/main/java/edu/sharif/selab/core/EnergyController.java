package edu.sharif.selab.core;

import edu.sharif.selab.state.EnergyState;

import java.util.Objects;

public class EnergyController {
    private EnergyState state;

    public EnergyController(EnergyState initial) {
        this.state = Objects.requireNonNull(initial);
    }

    public EnergyState getState() {
        return state;
    }

    public void setState(EnergyState newState) {
        this.state = Objects.requireNonNull(newState);
        System.out.println(newState.enterMessage());
    }

    public double multiplier() {
        return state.consumptionMultiplier();
    }

    public String status() {
        return state.name();
    }
}

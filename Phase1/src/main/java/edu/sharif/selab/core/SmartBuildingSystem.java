package edu.sharif.selab.core;

import edu.sharif.selab.state.ActiveState;
import edu.sharif.selab.state.EnergyState;
import edu.sharif.selab.strategy.StandardTariff;
import edu.sharif.selab.strategy.TariffStrategy;

public class SmartBuildingSystem {

    private final BillingService billing;
    private final EnergyController energy;

    public SmartBuildingSystem(BillingService billing, EnergyController energy) {
        this.billing = billing;
        this.energy = energy;
    }

    public static SmartBuildingSystem defaultSystem() {
        return new SmartBuildingSystem(
                new BillingService(new StandardTariff()),
                new EnergyController(new ActiveState())
        );
    }

    public void setTariff(TariffStrategy t) {
        billing.setTariff(t);
    }

    public TariffStrategy getTariff() {
        return billing.getTariff();
    }

    public void setState(EnergyState s) {
        energy.setState(s);
    }

    public EnergyState getState() {
        return energy.getState();
    }

    public long adjustedUnits(long rawUnits) {
        return Math.round(rawUnits * energy.multiplier());
    }

    public long simulateCost(long rawUnits) {
        long adjusted = adjustedUnits(rawUnits);
        return billing.costForAdjustedUnits(adjusted);
    }

    public String statusLine() {
        return "Status: " + energy.status()
                + " | Tariff: " + billing.getTariff().name()
                + " | Consumption multiplier: " + String.format("%.2f", energy.multiplier());
    }

}

package edu.sharif.selab.core;

import edu.sharif.selab.strategy.TariffStrategy;

import java.util.Objects;

public class BillingService {
    private TariffStrategy tariff;

    public BillingService(TariffStrategy initial) {
        this.tariff = Objects.requireNonNull(initial);
    }

    public void setTariff(TariffStrategy t) {
        this.tariff = Objects.requireNonNull(t);
    }

    public TariffStrategy getTariff() {
        return tariff;
    }

    public long costForAdjustedUnits(long adjustedUnits) {
        return tariff.cost(adjustedUnits);
    }
}

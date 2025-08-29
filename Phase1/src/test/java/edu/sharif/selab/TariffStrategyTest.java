package edu.sharif.selab;

import edu.sharif.selab.strategy.GreenTariff;
import edu.sharif.selab.strategy.PeakTariff;
import edu.sharif.selab.strategy.StandardTariff;
import edu.sharif.selab.strategy.TariffStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TariffStrategyTest {

    @Test
    void standardTariff_cost_is500PerUnit() {
        TariffStrategy t = new StandardTariff();
        assertEquals(5000L, t.cost(10));
    }

    @Test
    void peakTariff_cost_is1000PerUnit() {
        TariffStrategy t = new PeakTariff();
        assertEquals(3000L, t.cost(3));
    }

    @Test
    void greenTariff_cost_is300PerUnit() {
        TariffStrategy t = new GreenTariff();
        assertEquals(3000L, t.cost(10));
    }
}

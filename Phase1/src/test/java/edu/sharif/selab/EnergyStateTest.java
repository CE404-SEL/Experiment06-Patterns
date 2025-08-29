package edu.sharif.selab;

import edu.sharif.selab.state.ActiveState;
import edu.sharif.selab.state.EcoState;
import edu.sharif.selab.state.EnergyState;
import edu.sharif.selab.state.ShutdownState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnergyStateTest {

    @Test
    void active_multiplier_is1() {
        EnergyState s = new ActiveState();
        assertEquals(1.0, s.consumptionMultiplier(), 1e-9);
    }

    @Test
    void eco_multiplier_is0_5() {
        EnergyState s = new EcoState();
        assertEquals(0.5, s.consumptionMultiplier(), 1e-9);
    }

    @Test
    void shutdown_multiplier_is0() {
        EnergyState s = new ShutdownState();
        assertEquals(0.0, s.consumptionMultiplier(), 1e-9);
    }
}

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


    @Test
    void active_has_name_and_message() {
        EnergyState s = new ActiveState();
        assertEquals("Active", s.name());
        assertNotNull(s.enterMessage());
        assertTrue(s.enterMessage().contains("Active"));
    }

    @Test
    void eco_has_name_and_message() {
        EnergyState s = new EcoState();
        assertEquals("Eco Mode", s.name());
        assertNotNull(s.enterMessage());
        assertTrue(s.enterMessage().contains("Eco"));
    }

    @Test
    void shutdown_has_name_and_message() {
        EnergyState s = new ShutdownState();
        assertEquals("Shutdown", s.name());
        assertNotNull(s.enterMessage());
        assertTrue(s.enterMessage().contains("Shutdown"));
    }

    @Test
    void multipliers_within_0_and_1_and_ordered() {
        EnergyState active = new ActiveState();
        EnergyState eco = new EcoState();
        EnergyState shutdown = new ShutdownState();

        double a = active.consumptionMultiplier();
        double e = eco.consumptionMultiplier();
        double z = shutdown.consumptionMultiplier();

        assertTrue(0.0 <= a && a <= 1.0);
        assertTrue(0.0 <= e && e <= 1.0);
        assertTrue(0.0 <= z && z <= 1.0);

        assertTrue(a >= e, "Active باید >= Eco باشد");
        assertTrue(e >= z, "Eco باید >= Shutdown باشد");
    }
}

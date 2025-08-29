package edu.sharif.selab;

import edu.sharif.selab.core.SmartBuildingSystem;
import edu.sharif.selab.state.EcoState;
import edu.sharif.selab.state.ShutdownState;
import edu.sharif.selab.strategy.GreenTariff;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SmartBuildingSystemTest {

    @Test
    void active_standard_10units_cost_5000() {
        SmartBuildingSystem sys = SmartBuildingSystem.defaultSystem();
        assertEquals(5000L, sys.simulateCost(10));
        assertEquals(10L, sys.adjustedUnits(10));
    }

    @Test
    void eco_green_10units_cost_1500() {
        SmartBuildingSystem sys = SmartBuildingSystem.defaultSystem();
        sys.setState(new EcoState());
        sys.setTariff(new GreenTariff());
        assertEquals(5L, sys.adjustedUnits(10));
        assertEquals(1500L, sys.simulateCost(10));
    }

    @Test
    void shutdown_anyTariff_cost_zero() {
        SmartBuildingSystem sys = SmartBuildingSystem.defaultSystem();
        sys.setState(new ShutdownState());
        assertEquals(0L, sys.adjustedUnits(100));
        assertEquals(0L, sys.simulateCost(100));
    }
}

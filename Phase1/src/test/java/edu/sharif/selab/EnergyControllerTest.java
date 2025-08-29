package edu.sharif.selab;

import edu.sharif.selab.core.EnergyController;
import edu.sharif.selab.state.ActiveState;
import edu.sharif.selab.state.EcoState;
import edu.sharif.selab.state.ShutdownState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnergyControllerTest {

    @Test
    void initial_state_reflected_in_status_and_multiplier() {
        EnergyController controller = new EnergyController(new ActiveState());
        assertEquals("Active", controller.status());
        assertEquals(1.0, controller.multiplier(), 1e-9);
    }

    @Test
    void changing_to_eco_updates_status_and_multiplier() {
        EnergyController controller = new EnergyController(new ActiveState());
        controller.setState(new EcoState());
        assertEquals("Eco Mode", controller.status());
        assertEquals(0.5, controller.multiplier(), 1e-9);
    }

    @Test
    void changing_to_shutdown_updates_status_and_multiplier() {
        EnergyController controller = new EnergyController(new ActiveState());
        controller.setState(new ShutdownState());
        assertEquals("Shutdown", controller.status());
        assertEquals(0.0, controller.multiplier(), 1e-9);
    }
}

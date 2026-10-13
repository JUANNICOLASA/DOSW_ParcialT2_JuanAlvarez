package edu.dosw.parcial.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

class HealthControllerTest {

    private final HealthController controller = new HealthController();

    @Test
    void healthShouldReturnStatusUp() {
        Map<String, String> response = controller.health();

        assertEquals("UP", response.get("status"));
        assertEquals("DOSW-ParcialT2", response.get("service"));
    }
}

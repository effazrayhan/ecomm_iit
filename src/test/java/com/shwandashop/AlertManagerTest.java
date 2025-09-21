package com.shwandashop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlertManagerTest {
    private AlertManager testManager;

    @BeforeEach
    public void setUp() {
        testManager = AlertManager.getInstance();
    }

    @Test
    public void testAlertManagerInstance() {
        assertNotNull(testManager);
    }

    @Test
    public void testDummy() {
        // Placeholder test since checkAlerts() is private
        assertTrue(true);
    }
}

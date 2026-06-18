package com.proclaimer.ui.window

import kotlin.test.*

class MonitorDetectorTest {

    @Test
    fun testDetectMonitorsReturnsAtLeastOneDisplay() {
        val monitors = MonitorDetector.detectMonitors()
        assertTrue(monitors.isNotEmpty(), "Should detect at least one display monitor in GUI environment")
        
        monitors.forEach { monitor ->
            assertFalse(monitor.id.isBlank(), "Monitor id should not be blank")
            assertFalse(monitor.name.isBlank(), "Monitor name should not be blank")
            assertTrue(monitor.width > 0, "Monitor width must be positive")
            assertTrue(monitor.height > 0, "Monitor height must be positive")
        }
    }
}

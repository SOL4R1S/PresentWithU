package com.presentwithu.ui.window

import java.awt.GraphicsEnvironment

object MonitorDetector {
    fun detectMonitors(): List<MonitorInfo> {
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val screens = ge.screenDevices
        return screens.mapIndexed { index, device ->
            val bounds = device.defaultConfiguration.bounds
            MonitorInfo(
                id = device.iDstring ?: "display_$index",
                name = "Display ${index + 1} (${bounds.width}x${bounds.height})",
                width = bounds.width,
                height = bounds.height,
                x = bounds.x,
                y = bounds.y
            )
        }
    }
}

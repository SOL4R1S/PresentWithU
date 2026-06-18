package com.presentwithu.ui.window

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState

object PresentationWindowManager {
    fun getWindowStateForMonitor(
        monitorId: String,
        assignment: OutputAssignment?
    ): WindowState {
        val monitors = MonitorDetector.detectMonitors()
        val monitor = monitors.find { it.id == monitorId } ?: monitors.firstOrNull()

        if (monitor == null) {
            return WindowState(
                placement = WindowPlacement.Floating,
                size = DpSize(1024.dp, 768.dp)
            )
        }

        val width = assignment?.resolutionWidth ?: monitor.width
        val height = assignment?.resolutionHeight ?: monitor.height
        val isFullscreen = assignment?.fullscreen ?: true

        return WindowState(
            placement = if (isFullscreen) WindowPlacement.Maximized else WindowPlacement.Floating,
            size = DpSize(width.dp, height.dp),
            position = WindowPosition(monitor.x.dp, monitor.y.dp)
        )
    }
}

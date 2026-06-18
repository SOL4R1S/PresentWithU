package com.proclaimer.ui.window

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import java.awt.GraphicsEnvironment
import java.awt.Rectangle

/**
 * Describes a physical monitor attached to the system.
 */
data class Monitor(
    val id: String,
    val index: Int,
    val bounds: Rectangle,
    val isPrimary: Boolean,
    val name: String
) {
    val width: Int get() = bounds.width
    val height: Int get() = bounds.height
    val x: Int get() = bounds.x
    val y: Int get() = bounds.y
}

/**
 * Supported output resolutions. Values above 1920x1080 are upscaled.
 */
enum class OutputResolution(val width: Int, val height: Int, val label: String) {
    R_1280x720(1280, 720, "1280x720"),
    R_1920x1080(1920, 1080, "1920x1080"),
    R_2560x1440(2560, 1440, "2560x1440 (upscaled)"),
    R_3840x2160(3840, 2160, "3840x2160 (upscaled)"),
    R_NATIVE(0, 0, "Native monitor resolution");

    override fun toString(): String = label
}

/**
 * Configuration for a single output window (audience or stage).
 */
data class OutputWindowConfig(
    val monitorId: String? = null,
    val resolution: OutputResolution = OutputResolution.R_1920x1080,
    val fullscreen: Boolean = true,
    val fadeDurationMs: Int = 0
)

/**
 * Tracks which windows are currently open across the application.
 */
class WindowRegistry {
    private val _isControlWindowOpen = mutableStateOf(false)
    val isControlWindowOpen: State<Boolean> = _isControlWindowOpen

    private val _isAudienceWindowOpen = mutableStateOf(false)
    val isAudienceWindowOpen: State<Boolean> = _isAudienceWindowOpen

    private val _isStageWindowOpen = mutableStateOf(false)
    val isStageWindowOpen: State<Boolean> = _isStageWindowOpen

    fun openControlWindow() { _isControlWindowOpen.value = true }
    fun closeControlWindow() { _isControlWindowOpen.value = false }

    fun openAudienceWindow() { _isAudienceWindowOpen.value = true }
    fun closeAudienceWindow() { _isAudienceWindowOpen.value = false }

    fun openStageWindow() { _isStageWindowOpen.value = true }
    fun closeStageWindow() { _isStageWindowOpen.value = false }
}

/**
 * Detects attached monitors using AWT. This is the same on all desktop platforms.
 */
fun detectMonitors(): List<Monitor> {
    val env = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val devices = env.screenDevices
    val defaultDevice = env.defaultScreenDevice

    return devices.mapIndexed { index, device ->
        val config = device.defaultConfiguration
        val bounds = config.bounds
        Monitor(
            id = "${device.iDstring}_${bounds.x}_${bounds.y}",
            index = index,
            bounds = bounds,
            isPrimary = device == defaultDevice,
            name = device.iDstring
        )
    }
}

/**
 * Builds a [WindowState] for a window that should cover the given monitor.
 */
fun windowStateForMonitor(monitor: Monitor?, resolution: OutputResolution): WindowState {
    val size = when (resolution) {
        OutputResolution.R_NATIVE -> DpSize(monitor?.width?.dp ?: 1024.dp, monitor?.height?.dp ?: 768.dp)
        else -> DpSize(resolution.width.dp, resolution.height.dp)
    }
    return WindowState(
        size = size,
        placement = WindowPlacement.Floating,
        position = androidx.compose.ui.window.WindowPosition(
            x = (monitor?.x ?: 0).dp,
            y = (monitor?.y ?: 0).dp
        )
    )
}

/**
 * Returns the configured size in dp for the chosen resolution.
 */
fun OutputResolution.toDpSize(monitor: Monitor?): DpSize = windowStateForMonitor(monitor, this).size

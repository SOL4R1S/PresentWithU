package com.proclaimer.ui.window

import kotlinx.serialization.Serializable

enum class OutputType { AUDIENCE, STAGE }

@Serializable
data class MonitorInfo(
    val id: String,
    val name: String,
    val width: Int,
    val height: Int,
    val x: Int,
    val y: Int
)

@Serializable
data class OutputAssignment(
    val outputType: OutputType,
    val monitorId: String,
    val resolutionWidth: Int = 1920,
    val resolutionHeight: Int = 1080,
    val fullscreen: Boolean = true
)

@Serializable
data class AppConfig(
    val outputAssignments: List<OutputAssignment> = emptyList(),
    val fadeDurationMs: Int = 0,
    val controlSplitRatio: Float = 0.6f
)

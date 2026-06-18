package com.proclaimer.ui.state

import com.proclaimer.ui.window.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import androidx.compose.runtime.*

class WindowStateHolder(private val dataDir: File) {
    private val configFile = File(dataDir, "config.json")
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    var appConfig by mutableStateOf(AppConfig())
        private set

    var isControlOpen by mutableStateOf(false)
    var isAudienceOpen by mutableStateOf(false)
    var isStageOpen by mutableStateOf(false)

    init {
        loadConfig()
    }

    fun loadConfig() {
        if (configFile.exists()) {
            try {
                appConfig = json.decodeFromString<AppConfig>(configFile.readText())
                validateAssignments()
            } catch (e: Exception) {
                appConfig = AppConfig()
            }
        }
    }

    fun saveConfig() {
        try {
            dataDir.mkdirs()
            configFile.writeText(json.encodeToString(appConfig))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateAssignments(assignments: List<OutputAssignment>) {
        appConfig = appConfig.copy(outputAssignments = assignments)
        validateAssignments()
        saveConfig()
    }

    fun updateFadeDuration(durationMs: Int) {
        appConfig = appConfig.copy(fadeDurationMs = durationMs)
        saveConfig()
    }

    fun updateControlSplitRatio(ratio: Float) {
        appConfig = appConfig.copy(controlSplitRatio = ratio)
        saveConfig()
    }

    private fun validateAssignments() {
        val activeMonitors = MonitorDetector.detectMonitors()
        val activeIds = activeMonitors.map { it.id }.toSet()

        val validated = appConfig.outputAssignments.map { assignment ->
            if (assignment.monitorId !in activeIds && activeMonitors.isNotEmpty()) {
                assignment.copy(monitorId = activeMonitors.first().id)
            } else {
                assignment
            }
        }
        appConfig = appConfig.copy(outputAssignments = validated)
    }
}

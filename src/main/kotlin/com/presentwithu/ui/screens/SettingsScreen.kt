package com.presentwithu.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.presentwithu.ui.window.*
import com.presentwithu.ui.state.WindowStateHolder

@Composable
fun SettingsScreen(
    windowStateHolder: WindowStateHolder,
    modifier: Modifier = Modifier
) {
    var monitors by remember { mutableStateOf(MonitorDetector.detectMonitors()) }
    val config = windowStateHolder.appConfig
    val assignments = config.outputAssignments

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Output & Monitor Settings", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
            Button(onClick = { monitors = MonitorDetector.detectMonitors() }) {
                Text("Refresh Monitors")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Display Mapping", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            }

            items(monitors) { monitor ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(monitor.name, style = MaterialTheme.typography.bodyLarge)
                        Text("Bounds: X=${monitor.x}, Y=${monitor.y}, Size=${monitor.width}x${monitor.height}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        Spacer(Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Assign Output:")
                            
                            val audienceAssigned = assignments.find { it.monitorId == monitor.id && it.outputType == OutputType.AUDIENCE } != null
                            val stageAssigned = assignments.find { it.monitorId == monitor.id && it.outputType == OutputType.STAGE } != null
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = audienceAssigned,
                                    onClick = {
                                        val filtered = assignments.filter { it.outputType != OutputType.AUDIENCE }
                                        val newAssignment = OutputAssignment(
                                            outputType = OutputType.AUDIENCE,
                                            monitorId = monitor.id,
                                            resolutionWidth = monitor.width,
                                            resolutionHeight = monitor.height
                                        )
                                        windowStateHolder.updateAssignments(filtered + newAssignment)
                                    }
                                )
                                Text("Audience")
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = stageAssigned,
                                    onClick = {
                                        val filtered = assignments.filter { it.outputType != OutputType.STAGE }
                                        val newAssignment = OutputAssignment(
                                            outputType = OutputType.STAGE,
                                            monitorId = monitor.id,
                                            resolutionWidth = monitor.width,
                                            resolutionHeight = monitor.height
                                        )
                                        windowStateHolder.updateAssignments(filtered + newAssignment)
                                    }
                                )
                                Text("Stage")
                            }

                            if (audienceAssigned || stageAssigned) {
                                TextButton(
                                    onClick = {
                                        val newAssignments = assignments.filter { it.monitorId != monitor.id }
                                        windowStateHolder.updateAssignments(newAssignments)
                                    }
                                ) {
                                    Text("Unassign", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("Transition Settings", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Fade Transition Duration (ms)")
                            Text("${config.fadeDurationMs} ms", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                        Slider(
                            value = config.fadeDurationMs.toFloat(),
                            onValueChange = { windowStateHolder.updateFadeDuration(it.toInt()) },
                            valueRange = 0f..2000f,
                            steps = 20
                        )
                    }
                }
            }
        }
    }
}

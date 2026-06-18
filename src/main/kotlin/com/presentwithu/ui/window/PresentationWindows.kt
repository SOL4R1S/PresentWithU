package com.presentwithu.ui.window

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.presentwithu.ui.state.*
import com.presentwithu.ui.screens.*
import com.presentwithu.ui.components.OutputDisplay
import androidx.compose.ui.unit.dp
import com.presentwithu.model.toLibraryItem

@Composable
fun PresentationWindows(
    stateHolder: MainStateHolder,
    windowStateHolder: WindowStateHolder
) {
    val config = windowStateHolder.appConfig
    val slides by stateHolder.slides.collectAsState()
    val currentSlideIndex by stateHolder.currentSlideIndex.collectAsState()

    // 1. Control Window
    if (windowStateHolder.isControlOpen) {
        Window(
            onCloseRequest = { windowStateHolder.isControlOpen = false },
            title = "Presenter Control",
            state = rememberWindowState(width = 1000.dp, height = 700.dp)
        ) {
            ControlScreen(stateHolder = stateHolder)
        }
    }

    // 2. Audience Output Window
    if (windowStateHolder.isAudienceOpen) {
        val assignment = config.outputAssignments.find { it.outputType == OutputType.AUDIENCE }
        val monitorId = assignment?.monitorId ?: ""
        val windowState = remember(monitorId) {
            PresentationWindowManager.getWindowStateForMonitor(monitorId, assignment)
        }

        Window(
            onCloseRequest = { windowStateHolder.isAudienceOpen = false },
            title = "Audience Display",
            state = windowState,
            undecorated = assignment?.fullscreen ?: true,
            alwaysOnTop = true
        ) {
            val currentItem = slides.getOrNull(currentSlideIndex)?.toLibraryItem()
            OutputDisplay(
                item = currentItem,
                isStageDisplay = false,
                fadeDurationMs = config.fadeDurationMs
            )
        }
    }

    // 3. Stage Display Window
    if (windowStateHolder.isStageOpen) {
        val assignment = config.outputAssignments.find { it.outputType == OutputType.STAGE }
        val monitorId = assignment?.monitorId ?: ""
        val windowState = remember(monitorId) {
            PresentationWindowManager.getWindowStateForMonitor(monitorId, assignment)
        }

        Window(
            onCloseRequest = { windowStateHolder.isStageOpen = false },
            title = "Stage Display",
            state = windowState,
            undecorated = assignment?.fullscreen ?: true
        ) {
            val currentItem = slides.getOrNull(currentSlideIndex)?.toLibraryItem()
            val nextItem = slides.getOrNull(currentSlideIndex + 1)?.toLibraryItem()
            
            OutputDisplay(
                item = currentItem,
                nextItem = nextItem,
                isStageDisplay = true,
                fadeDurationMs = 0
            )
        }
    }
}

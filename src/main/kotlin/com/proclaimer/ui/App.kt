package com.proclaimer.ui

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.proclaimer.data.SongRepository
import com.proclaimer.model.Slide
import com.proclaimer.ui.screens.MainScreen
import com.proclaimer.ui.screens.PresenterScreen
import com.proclaimer.ui.screens.StageDisplayScreen
import com.proclaimer.ui.theme.ProclaimerTheme
import java.io.File

@Composable
fun App() {
    val dataDir = File(System.getProperty("user.home"), ".proclaimer")
    val repository = remember { SongRepository(dataDir) }

    // Application state
    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Main) }
    var slides by remember { mutableStateOf<List<Slide>>(emptyList()) }
    var currentSlideIndex by remember { mutableStateOf(0) }

    ProclaimerTheme(darkTheme = true) {
        when (screen) {
            is AppScreen.Main -> {
                MainScreen(
                    repository = repository,
                    onStartPresentation = { s ->
                        slides = s
                        currentSlideIndex = 0
                        screen = AppScreen.Presenter
                    },
                    onOpenStageDisplay = { s, idx ->
                        slides = s
                        currentSlideIndex = idx
                        screen = AppScreen.StageDisplay
                    }
                )
            }

            is AppScreen.Presenter -> {
                PresenterScreen(
                    slides = slides,
                    initialIndex = currentSlideIndex,
                    onClose = {
                        screen = AppScreen.Main
                    },
                    onOpenStageDisplay = { s, idx ->
                        currentSlideIndex = idx
                        screen = AppScreen.StageDisplay
                    }
                )
            }

            is AppScreen.StageDisplay -> {
                StageDisplayScreen(
                    slides = slides,
                    currentIndex = currentSlideIndex,
                    onClose = {
                        screen = AppScreen.Presenter
                    }
                )
            }
        }
    }
}

sealed class AppScreen {
    data object Main : AppScreen()
    data object Presenter : AppScreen()
    data object StageDisplay : AppScreen()
}

package com.proclaimer.ui

import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import com.proclaimer.data.SongRepository
import com.proclaimer.model.Slide
import com.proclaimer.ui.screens.MainScreen
import com.proclaimer.ui.screens.PresenterScreen
import com.proclaimer.ui.screens.StageDisplayScreen
import com.proclaimer.ui.theme.ProclaimerTheme
import java.io.File
import androidx.compose.runtime.rememberCoroutineScope
import com.proclaimer.ui.state.MainStateHolder

@Composable
fun App() {
    val dataDir = File(System.getProperty("user.home"), ".proclaimer")
    val repository = remember { SongRepository(dataDir) }
    val scope = rememberCoroutineScope()
    val stateHolder = remember { MainStateHolder(repository, scope) }

    // Application state
    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Main) }
    var slides by remember { mutableStateOf<List<Slide>>(emptyList()) }
    var currentSlideIndex by remember { mutableStateOf(0) }

    ProclaimerTheme(darkTheme = true) {
        when (screen) {
            is AppScreen.Main -> {
                MainScreen(
                    stateHolder = stateHolder,
                    onStartPresentation = {
                        slides = stateHolder.slides.value
                        currentSlideIndex = stateHolder.currentSlideIndex.value
                        screen = AppScreen.Presenter
                    },
                    onOpenStageDisplay = {
                        slides = stateHolder.slides.value
                        currentSlideIndex = stateHolder.currentSlideIndex.value
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

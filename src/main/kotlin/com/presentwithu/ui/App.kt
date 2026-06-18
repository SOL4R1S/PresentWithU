package com.presentwithu.ui

import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import com.presentwithu.data.SongRepository
import com.presentwithu.model.Slide
import com.presentwithu.ui.screens.MainScreen
import com.presentwithu.ui.screens.PresenterScreen
import com.presentwithu.ui.screens.StageDisplayScreen
import com.presentwithu.ui.state.MainStateHolder
import com.presentwithu.ui.state.WindowStateHolder
import com.presentwithu.ui.window.PresentationWindows
import com.presentwithu.ui.theme.PresentWithUTheme
import java.io.File
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun App() {
    val dataDir = File(System.getProperty("user.home"), ".presentwithu")
    val repository = remember { SongRepository(dataDir) }
    val scope = rememberCoroutineScope()
    val stateHolder = remember { MainStateHolder(repository, scope) }
    val windowStateHolder = remember { WindowStateHolder(dataDir) }

    // Application state
    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Main) }
    var slides by remember { mutableStateOf<List<Slide>>(emptyList()) }
    var currentSlideIndex by remember { mutableStateOf(0) }

    PresentWithUTheme(darkTheme = true) {
        PresentationWindows(stateHolder = stateHolder, windowStateHolder = windowStateHolder)

        when (screen) {
            is AppScreen.Main -> {
                MainScreen(
                    stateHolder = stateHolder,
                    windowStateHolder = windowStateHolder,
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

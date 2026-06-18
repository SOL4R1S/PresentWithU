package com.presentwithu

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.presentwithu.ui.App

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(1400.dp, 900.dp)
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "PresentWithU — Church Presentation Software"
    ) {
        App()
    }
}

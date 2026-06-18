package com.proclaimer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.proclaimer.ui.components.*
import com.proclaimer.ui.state.MainStateHolder

@Composable
fun ControlScreen(
    stateHolder: MainStateHolder,
    modifier: Modifier = Modifier
) {
    var splitRatio by remember { mutableStateOf(0.6f) } // 60:40 split

    DragAndDropProvider {
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val totalWidth = maxWidth

            Row(modifier = Modifier.fillMaxSize()) {
                // Left Panel: Setlist Panel (owns splitRatio)
                SetlistPanel(
                    stateHolder = stateHolder,
                    onSelect = { item, idx ->
                        stateHolder.selectSetlistItem(idx)
                        stateHolder.loadLibraryItemToSlides(item)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(totalWidth * splitRatio)
                )

                // Drag Splitter Divider
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(8.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val deltaRatio = dragAmount.x / size.width.toFloat()
                                // Clamp splitRatio between 0.3 and 0.8
                                splitRatio = (splitRatio + deltaRatio).coerceIn(0.3f, 0.8f)
                            }
                        }
                )

                // Right Panel: Library Browser
                LibraryBrowser(
                    stateHolder = stateHolder,
                    onAddToSetlist = { item ->
                        stateHolder.addToSetlist(item)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        }
    }
}

package com.proclaimer.ui.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import com.proclaimer.model.LibraryItem

@Stable
class DragAndDropState {
    var draggedItem by mutableStateOf<LibraryItem?>(null)
    var isDragging by mutableStateOf(false)
    var dragOffset by mutableStateOf(Offset.Zero)
}

val LocalDragAndDropState = staticCompositionLocalOf { DragAndDropState() }

@Composable
fun DragAndDropProvider(content: @Composable () -> Unit) {
    val state = remember { DragAndDropState() }
    CompositionLocalProvider(LocalDragAndDropState provides state, content = content)
}

fun Modifier.dragSource(
    item: LibraryItem,
    state: DragAndDropState
): Modifier = this.pointerInput(item) {
    detectDragGesturesAfterLongPress(
        onDragStart = { offset ->
            state.draggedItem = item
            state.isDragging = true
            state.dragOffset = Offset.Zero
        },
        onDrag = { change, dragAmount ->
            change.consume()
            state.dragOffset += dragAmount
        },
        onDragEnd = {
            state.isDragging = false
            state.draggedItem = null
        },
        onDragCancel = {
            state.isDragging = false
            state.draggedItem = null
        }
    )
}

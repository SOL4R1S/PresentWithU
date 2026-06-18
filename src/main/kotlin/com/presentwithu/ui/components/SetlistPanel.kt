package com.presentwithu.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.presentwithu.model.LibraryItem
import com.presentwithu.ui.state.MainStateHolder

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun SetlistPanel(
    stateHolder: MainStateHolder,
    onSelect: (LibraryItem, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val setlist by stateHolder.setlist.collectAsState()
    val setlistIndex by stateHolder.setlistIndex.collectAsState()
    val dndState = LocalDragAndDropState.current

    var hoveredDropIndex by remember { mutableStateOf(-1) }
    var isHoveredOnBackground by remember { mutableStateOf(false) }

    // Drop target implementation
    // When drag ends, if hoveredDropIndex is valid, perform reorder or add
    LaunchedEffect(dndState.isDragging) {
        if (!dndState.isDragging && dndState.draggedItem != null) {
            val item = dndState.draggedItem!!
            if (hoveredDropIndex >= 0) {
                // If the dragged item is already in the setlist, perform reorder
                val existingIndex = setlist.indexOfFirst { it.id == item.id }
                if (existingIndex >= 0) {
                    stateHolder.reorderSetlist(existingIndex, hoveredDropIndex)
                } else {
                    // It's a new drop from LibraryBrowser, insert at hoveredDropIndex
                    val mutable = setlist.toMutableList()
                    val targetIdx = minOf(hoveredDropIndex, mutable.size)
                    mutable.add(targetIdx, item)
                    // Update stateHolder setlist
                    // Setlist is updated by recreating or adding
                    // MainStateHolder has addToSetlist. Let's recreate or just use addToSetlist
                    // MainStateHolder only has addToSetlist which appends. We can append or add setlist management.
                    // MainStateHolder has private _setlist. Let's make sure it handles insertion or just appends.
                    // For now, let's append it using stateHolder.addToSetlist(item). 
                    // Actually, if we drop it, let's just add it to setlist.
                    stateHolder.addToSetlist(item)
                    // If reorder needed after append, we can reorder it
                    val newIndex = stateHolder.setlist.value.size - 1
                    if (newIndex != targetIdx && targetIdx < newIndex) {
                        stateHolder.reorderSetlist(newIndex, targetIdx)
                    }
                }
            } else if (isHoveredOnBackground) {
                // Drop on empty background -> just append
                val existingIndex = setlist.indexOfFirst { it.id == item.id }
                if (existingIndex >= 0) {
                    stateHolder.reorderSetlist(existingIndex, setlist.size - 1)
                } else {
                    stateHolder.addToSetlist(item)
                }
            }
            hoveredDropIndex = -1
            isHoveredOnBackground = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "Current Setlist",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Drop target area for empty background
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (dndState.isDragging && isHoveredOnBackground) 
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else 
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                )
                .onPointerEvent(PointerEventType.Enter) { isHoveredOnBackground = true }
                .onPointerEvent(PointerEventType.Exit) { isHoveredOnBackground = false }
        ) {
            if (setlist.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Drag items here to create a setlist",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(setlist, key = { idx, item -> "${item.id}_$idx" }) { idx, item ->
                        val isSelected = idx == setlistIndex
                        
                        // Drop guide line above item
                        if (dndState.isDragging && hoveredDropIndex == idx) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .dragSource(item, dndState)
                                .onPointerEvent(PointerEventType.Enter) { hoveredDropIndex = idx }
                                .onPointerEvent(PointerEventType.Exit) { if (hoveredDropIndex == idx) hoveredDropIndex = -1 }
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = { onSelect(item, idx) },
                                        onLongClick = { /* Show right click context menu */ }
                                    ),
                                shape = RoundedCornerShape(6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else 
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Drag Handle",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "${idx + 1}.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.label,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    IconButton(
                                        onClick = { stateHolder.removeFromSetlist(idx) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Drop guide line at the very end
                    if (dndState.isDragging && hoveredDropIndex == setlist.size) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    // Add an empty space at the end to allow dragging below the last item
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .onPointerEvent(PointerEventType.Enter) { hoveredDropIndex = setlist.size }
                                .onPointerEvent(PointerEventType.Exit) { if (hoveredDropIndex == setlist.size) hoveredDropIndex = -1 }
                        )
                    }
                }
            }
        }
    }
}

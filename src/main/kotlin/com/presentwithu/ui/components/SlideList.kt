package com.presentwithu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.presentwithu.model.Slide
import com.presentwithu.model.SlideType

@Composable
fun SlideListPanel(
    slides: List<Slide>,
    currentIndex: Int,
    onSelect: (Int) -> Unit,
    onAdd: () -> Unit,
    onDelete: (Int) -> Unit,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxHeight().padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Slides",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${slides.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            // Slide list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(slides) { index, slide ->
                    SlideListItem(
                        slide = slide,
                        index = index,
                        isCurrent = index == currentIndex,
                        onSelect = { onSelect(index) },
                        onDelete = { onDelete(index) },
                        onMoveUp = { if (index > 0) onMoveUp(index) },
                        onMoveDown = { if (index < slides.size - 1) onMoveDown(index) },
                        isFirst = index == 0,
                        isLast = index == slides.size - 1
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Add slide button
            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add Slide")
            }
        }
    }
}

@Composable
private fun SlideListItem(
    slide: Slide,
    index: Int,
    isCurrent: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean
) {
    val bgColor = if (isCurrent)
        MaterialTheme.colorScheme.primaryContainer
    else
        Color.Transparent

    val typeIcon = when (slide.type) {
        SlideType.LYRIC -> Icons.Default.MusicNote
        SlideType.SCRIPTURE -> Icons.AutoMirrored.Filled.MenuBook
        SlideType.ANNOUNCEMENT -> Icons.Default.Campaign
        SlideType.TITLE -> Icons.Default.Title
        SlideType.BLANK -> Icons.Default.Brightness1
        SlideType.IMAGE -> Icons.Default.Image
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Slide number
            Text(
                "${index + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isCurrent) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(20.dp),
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
            )

            // Type icon
            Icon(
                typeIcon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (isCurrent) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.width(6.dp))

            // Preview text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    slide.content.lines().firstOrNull()?.take(30) ?: "Empty",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal
                )
                if (slide.content.lines().size > 1) {
                    Text(
                        "+${slide.content.lines().size - 1} more lines",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Move up
            IconButton(
                onClick = onMoveUp,
                modifier = Modifier.size(20.dp),
                enabled = !isFirst
            ) {
                Icon(Icons.Default.KeyboardArrowUp, "Up", modifier = Modifier.size(14.dp))
            }

            // Move down
            IconButton(
                onClick = onMoveDown,
                modifier = Modifier.size(20.dp),
                enabled = !isLast
            ) {
                Icon(Icons.Default.KeyboardArrowDown, "Down", modifier = Modifier.size(14.dp))
            }

            // Delete
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    "Delete",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

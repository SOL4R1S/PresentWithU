package com.presentwithu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import com.presentwithu.model.Slide
import com.presentwithu.model.SlideType

@Composable
fun SlideEditor(
    slide: Slide?,
    onUpdate: (Slide) -> Unit,
    modifier: Modifier = Modifier
) {
    if (slide == null) {
        Box(
            modifier = modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Select or add a slide",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    var content by remember(slide.id) { mutableStateOf(slide.content) }
    var note by remember(slide.id) { mutableStateOf(slide.note) }
    var selectedType by remember(slide.id) { mutableStateOf(slide.type) }
    var bgColor by remember(slide.id) { mutableStateOf(slide.backgroundColor) }

    // Update parent when changes happen
    fun pushUpdate() {
        onUpdate(
            slide.copy(
                content = content,
                note = note,
                type = selectedType,
                backgroundColor = bgColor
            )
        )
    }

    Surface(
        modifier = modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Title
            Text(
                "Slide Editor",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(16.dp))

            // Slide Type selector
            Text("Slide Type", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SlideType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            pushUpdate()
                        },
                        label = { Text(type.name, fontSize = MaterialTheme.typography.labelSmall.fontSize) },
                        leadingIcon = {
                            Icon(
                                when (type) {
                                    SlideType.LYRIC -> Icons.Default.MusicNote
                                    SlideType.SCRIPTURE -> Icons.AutoMirrored.Filled.MenuBook
                                    SlideType.ANNOUNCEMENT -> Icons.Default.Campaign
                                    SlideType.TITLE -> Icons.Default.Title
                                    SlideType.BLANK -> Icons.Default.Brightness1
                                    SlideType.IMAGE -> Icons.Default.Image
                                },
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Content text area
            Text("Content", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                    pushUpdate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                shape = RoundedCornerShape(8.dp),
                placeholder = { Text("Enter slide content…\nUse blank lines for slide transitions.") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(Modifier.height(12.dp))

            // Notes
            Text("Notes", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = note,
                onValueChange = {
                    note = it
                    pushUpdate()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                placeholder = { Text("Stage display notes…") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(Modifier.height(16.dp))

            // Background color picker (simple)
            Text("Background Color", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val presets = listOf(
                    "#1a1a2e" to "Dark Navy",
                    "#0a0a1a" to "Deep Black",
                    "#2d1b4e" to "Royal Purple",
                    "#1b3a2e" to "Forest",
                    "#3a1b1b" to "Wine",
                    "#1a2e3a" to "Ocean",
                    "#ffffff" to "White",
                    "#000000" to "Black"
                )
                // Show first 8 presets
                presets.take(8).forEach { (colorHex, label) ->
                    val isSelected = bgColor.equals(colorHex, ignoreCase = true)
                    val color = try {
                        Color(colorHex.replace("#", "").toLong(16) or 0xFF000000)
                    } catch (e: Exception) {
                        Color(0xFF1a1a2e)
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color)
                            .then(
                                if (isSelected) Modifier.background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    RoundedCornerShape(6.dp)
                                ) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = if (colorHex == "#ffffff") Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Preview
            Text("Preview", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            SlidePreview(slide = slide.copy(content = content, type = selectedType, backgroundColor = bgColor))
        }
    }
}

@Composable
fun SlidePreview(slide: Slide, modifier: Modifier = Modifier) {
    val bgColor = try {
        Color(slide.backgroundColor.replace("#", "").toLong(16) or 0xFF000000)
    } catch (e: Exception) {
        MaterialTheme.colorScheme.background
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            if (slide.type == SlideType.TITLE) {
                Text(
                    slide.content,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                slide.content.lines().forEach { line ->
                    Text(
                        line,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        // Type label
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ) {
            Text(
                slide.type.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

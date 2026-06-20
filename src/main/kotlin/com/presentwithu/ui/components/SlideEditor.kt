package com.presentwithu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var textColor by remember(slide.id) { mutableStateOf(slide.textColor) }
    var fontSize by remember(slide.id) { mutableStateOf(slide.fontSize) }
    var fontFamily by remember(slide.id) { mutableStateOf(slide.fontFamily) }
    var alignment by remember(slide.id) { mutableStateOf(slide.alignment) }
    var positionX by remember(slide.id) { mutableStateOf(slide.positionX) }
    var positionY by remember(slide.id) { mutableStateOf(slide.positionY) }
    var boxWidth by remember(slide.id) { mutableStateOf(slide.boxWidth) }
    var boxHeight by remember(slide.id) { mutableStateOf(slide.boxHeight) }

    // Update parent when changes happen
    fun pushUpdate() {
        onUpdate(
            slide.copy(
                content = content,
                note = note,
                type = selectedType,
                backgroundColor = bgColor,
                textColor = textColor,
                fontSize = fontSize,
                fontFamily = fontFamily,
                alignment = alignment,
                positionX = positionX,
                positionY = positionY,
                boxWidth = boxWidth,
                boxHeight = boxHeight
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
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(Modifier.height(12.dp))

            Text("Text Formatting (PPT style)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))

            // Text Color presets
            Text("Text Color", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val textPresets = listOf(
                    "#FFFFFF" to "White",
                    "#FFFF00" to "Yellow",
                    "#FF4A4A" to "Red",
                    "#4AFF4A" to "Green",
                    "#4A90E2" to "Blue",
                    "#000000" to "Black"
                )
                textPresets.forEach { (colorHex, name) ->
                    val isSelected = textColor.equals(colorHex, ignoreCase = true)
                    val color = try {
                        Color(colorHex.replace("#", "").toLong(16) or 0xFF000000)
                    } catch (e: Exception) {
                        Color.White
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color)
                            .then(
                                if (isSelected) Modifier.background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    RoundedCornerShape(6.dp)
                                ) else Modifier
                            )
                            .clickable {
                                textColor = colorHex
                                pushUpdate()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = if (colorHex == "#FFFFFF" || colorHex == "#FFFF00") Color.Black else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Font Family & Alignment Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Font Family", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(6.dp))
                    var showFontMenu by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { showFontMenu = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(fontFamily)
                        }
                        DropdownMenu(expanded = showFontMenu, onDismissRequest = { showFontMenu = false }) {
                            listOf("SansSerif", "Serif", "Monospace").forEach { font ->
                                DropdownMenuItem(
                                    text = { Text(font) },
                                    onClick = {
                                        fontFamily = font
                                        showFontMenu = false
                                        pushUpdate()
                                    }
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Alignment", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Left", "Center", "Right").forEach { align ->
                            val isSelected = alignment == align
                            OutlinedButton(
                                onClick = {
                                    alignment = align
                                    pushUpdate()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                ),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(align.take(1), fontWeight = FontWeight.Bold, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface) // L, C, R
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Font Size Slider
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Font Size: ${fontSize}sp", style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(120.dp))
                Slider(
                    value = fontSize.toFloat(),
                    onValueChange = {
                        fontSize = it.toInt()
                        pushUpdate()
                    },
                    valueRange = 16f..120f,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            Spacer(Modifier.height(12.dp))

            Text("Text Box Position & Size (PPT scale)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // X & Y position
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Position X: ${(positionX * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(110.dp))
                Slider(
                    value = positionX,
                    onValueChange = {
                        positionX = it
                        pushUpdate()
                    },
                    valueRange = 0.0f..0.9f,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Position Y: ${(positionY * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(110.dp))
                Slider(
                    value = positionY,
                    onValueChange = {
                        positionY = it
                        pushUpdate()
                    },
                    valueRange = 0.0f..0.9f,
                    modifier = Modifier.weight(1f)
                )
            }

            // Width & Height size
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Box Width: ${(boxWidth * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(110.dp))
                Slider(
                    value = boxWidth,
                    onValueChange = {
                        boxWidth = it
                        pushUpdate()
                    },
                    valueRange = 0.1f..1.0f,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Box Height: ${(boxHeight * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(110.dp))
                Slider(
                    value = boxHeight,
                    onValueChange = {
                        boxHeight = it
                        pushUpdate()
                    },
                    valueRange = 0.1f..1.0f,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Preview
            Text("Preview", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            SlidePreview(
                slide = slide.copy(
                    content = content,
                    type = selectedType,
                    backgroundColor = bgColor,
                    textColor = textColor,
                    fontSize = fontSize,
                    fontFamily = fontFamily,
                    alignment = alignment,
                    positionX = positionX,
                    positionY = positionY,
                    boxWidth = boxWidth,
                    boxHeight = boxHeight
                )
            )
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

    val textColor = try {
        Color(slide.textColor.replace("#", "").toLong(16) or 0xFF000000)
    } catch (e: Exception) {
        Color.White
    }

    val parsedFontFamily = when (slide.fontFamily) {
        "Serif" -> FontFamily.Serif
        "Monospace" -> FontFamily.Monospace
        else -> FontFamily.SansSerif
    }

    val parsedAlignment = when (slide.alignment) {
        "Left" -> androidx.compose.ui.text.style.TextAlign.Left
        "Right" -> androidx.compose.ui.text.style.TextAlign.Right
        else -> androidx.compose.ui.text.style.TextAlign.Center
    }

    val parsedHorizontalAlignment = when (slide.alignment) {
        "Left" -> Alignment.Start
        "Right" -> Alignment.End
        else -> Alignment.CenterHorizontally
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (slide.type == SlideType.BLANK) {
            // Empty
        } else {
            // Absolute positioning text box using BiasAlignment based on custom X and Y coordinates
            Box(
                modifier = Modifier
                    .fillMaxWidth(slide.boxWidth)
                    .fillMaxHeight(slide.boxHeight)
                    .align(
                        androidx.compose.ui.BiasAlignment(
                            horizontalBias = (slide.positionX * 2f) - 1f,
                            verticalBias = (slide.positionY * 2f) - 1f
                        )
                    )
            ) {
                Column(
                    horizontalAlignment = parsedHorizontalAlignment,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Normalize preview font size to fit within the smaller preview window (scale by 0.35)
                    val previewFontSize = (slide.fontSize * 0.35f).sp
                    slide.content.lines().forEach { line ->
                        Text(
                            text = line,
                            fontSize = previewFontSize,
                            fontFamily = parsedFontFamily,
                            color = textColor,
                            textAlign = parsedAlignment,
                            lineHeight = (previewFontSize.value * 1.25f).sp,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
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

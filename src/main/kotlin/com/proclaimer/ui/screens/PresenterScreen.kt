package com.proclaimer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proclaimer.model.Slide
import com.proclaimer.ui.components.OutputDisplay
import com.proclaimer.model.toLibraryItem
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresenterScreen(
    slides: List<Slide>,
    initialIndex: Int = 0,
    onClose: () -> Unit,
    onOpenStageDisplay: (List<Slide>, Int) -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val currentSlide = slides.getOrNull(currentIndex)
    var numberInput by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionRight, Key.PageDown -> {
                            if (currentIndex < slides.size - 1) currentIndex++
                            true
                        }
                        Key.DirectionLeft, Key.PageUp -> {
                            if (currentIndex > 0) currentIndex--
                            true
                        }
                        Key.NumPad0, Key.Zero -> { numberInput += "0"; true }
                        Key.NumPad1, Key.One -> { numberInput += "1"; true }
                        Key.NumPad2, Key.Two -> { numberInput += "2"; true }
                        Key.NumPad3, Key.Three -> { numberInput += "3"; true }
                        Key.NumPad4, Key.Four -> { numberInput += "4"; true }
                        Key.NumPad5, Key.Five -> { numberInput += "5"; true }
                        Key.NumPad6, Key.Six -> { numberInput += "6"; true }
                        Key.NumPad7, Key.Seven -> { numberInput += "7"; true }
                        Key.NumPad8, Key.Eight -> { numberInput += "8"; true }
                        Key.NumPad9, Key.Nine -> { numberInput += "9"; true }
                        Key.Enter, Key.NumPadEnter -> {
                            val targetIndex = numberInput.toIntOrNull()?.minus(1)
                            if (targetIndex != null && targetIndex in slides.indices) {
                                currentIndex = targetIndex
                            }
                            numberInput = ""
                            true
                        }
                        else -> false
                    }
                } else false
            },
        color = Color(0xFF0A0A14)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left: Audience display (large)
            OutputDisplay(
                item = currentSlide?.toLibraryItem(),
                modifier = Modifier.weight(1.2f)
            )

            // Right: Presenter panel
            Surface(
                modifier = Modifier
                    .width(380.dp)
                    .fillMaxHeight(),
                color = Color(0xFF12121E)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Presenter View ${if (numberInput.isNotEmpty()) "[$numberInput]" else ""}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        IconButton(onClick = onClose) {
                            Icon(
                                Icons.Default.Close,
                                "Close",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Current slide preview (mini)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1A1A2E)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (currentSlide != null) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    currentSlide.content.lines().take(4).forEach { line ->
                                        Text(
                                            line,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color.White,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                    if (currentSlide.content.lines().size > 4) {
                                        Text(
                                            "...",
                                            color = Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Navigation controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { if (currentIndex > 0) currentIndex-- },
                            enabled = currentIndex > 0,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.SkipPrevious, "Previous", modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Prev", color = Color.White)
                        }

                        Spacer(Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                "${currentIndex + 1} / ${slides.size}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Button(
                            onClick = { if (currentIndex < slides.size - 1) currentIndex++ },
                            enabled = currentIndex < slides.size - 1,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Next", color = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.SkipNext, "Next", modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { onOpenStageDisplay(slides, currentIndex) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Open Stage Display Window")
                    }

                    Spacer(Modifier.height(16.dp))

                    // Quick nav — slide thumbnails
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Slides",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "[←/→] [Num+Enter]",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Scrollable thumbnail list
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(slides) { index, slide ->
                            val isCurrentSlide = index == currentIndex
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { currentIndex = index },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCurrentSlide) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        else Color(0xFF1A1A2E).copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isCurrentSlide) MaterialTheme.colorScheme.primary
                                                else Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.width(20.dp)
                                    )
                                    Text(
                                        slide.content.lines().firstOrNull()?.take(25) ?: "(blank)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f),
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Notes display
                    if (currentSlide?.note?.isNotBlank() == true) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    currentSlide.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.proclaimer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.proclaimer.model.Slide
import com.proclaimer.ui.components.OutputDisplay

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

    // Keyboard shortcuts (handled via buttons for desktop)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0A0A14)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left: Audience display (large)
            OutputDisplay(
                slide = currentSlide,
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
                            "Presenter View",
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
                        // Previous
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

                        // Counter
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

                        // Next
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

                    // Stage Display button
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
                    Text(
                        "Slides",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(Modifier.height(8.dp))

                    // Scrollable thumbnail list
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        slides.forEachIndexed { index, slide ->
                            val isCurrentSlide = index == currentIndex
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
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

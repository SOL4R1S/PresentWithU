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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.proclaimer.model.Slide
import com.proclaimer.ui.components.OutputDisplay

@Composable
fun StageDisplayScreen(
    slides: List<Slide>,
    currentIndex: Int,
    onClose: () -> Unit
) {
    val currentSlide = slides.getOrNull(currentIndex)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0D0D1A)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main presentation area
            OutputDisplay(
                slide = currentSlide,
                isStageDisplay = true
            )

            // Top bar with info
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Close button
                    TextButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Close", color = Color.White.copy(alpha = 0.7f))
                    }

                    // Slide counter
                    Text(
                        "${currentIndex + 1} / ${slides.size}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    // Next/Prev indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            slideLabel(currentSlide),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Bottom: upcoming slide preview
            val nextSlide = slides.getOrNull(currentIndex + 1)
            if (nextSlide != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .widthIn(max = 300.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Next: ${nextSlide.content.lines().firstOrNull()?.take(30) ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

private fun slideLabel(slide: Slide?): String {
    if (slide == null) return ""
    val preview = slide.content.lines().firstOrNull()?.take(20) ?: return slide.type.name
    return if (slide.note.isNotBlank()) "${slide.note}: $preview" else preview
}

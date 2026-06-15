package com.proclaimer.ui.components

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
import androidx.compose.ui.unit.sp
import com.proclaimer.model.Slide
import com.proclaimer.model.SlideType

@Composable
fun PresentationDisplay(
    slide: Slide?,
    modifier: Modifier = Modifier,
    isStageDisplay: Boolean = false
) {
    val bgColor = try {
        Color(android.graphics.Color.parseColor(slide?.backgroundColor ?: "#0D0D1A"))
    } catch (e: Exception) {
        Color(0xFF0D0D1A)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (slide == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Tv,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.White.copy(alpha = 0.2f)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "No Slide Selected",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White.copy(alpha = 0.3f)
                )
            }
            return@Box
        }

        when (slide.type) {
            SlideType.BLANK -> {
                // Pure black — for transitions/silence
            }

            SlideType.TITLE -> {
                Text(
                    slide.content,
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(48.dp)
                )
            }

            SlideType.IMAGE -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.White.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        slide.content.ifBlank { "Image Placeholder" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            else -> {
                // Lyrics, Scripture, Announcement
                val lines = slide.content.lines().filter { it.isNotBlank() }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 48.dp)
                ) {
                    lines.forEach { line ->
                        Text(
                            line,
                            style = if (isStageDisplay) MaterialTheme.typography.headlineMedium
                                    else MaterialTheme.typography.displaySmall,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontWeight = if (line.startsWith("(") || line.startsWith("[")) FontWeight.Light
                                        else FontWeight.Normal,
                            modifier = Modifier.padding(vertical = if (isStageDisplay) 4.dp else 8.dp),
                            lineHeight = if (isStageDisplay) 40.sp else 56.sp
                        )
                    }
                }
            }
        }

        // Bottom-right slide indicator
        if (slide.note.isNotBlank()) {
            Surface(
                modifier = Modifier
                    .align(if (isStageDisplay) Alignment.TopStart else Alignment.BottomEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    if (isStageDisplay) "→ ${slide.note}" else slide.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // Top-left slide type indicator (stage display only)
        if (isStageDisplay) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                Text(
                    slide.type.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

package com.presentwithu.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.presentwithu.model.LibraryItem
import com.presentwithu.model.LibraryItemType
import java.io.File
import javax.imageio.ImageIO
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.presentwithu.model.Slide
import com.presentwithu.model.SlideType
import com.presentwithu.model.toLibraryItem
import androidx.compose.foundation.Image

/**
 * Positioning and sizing for text or media within the output canvas.
 */
data class OutputPosition(
    val x: Float = 0.05f,      // normalized 0..1
    val y: Float = 0.05f,
    val width: Float = 0.9f,
    val height: Float = 0.9f
)

/**
 * Crop rectangle for media, normalized to the source media dimensions.
 */
data class OutputCrop(
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 1f,
    val height: Float = 1f
)

/**
 * Text styling applied by OutputDisplay.
 */
data class OutputTextStyle(
    val fontSize: TextUnit = 56.sp,
    val fontFamily: FontFamily = FontFamily.Default,
    val fontWeight: FontWeight = FontWeight.Normal,
    val lineHeight: TextUnit = 64.sp,
    val color: Color = Color.White,
    val alignment: TextAlign = TextAlign.Center
)

/**
 * Unified output renderer for audience and stage displays.
 *
 * @param item The library item to render.
 * @param isStageDisplay When true, renders a high-contrast text-only stage layout.
 * @param fadeDurationMs Fade transition duration in milliseconds. 0 disables fade.
 * @param textStyle Custom text styling for audience output.
 * @param textPosition Normalized position for text content.
 * @param mediaCrop Normalized crop rectangle for background media.
 * @param modifier Layout modifier.
 * @param showNotes Whether to show notes in the stage display strip.
 * @param nextItem The next library item for stage display preview.
 */
@Composable
fun OutputDisplay(
    item: LibraryItem?,
    modifier: Modifier = Modifier,
    isStageDisplay: Boolean = false,
    fadeDurationMs: Int = 0,
    textStyle: OutputTextStyle? = null,
    textPosition: OutputPosition? = null,
    mediaCrop: OutputCrop = OutputCrop(),
    showNotes: Boolean = false,
    nextItem: LibraryItem? = null
) {
    val bgColor = parseColor(item?.backgroundColor) ?: Color.Black
    var backgroundImage by remember(item?.mediaPath) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(item?.mediaPath) {
        val path = item?.mediaPath
        if (!path.isNullOrBlank()) {
            backgroundImage = com.presentwithu.media.MediaLoader.loadImage(path)
        } else {
            backgroundImage = null
        }
    }

    val resolvedTextStyle = textStyle ?: OutputTextStyle(
        fontSize = (item?.fontSize ?: 48).sp,
        fontFamily = when (item?.fontFamily) {
            "Serif" -> FontFamily.Serif
            "Monospace" -> FontFamily.Monospace
            else -> FontFamily.SansSerif
        },
        alignment = when (item?.alignment) {
            "Left" -> TextAlign.Left
            "Right" -> TextAlign.Right
            else -> TextAlign.Center
        },
        lineHeight = ((item?.fontSize ?: 48) * 1.25).sp,
        color = parseColor(item?.textColor) ?: Color.White
    )

    val resolvedTextPosition = textPosition ?: OutputPosition(
        x = item?.positionX ?: 0.05f,
        y = item?.positionY ?: 0.05f,
        width = item?.boxWidth ?: 0.9f,
        height = item?.boxHeight ?: 0.9f
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (item == null) {
            EmptyOutputMessage(isStageDisplay)
            return@Box
        }

        // Background layer
        backgroundImage?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        val transitionSpec = if (fadeDurationMs > 0) {
            fadeIn(animationSpec = tween(fadeDurationMs)) togetherWith
                fadeOut(animationSpec = tween(fadeDurationMs))
        } else {
            fadeIn() togetherWith fadeOut()
        }

        AnimatedContent(
            targetState = item to showNotes,
            transitionSpec = { transitionSpec },
            label = "output_display_transition"
        ) { (currentItem, notesVisible) ->
            if (isStageDisplay) {
                StageDisplayLayout(
                    item = currentItem,
                    nextItem = nextItem,
                    showNotes = notesVisible
                )
            } else {
                AudienceDisplayLayout(
                    item = currentItem,
                    textStyle = resolvedTextStyle,
                    textPosition = resolvedTextPosition,
                    mediaCrop = mediaCrop
                )
            }
        }
    }
}

@Composable
private fun AudienceDisplayLayout(
    item: LibraryItem,
    textStyle: OutputTextStyle,
    textPosition: OutputPosition,
    mediaCrop: OutputCrop
) {
    var contentImage by remember(item.mediaPath) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(item.mediaPath) {
        if (item.type == LibraryItemType.IMAGE && item.mediaPath.isNotBlank()) {
            contentImage = com.presentwithu.media.MediaLoader.loadImage(item.mediaPath)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (item.type) {
            LibraryItemType.BLANK -> { /* intentionally empty */ }

            LibraryItemType.IMAGE -> {
                contentImage?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            LibraryItemType.VIDEO -> {
                com.presentwithu.media.VideoPlayer(
                    path = item.mediaPath,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                val lines = item.content.lines().filter { it.isNotBlank() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(textPosition.width)
                        .fillMaxHeight(textPosition.height)
                        .align(
                            androidx.compose.ui.BiasAlignment(
                                horizontalBias = (textPosition.x * 2f) - 1f,
                                verticalBias = (textPosition.y * 2f) - 1f
                            )
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        lines.forEach { line ->
                            Text(
                                text = line,
                                fontSize = textStyle.fontSize,
                                fontFamily = textStyle.fontFamily,
                                fontWeight = textStyle.fontWeight,
                                color = textStyle.color,
                                textAlign = textStyle.alignment,
                                lineHeight = textStyle.lineHeight,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StageDisplayLayout(
    item: LibraryItem,
    nextItem: LibraryItem?,
    showNotes: Boolean
) {
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Current slide takes remaining space (80-100%)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val lines = item.content.lines().filter { it.isNotBlank() }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                lines.forEach { line ->
                    Text(
                        text = line,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 84.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        // Next / Notes strip (20-0%)
        if (nextItem != null || item.note.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = 140.dp)
                    .background(Color(0xFF111111))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (showNotes && item.note.isNotBlank()) {
                    Text(
                        text = "Note: ${item.note}",
                        fontSize = 24.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Start
                    )
                } else if (nextItem != null) {
                    val preview = nextItem.content.lines().firstOrNull()?.take(60) ?: ""
                    Text(
                        text = "Next: $preview",
                        fontSize = 24.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyOutputMessage(isStageDisplay: Boolean) {
    Text(
        text = if (isStageDisplay) "" else "No Slide Selected",
        fontSize = if (isStageDisplay) 32.sp else 48.sp,
        color = Color.White.copy(alpha = 0.3f),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun VideoPlaceholder(path: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Video: ${File(path).name}",
            fontSize = 32.sp,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

private sealed class OutputMedia {
    data class Image(val bitmap: ImageBitmap) : OutputMedia()
}

private fun resolveMedia(path: String): OutputMedia? {
    return try {
        val file = File(path)
        if (!file.exists()) return null
        val ext = file.extension.lowercase()
        if (ext in setOf("png", "jpg", "jpeg", "gif", "bmp")) {
            val buffered = ImageIO.read(file) ?: return null
            OutputMedia.Image(buffered.toComposeImageBitmap())
        } else {
            null
        }
    } catch (_: Exception) {
        null
    }
}

private fun parseColor(hex: String?): Color? {
    if (hex.isNullOrBlank()) return null
    return try {
        val normalized = hex.replace("#", "")
        Color(normalized.toLong(16) or 0xFF000000)
    } catch (_: Exception) {
        null
    }
}

/**
 * Temporary adapter overload that renders legacy [Slide] instances through the new
 * [LibraryItem]-based renderer. This will be removed once callers are migrated.
 */
@Composable
fun OutputDisplay(
    slide: Slide?,
    modifier: Modifier = Modifier,
    isStageDisplay: Boolean = false,
    showNextPreview: Boolean = false,
    nextSlide: Slide? = null
) {
    OutputDisplay(
        item = slide?.toLibraryItem(),
        modifier = modifier,
        isStageDisplay = isStageDisplay,
        showNotes = showNextPreview,
        nextItem = nextSlide?.toLibraryItem()
    )
}


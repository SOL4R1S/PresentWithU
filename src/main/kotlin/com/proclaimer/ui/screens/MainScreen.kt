package com.proclaimer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.proclaimer.model.*
import com.proclaimer.ui.components.*
import com.proclaimer.ui.state.MainStateHolder
import com.proclaimer.ui.state.WindowStateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    stateHolder: MainStateHolder,
    windowStateHolder: WindowStateHolder,
    onStartPresentation: () -> Unit,
    onOpenStageDisplay: () -> Unit
) {
    val slides by stateHolder.slides.collectAsState()
    val currentSlideIndex by stateHolder.currentSlideIndex.collectAsState()
    val songs by stateHolder.songs.collectAsState()
    val presentations by stateHolder.presentations.collectAsState()
    val currentPresentation by stateHolder.currentPresentation.collectAsState()
    val bibleVerses by stateHolder.bibleVerses.collectAsState()
    val libraryItems by stateHolder.libraryItems.collectAsState()
    val playlists by stateHolder.playlists.collectAsState()
    val selectedTab by stateHolder.selectedTab.collectAsState()
    val showSongDialog by stateHolder.showSongDialog.collectAsState()
    val editingSong by stateHolder.editingSong.collectAsState()
    val showBibleDialog by stateHolder.showBibleDialog.collectAsState()
    val showPresentationDialog by stateHolder.showPresentationDialog.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (selectedTab == 4) {
                SettingsScreen(
                    windowStateHolder = windowStateHolder,
                    modifier = Modifier.weight(1f)
                )
            } else {
                // Left panel adapts to selected tab
                when (selectedTab) {
                    0 -> SlideListPanel(
                        slides = slides,
                        currentIndex = currentSlideIndex,
                        onSelect = { stateHolder.selectSlide(it) },
                        onAdd = { stateHolder.addSlide() },
                        onDelete = { stateHolder.deleteSlide(it) },
                        onMoveUp = { stateHolder.moveSlideUp(it) },
                        onMoveDown = { stateHolder.moveSlideDown(it) },
                        modifier = Modifier.fillMaxWidth(0.22f).widthIn(min = 180.dp, max = 320.dp)
                    )
                    1 -> SongLibraryPanel(
                        songs = songs,
                        onSelectSong = { stateHolder.addSongToSlides(it) },
                        onDeleteSong = { stateHolder.deleteSong(it) },
                        onNewSong = { stateHolder.showSongEditor() },
                        modifier = Modifier.fillMaxWidth(0.22f).widthIn(min = 180.dp, max = 320.dp)
                    )
                    2 -> LibraryPanel(
                        items = libraryItems,
                        onAddToSlides = { stateHolder.addLibraryItemToSlides(it) },
                        onDelete = { stateHolder.deleteLibraryItem(it) },
                        onSave = { stateHolder.saveLibraryItem(it) },
                        modifier = Modifier.fillMaxWidth(0.22f).widthIn(min = 180.dp, max = 320.dp)
                    )
                    3 -> PlaylistPanel(
                        playlists = playlists,
                        onApply = { stateHolder.applyPlaylist(it) },
                        onDelete = { stateHolder.deletePlaylist(it) },
                        modifier = Modifier.fillMaxWidth(0.22f).widthIn(min = 180.dp, max = 320.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Center — Slide Editor
                SlideEditor(
                    slide = slides.getOrNull(currentSlideIndex),
                    onUpdate = { updated -> stateHolder.updateSlide(updated, currentSlideIndex) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.width(12.dp))

            // Right — Control Panel
            Surface(
                modifier = Modifier.fillMaxWidth(0.2f).widthIn(min = 180.dp, max = 280.dp).fillMaxHeight(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Controls",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Tab selector
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { stateHolder.selectTab(0) },
                            text = { Text("Slides", style = MaterialTheme.typography.labelSmall) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { stateHolder.selectTab(1) },
                            text = { Text("Songs", style = MaterialTheme.typography.labelSmall) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { stateHolder.selectTab(2) },
                            text = { Text("Library", style = MaterialTheme.typography.labelSmall) }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { stateHolder.selectTab(3) },
                            text = { Text("Lists", style = MaterialTheme.typography.labelSmall) }
                        )
                        Tab(
                            selected = selectedTab == 4,
                            onClick = { stateHolder.selectTab(4) },
                            text = { Text("Settings", style = MaterialTheme.typography.labelSmall) }
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    // Section: Presentation
                    Button(
                        onClick = onStartPresentation,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Start Presentation")
                    }

                    Button(
                        onClick = onOpenStageDisplay,
                        enabled = slides.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Stage Display (Single)")
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Text("Multi-Window Output", style = MaterialTheme.typography.titleSmall)

                    // Control Window Toggle
                    OutlinedButton(
                        onClick = { windowStateHolder.isControlOpen = !windowStateHolder.isControlOpen },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (windowStateHolder.isControlOpen) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = if (windowStateHolder.isControlOpen) Icons.Default.PlayArrow else Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(if (windowStateHolder.isControlOpen) "Close Controller" else "Open Controller")
                    }

                    // Audience Display Toggle
                    OutlinedButton(
                        onClick = { windowStateHolder.isAudienceOpen = !windowStateHolder.isAudienceOpen },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (windowStateHolder.isAudienceOpen) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = if (windowStateHolder.isAudienceOpen) Icons.Default.PlayArrow else Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(if (windowStateHolder.isAudienceOpen) "Close Audience" else "Open Audience")
                    }

                    // Stage Display Toggle
                    OutlinedButton(
                        onClick = { windowStateHolder.isStageOpen = !windowStateHolder.isStageOpen },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (windowStateHolder.isStageOpen) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = if (windowStateHolder.isStageOpen) Icons.Default.PlayArrow else Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(if (windowStateHolder.isStageOpen) "Close Stage Window" else "Open Stage Window")
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Section: Tools
                    Text("Tools", style = MaterialTheme.typography.titleSmall)

                    OutlinedButton(
                        onClick = { stateHolder.showBibleDialog() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Bible Lookup")
                    }

                    OutlinedButton(
                        onClick = { stateHolder.showPresentationDialog() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Save/Load")
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Section: Quick slide tools
                    Text("Quick Actions", style = MaterialTheme.typography.titleSmall)

                    TextButton(
                        onClick = { stateHolder.addBlankSlide() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Brightness1, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add Black Slide")
                    }

                    TextButton(
                        onClick = { stateHolder.addAnnouncementSlide() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add Announcement")
                    }

                    if (slides.isNotEmpty()) {
                        Spacer(Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ) {
                            Text(
                                "Slide ${currentSlideIndex + 1} of ${slides.size}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSongDialog) {
        SongEditorDialog(
            song = editingSong,
            onDismiss = { stateHolder.dismissSongDialog() },
            onSave = { song -> stateHolder.saveSong(song) }
        )
    }

    if (showBibleDialog) {
        BibleLibraryDialog(
            savedVerses = bibleVerses,
            onDismiss = { stateHolder.dismissBibleDialog() },
            onAddToSlides = { verse ->
                stateHolder.addBibleVerseToSlides(verse)
                stateHolder.dismissBibleDialog()
            },
            onSave = { verse -> stateHolder.saveBibleVerse(verse) },
            onDelete = { id -> stateHolder.deleteBibleVerse(id) }
        )
    }

    if (showPresentationDialog) {
        SaveLoadDialog(
            currentSlides = slides,
            currentName = currentPresentation?.name ?: "Untitled",
            presentations = presentations,
            onDismiss = { stateHolder.dismissPresentationDialog() },
            onSave = { name -> stateHolder.savePresentation(name) },
            onLoad = { pres -> stateHolder.loadPresentation(pres) },
            onDelete = { id -> stateHolder.deletePresentation(id) }
        )
    }
}

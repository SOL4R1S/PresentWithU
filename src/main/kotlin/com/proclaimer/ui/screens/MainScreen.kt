package com.proclaimer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.proclaimer.data.SongRepository
import com.proclaimer.model.*
import com.proclaimer.ui.components.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    repository: SongRepository,
    onStartPresentation: (List<Slide>) -> Unit,
    onOpenStageDisplay: (List<Slide>, Int) -> Unit
) {
    // State
    var slides by remember { mutableStateOf(listOf<Slide>()) }
    var currentSlideIndex by remember { mutableStateOf(0) }
    var songs by remember { mutableStateOf(repository.getAllSongs()) }
    var presentations by remember { mutableStateOf(repository.getAllPresentations()) }
    var currentPresentation by remember { mutableStateOf<Presentation?>(null) }
    var showSongDialog by remember { mutableStateOf(false) }
    var editingSong by remember { mutableStateOf<Song?>(null) }
    var showBibleDialog by remember { mutableStateOf(false) }
    var bibleVerses by remember { mutableStateOf(repository.getAllBibleVerses()) }
    var selectedTab by remember { mutableStateOf(0) }
    var showPresentationDialog by remember { mutableStateOf(false) }

    // Refresh data
    fun refreshSongs() {
        songs = repository.getAllSongs()
        presentations = repository.getAllPresentations()
        bibleVerses = repository.getAllBibleVerses()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            // Left panel — Slides or Song Library
            when (selectedTab) {
                0 -> {
                    // Slides panel
                    SlideListPanel(
                        slides = slides,
                        currentIndex = currentSlideIndex,
                        onSelect = { currentSlideIndex = it },
                        onAdd = {
                            slides = slides + Slide(
                                type = SlideType.LYRIC,
                                content = "",
                                order = slides.size
                            )
                            currentSlideIndex = slides.size - 1
                        },
                        onDelete = { index ->
                            slides = slides.toMutableList().apply { removeAt(index) }
                            if (currentSlideIndex >= slides.size) currentSlideIndex = slides.size - 1
                        },
                        onMoveUp = { index ->
                            slides = slides.toMutableList().apply {
                                val item = removeAt(index)
                                add(index - 1, item)
                            }
                            currentSlideIndex = index - 1
                        },
                        onMoveDown = { index ->
                            slides = slides.toMutableList().apply {
                                val item = removeAt(index)
                                add(index + 1, item)
                            }
                            currentSlideIndex = index + 1
                        },
                        modifier = Modifier.width(260.dp)
                    )
                }
                1 -> {
                    // Song Library panel
                    SongLibraryPanel(
                        songs = songs,
                        onSelectSong = { song ->
                            // Convert song verses to slides
                            val songSlides = song.verses.flatMap { verse ->
                                if (verse.lines.isEmpty()) {
                                    listOf(Slide(type = SlideType.LYRIC, content = "", note = verse.label))
                                } else {
                                    listOf(
                                        Slide(
                                            type = SlideType.LYRIC,
                                            content = verse.lines.joinToString("\n"),
                                            note = verse.label
                                        )
                                    )
                                }
                            }
                            slides = slides + songSlides
                            currentSlideIndex = slides.size - songSlides.size
                        },
                        onDeleteSong = { id ->
                            repository.deleteSong(id)
                            refreshSongs()
                        },
                        onNewSong = {
                            editingSong = null
                            showSongDialog = true
                        },
                        modifier = Modifier.width(260.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Center — Slide Editor
            SlideEditor(
                slide = slides.getOrNull(currentSlideIndex),
                onUpdate = { updated ->
                    slides = slides.toMutableList().apply {
                        set(currentSlideIndex, updated)
                    }
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            // Right — Control Panel
            Surface(
                modifier = Modifier.width(240.dp).fillMaxHeight(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            onClick = { selectedTab = 0 },
                            text = { Text("Slides", style = MaterialTheme.typography.labelMedium) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Songs", style = MaterialTheme.typography.labelMedium) }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Section: Presentation
                    Button(
                        onClick = { onStartPresentation(slides) },
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
                        onClick = { if (slides.isNotEmpty()) onOpenStageDisplay(slides, currentSlideIndex) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Stage Display")
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Section: Tools
                    Text("Tools", style = MaterialTheme.typography.titleSmall)

                    OutlinedButton(
                        onClick = { showBibleDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Bible Lookup")
                    }

                    OutlinedButton(
                        onClick = {
                            showPresentationDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Save/Load")
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Section: Quick slide tools
                    Text("Quick Actions", style = MaterialTheme.typography.titleSmall)

                    TextButton(
                        onClick = {
                            slides = slides + Slide(type = SlideType.BLANK, content = "", order = slides.size)
                            currentSlideIndex = slides.size - 1
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Brightness1, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add Black Slide")
                    }

                    TextButton(
                        onClick = {
                            slides = slides + Slide(
                                type = SlideType.ANNOUNCEMENT,
                                content = "",
                                order = slides.size
                            )
                            currentSlideIndex = slides.size - 1
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add Announcement")
                    }

                    // Current slide counter
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

    // Song Dialog
    if (showSongDialog) {
        SongEditorDialog(
            song = editingSong,
            onDismiss = { showSongDialog = false },
            onSave = { song ->
                repository.saveSong(song)
                refreshSongs()
            }
        )
    }

    // Bible Library (user-created verses)
    if (showBibleDialog) {
        BibleLibraryDialog(
            savedVerses = bibleVerses,
            onDismiss = { showBibleDialog = false },
            onAddToSlides = { verse ->
                if (verse.text.isNotBlank()) {
                    slides = slides + Slide(
                        type = SlideType.SCRIPTURE,
                        content = verse.text,
                        note = verse.label,
                        order = slides.size
                    )
                    currentSlideIndex = slides.size - 1
                }
                showBibleDialog = false
            },
            onSave = { verse ->
                repository.saveBibleVerse(verse)
                refreshSongs()
            },
            onDelete = { id ->
                repository.deleteBibleVerse(id)
                refreshSongs()
            }
        )
    }

    // Save/Load Dialog
    if (showPresentationDialog) {
        SaveLoadDialog(
            currentSlides = slides,
            currentName = currentPresentation?.name ?: "Untitled",
            presentations = presentations,
            onDismiss = { showPresentationDialog = false },
            onSave = { name ->
                val pres = currentPresentation?.copy(
                    name = name,
                    slides = slides,
                    updatedAt = System.currentTimeMillis()
                ) ?: Presentation(
                    name = name,
                    slides = slides
                )
                repository.savePresentation(pres)
                currentPresentation = pres
                refreshSongs() // also refreshes presentations
                showPresentationDialog = false
            },
            onLoad = { pres ->
                currentPresentation = pres
                slides = pres.slides
                currentSlideIndex = 0
                showPresentationDialog = false
            },
            onDelete = { id ->
                repository.deletePresentation(id)
                refreshSongs()
            }
        )
    }
}

@Composable
fun BibleLibraryDialog(
    savedVerses: List<BibleVerse>,
    onDismiss: () -> Unit,
    onAddToSlides: (BibleVerse) -> Unit,
    onSave: (BibleVerse) -> Unit,
    onDelete: (String) -> Unit
) {
    var tab by remember { mutableStateOf(0) } // 0 = Saved, 1 = Add New

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Bible Verse Library") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 500.dp)) {
                // Tab row
                TabRow(
                    selectedTabIndex = tab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(selected = tab == 0, onClick = { tab = 0 },
                        text = { Text("Saved Verses (${savedVerses.size})", style = MaterialTheme.typography.labelMedium) })
                    Tab(selected = tab == 1, onClick = { tab = 1 },
                        text = { Text("Add New", style = MaterialTheme.typography.labelMedium) })
                }

                Spacer(Modifier.height(12.dp))

                when (tab) {
                    0 -> SavedVersesTab(
                        verses = savedVerses,
                        onAddToSlides = onAddToSlides,
                        onDelete = onDelete
                    )
                    1 -> AddBibleVerseTab(onSave = onSave)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun SavedVersesTab(
    verses: List<BibleVerse>,
    onAddToSlides: (BibleVerse) -> Unit,
    onDelete: (String) -> Unit
) {
    if (verses.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "No saved verses yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Switch to 'Add New' tab to type your own verses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
        return
    }

    var searchQuery by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search verses...") },
        leadingIcon = { Icon(Icons.Default.Search, "Search", modifier = Modifier.size(16.dp)) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )

    Spacer(Modifier.height(8.dp))

    val filtered = if (searchQuery.isBlank()) verses
    else verses.filter {
        it.label.contains(searchQuery, ignoreCase = true) ||
        it.text.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(filtered) { verse ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MenuBook,
                                null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                verse.label,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row {
                            if (verse.category.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        verse.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                                Spacer(Modifier.width(4.dp))
                            }
                            IconButton(onClick = { onDelete(verse.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        verse.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (verse.translation.isNotBlank()) {
                        Text(
                            verse.translation,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Button(
                        onClick = { onAddToSlides(verse) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, "Add to presentation", modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add to Presentation", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddBibleVerseTab(
    onSave: (BibleVerse) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Type a Bible verse yourself to add to your personal library.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Label (e.g. "John 3:16")
        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            label = { Text("Reference (e.g. John 3:16)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        // Translation
        OutlinedTextField(
            value = translation,
            onValueChange = { translation = it },
            label = { Text("Translation (e.g. NIV, ESV, KJV)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        // Category
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category (e.g. Gospel, Worship, Comfort)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        // Verse text
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Verse Text") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Preview
        if (label.isNotBlank() && text.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (translation.isNotBlank()) {
                        Text(
                            translation,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // Save button
        Button(
            onClick = {
                val verse = BibleVerse(
                    label = label,
                    translation = translation,
                    text = text,
                    category = category
                )
                onSave(verse)
                // Reset form
                label = ""
                text = ""
                translation = ""
                category = ""
            },
            enabled = label.isNotBlank() && text.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Save, "Save verse", modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Save to Library")
        }
    }
}

@Composable
fun SaveLoadDialog(
    currentSlides: List<Slide>,
    currentName: String,
    presentations: List<Presentation>,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onLoad: (Presentation) -> Unit,
    onDelete: (String) -> Unit
) {
    var saveName by remember { mutableStateOf(currentName) }
    var isSaving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(if (isSaving) "Save Presentation" else "Load Presentation") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Toggle save/load
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isSaving,
                        onClick = { isSaving = false },
                        label = { Text("Load") }
                    )
                    FilterChip(
                        selected = isSaving,
                        onClick = { isSaving = true },
                        label = { Text("Save") }
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (isSaving) {
                    OutlinedTextField(
                        value = saveName,
                        onValueChange = { saveName = it },
                        label = { Text("Presentation Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${currentSlides.size} slides will be saved",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    if (presentations.isEmpty()) {
                        Text(
                            "No saved presentations yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 300.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(presentations) { pres ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Description, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(pres.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                            Text("${pres.slides.size} slides", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        IconButton(onClick = { onDelete(pres.id) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                        }
                                        IconButton(onClick = { onLoad(pres) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.OpenInNew, "Load", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isSaving) {
                Button(
                    onClick = { onSave(saveName) },
                    enabled = saveName.isNotBlank(),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Save") }
            } else {
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Close") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

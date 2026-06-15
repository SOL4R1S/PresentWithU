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
    var bibleReference by remember { mutableStateOf(BibleReference()) }
    var selectedTab by remember { mutableStateOf(0) }
    var showPresentationDialog by remember { mutableStateOf(false) }

    // Refresh data
    fun refreshSongs() {
        songs = repository.getAllSongs()
        presentations = repository.getAllPresentations()
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

    // Bible Dialog
    if (showBibleDialog) {
        BibleDialog(
            onDismiss = { showBibleDialog = false },
            onSelect = { ref ->
                bibleReference = ref
                if (ref.text.isNotBlank()) {
                    slides = slides + Slide(
                        type = SlideType.SCRIPTURE,
                        content = ref.text,
                        note = "${ref.book} ${ref.chapter}:${ref.verseStart}${if (ref.verseEnd != null && ref.verseEnd != ref.verseStart) "-${ref.verseEnd}" else ""} (${ref.translation})",
                        order = slides.size
                    )
                    currentSlideIndex = slides.size - 1
                }
                showBibleDialog = false
            },
            repository = repository
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
fun BibleDialog(
    onDismiss: () -> Unit,
    onSelect: (BibleReference) -> Unit,
    repository: SongRepository
) {
    var book by remember { mutableStateOf("John") }
    var chapter by remember { mutableStateOf("3") }
    var verseStart by remember { mutableStateOf("16") }
    var verseEnd by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("NIV") }
    var result by remember { mutableStateOf("") }

    val books = listOf("John", "Psalm", "Genesis", "Matthew", "Romans", "Philippians", "Ephesians", "Isaiah", "Jeremiah", "Proverbs", "Hebrews")

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Bible Lookup") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Book selector
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = book,
                        onValueChange = { book = it },
                        label = { Text("Book") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        books.forEach { b ->
                            DropdownMenuItem(
                                text = { Text(b) },
                                onClick = { book = b; expanded = false }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = chapter,
                        onValueChange = { chapter = it },
                        label = { Text("Chapter") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = verseStart,
                        onValueChange = { verseStart = it },
                        label = { Text("Verse") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = verseEnd,
                        onValueChange = { verseEnd = it },
                        label = { Text("To") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Lookup button
                Button(
                    onClick = {
                        val ref = BibleReference(
                            book = book,
                            chapter = chapter.toIntOrNull() ?: 1,
                            verseStart = verseStart.toIntOrNull() ?: 1,
                            verseEnd = verseEnd.toIntOrNull(),
                            translation = translation
                        )
                        val lookupResult = repository.getBibleVerse(ref)
                        result = lookupResult.text
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Search, "Lookup", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Lookup")
                }

                Spacer(Modifier.height(8.dp))

                // Result
                if (result.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "$book $chapter:${verseStart}${if (verseEnd.isNotBlank()) "-$verseEnd" else ""} ($translation)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                result,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ref = BibleReference(
                        book = book,
                        chapter = chapter.toIntOrNull() ?: 1,
                        verseStart = verseStart.toIntOrNull() ?: 1,
                        verseEnd = verseEnd.toIntOrNull(),
                        translation = translation,
                        text = result
                    )
                    onSelect(ref)
                },
                enabled = result.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add to Presentation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
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

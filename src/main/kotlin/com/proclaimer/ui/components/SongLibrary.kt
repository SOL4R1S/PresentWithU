package com.proclaimer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.proclaimer.model.Song
import com.proclaimer.model.Verse

@Composable
fun SongLibraryPanel(
    songs: List<Song>,
    onSelectSong: (Song) -> Unit,
    onDeleteSong: (String) -> Unit,
    onNewSong: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredSongs = if (searchQuery.isBlank()) songs
    else songs.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.author.contains(searchQuery, ignoreCase = true)
    }

    Surface(
        modifier = modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Song Library",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${songs.size} songs",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search songs...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search", modifier = Modifier.size(18.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(Modifier.height(8.dp))

            // Song list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredSongs) { song ->
                    SongListItem(
                        song = song,
                        onClick = { onSelectSong(song) },
                        onDelete = { onDeleteSong(song.id) }
                    )
                }

                if (filteredSongs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (searchQuery.isNotBlank()) "No songs found"
                                else "No songs yet. Create one!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // New song button
            Button(
                onClick = onNewSong,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Song", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("New Song")
            }
        }
    }
}

@Composable
private fun SongListItem(
    song: Song,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (song.author.isNotBlank()) {
                    Text(
                        song.author,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (song.verses.isNotEmpty()) {
                    Text(
                        "${song.verses.size} verses",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (song.category.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        song.category,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    "Delete song",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SongEditorDialog(
    song: Song?,
    onDismiss: () -> Unit,
    onSave: (Song) -> Unit
) {
    val isNew = song == null
    var title by remember { mutableStateOf(song?.title ?: "") }
    var author by remember { mutableStateOf(song?.author ?: "") }
    var category by remember { mutableStateOf(song?.category ?: "") }
    var verses by remember { mutableStateOf(song?.verses ?: listOf(Verse(label = "V1", lines = emptyList()))) }
    var editingVerseIndex by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                if (isNew) "New Song" else "Edit Song",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Song Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // Author
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author / Arranger") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // Category
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g., Worship, Hymn, Christmas)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                // Verses section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Verses",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = {
                        val labels = listOf("V1", "V2", "V3", "V4", "Chorus", "Bridge", "Tag", "Intro", "Ending")
                        val usedLabels = verses.map { it.label }.toSet()
                        val nextLabel = labels.find { it !in usedLabels } ?: "V${verses.size + 1}"
                        verses = verses + Verse(label = nextLabel, lines = emptyList())
                    }) {
                        Icon(Icons.Default.Add, "Add verse", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add Verse", style = MaterialTheme.typography.labelMedium)
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(verses) { index, verse ->
                        VerseEditor(
                            verse = verse,
                            onChange = { updated ->
                                verses = verses.toMutableList().apply { set(index, updated) }
                            },
                            onDelete = {
                                verses = verses.toMutableList().apply { removeAt(index) }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = if (isNew) {
                        Song(
                            title = title,
                            author = author,
                            category = category,
                            verses = verses.filter { it.lines.isNotEmpty() || it.label.isNotBlank() }
                        )
                    } else {
                        song.copy(
                            title = title,
                            author = author,
                            category = category,
                            verses = verses.filter { it.lines.isNotEmpty() || it.label.isNotBlank() }
                        )
                    }
                    onSave(updated)
                    onDismiss()
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun VerseEditor(
    verse: Verse,
    onChange: (Verse) -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Verse label
                var label by remember(verse.label) { mutableStateOf(verse.label) }
                val labels = listOf("V1", "V2", "V3", "V4", "Chorus", "Bridge", "Tag", "Intro", "Ending")
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it; onChange(verse.copy(label = it)) },
                        modifier = Modifier.width(100.dp).menuAnchor(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.labelMedium
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        labels.forEach { l ->
                            DropdownMenuItem(
                                text = { Text(l) },
                                onClick = {
                                    label = l
                                    onChange(verse.copy(label = l))
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))

                // Verse lines
                var linesText by remember(verse.lines) {
                    mutableStateOf(verse.lines.joinToString("\n"))
                }
                OutlinedTextField(
                    value = linesText,
                    onValueChange = {
                        linesText = it
                        onChange(verse.copy(lines = it.lines()))
                    },
                    modifier = Modifier.weight(1f).heightIn(min = 60.dp),
                    shape = RoundedCornerShape(6.dp),
                    textStyle = MaterialTheme.typography.bodySmall,
                    placeholder = { Text("One line per row") }
                )

                // Delete verse button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Close, "Remove verse", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

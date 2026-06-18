package com.proclaimer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.proclaimer.model.BibleVerse

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
private fun ColumnScope.SavedVersesTab(
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
                    Icons.AutoMirrored.Filled.MenuBook,
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
                                Icons.AutoMirrored.Filled.MenuBook,
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

        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            label = { Text("Reference (e.g. John 3:16)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = translation,
            onValueChange = { translation = it },
            label = { Text("Translation (e.g. NIV, ESV, KJV)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category (e.g. Gospel, Worship, Comfort)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Verse Text") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
            shape = RoundedCornerShape(8.dp)
        )

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

        Button(
            onClick = {
                val verse = BibleVerse(
                    label = label,
                    translation = translation,
                    text = text,
                    category = category
                )
                onSave(verse)
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

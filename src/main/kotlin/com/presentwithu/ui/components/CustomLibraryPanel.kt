@file:OptIn(ExperimentalMaterial3Api::class)

package com.presentwithu.ui.components

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.presentwithu.model.LibraryItem
import com.presentwithu.model.LibraryItemType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryPanel(
    items: List<LibraryItem>,
    onAddToSlides: (LibraryItem) -> Unit,
    onDelete: (String) -> Unit,
    onSave: (LibraryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showEditor by remember { mutableStateOf(false) }

    val filtered = if (searchQuery.isBlank()) items
    else items.filter {
        it.label.contains(searchQuery, ignoreCase = true) ||
        it.content.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }

    Surface(
        modifier = modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxHeight().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Library",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${items.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search library...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search", modifier = Modifier.size(18.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filtered) { item ->
                    LibraryListItem(
                        item = item,
                        onAdd = { onAddToSlides(item) },
                        onDelete = { onDelete(item.id) }
                    )
                }

                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (searchQuery.isNotBlank()) "No items found"
                                else "No items yet. Create one!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { showEditor = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "New", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("New Item")
            }
        }
    }

    if (showEditor) {
        LibraryEditorDialog(
            onDismiss = { showEditor = false },
            onSave = { item ->
                onSave(item)
                showEditor = false
            }
        )
    }
}

@Composable
private fun LibraryListItem(
    item: LibraryItem,
    onAdd: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                when (item.type) {
                    LibraryItemType.SONG -> Icons.Default.MusicNote
                    LibraryItemType.SERMON -> Icons.Default.Mic
                    LibraryItemType.SERVICE_ORDER -> Icons.Default.EventNote
                    LibraryItemType.ANNOUNCEMENT -> Icons.Default.Campaign
                    LibraryItemType.VIDEO -> Icons.Default.PlayCircle
                    LibraryItemType.IMAGE -> Icons.Default.Image
                    else -> Icons.Default.Book
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.category.isNotBlank()) {
                    Text(
                        item.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    item.type.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onAdd, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Add, "Add to slides", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun LibraryEditorDialog(
    onDismiss: () -> Unit,
    onSave: (LibraryItem) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(LibraryItemType.SONG) }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var metadata by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("New Library Item") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = type.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        LibraryItemType.entries.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.name) },
                                onClick = {
                                    type = t
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(when (type) {
                        LibraryItemType.VIDEO, LibraryItemType.IMAGE -> "File path or URL"
                        else -> "Content"
                    }) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = metadata,
                    onValueChange = { metadata = it },
                    label = { Text("Metadata (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        LibraryItem(
                            type = type,
                            label = label,
                            content = content,
                            category = category,
                            metadata = metadata
                        )
                    )
                },
                enabled = label.isNotBlank() && content.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Deprecated("Use LibraryPanel instead")
@Composable
fun CustomLibraryPanel(
    items: List<com.presentwithu.model.CustomLibraryItem>,
    onAddToSlides: (com.presentwithu.model.CustomLibraryItem) -> Unit,
    onDelete: (String) -> Unit,
    onSave: (com.presentwithu.model.CustomLibraryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LibraryPanel(
        items = items.map { old ->
            LibraryItem(
                id = old.id,
                type = when (old.type) {
                    com.presentwithu.model.CustomLibraryItemType.SONG -> LibraryItemType.SONG
                    com.presentwithu.model.CustomLibraryItemType.SERMON -> LibraryItemType.SERMON
                    com.presentwithu.model.CustomLibraryItemType.SERVICE_ORDER -> LibraryItemType.SERVICE_ORDER
                },
                label = old.label,
                content = old.content,
                metadata = old.metadata,
                category = old.category,
                createdAt = old.createdAt,
                updatedAt = old.updatedAt
            )
        },
        onAddToSlides = { item ->
            val legacy = items.find { it.id == item.id }
            if (legacy != null) onAddToSlides(legacy)
        },
        onDelete = onDelete,
        onSave = { item ->
            onSave(
                com.presentwithu.model.CustomLibraryItem(
                    id = item.id,
                    type = when (item.type) {
                        LibraryItemType.SONG -> com.presentwithu.model.CustomLibraryItemType.SONG
                        LibraryItemType.SERMON -> com.presentwithu.model.CustomLibraryItemType.SERMON
                        LibraryItemType.SERVICE_ORDER -> com.presentwithu.model.CustomLibraryItemType.SERVICE_ORDER
                        else -> com.presentwithu.model.CustomLibraryItemType.SONG
                    },
                    label = item.label,
                    content = item.content,
                    metadata = item.metadata,
                    category = item.category,
                    createdAt = item.createdAt,
                    updatedAt = item.updatedAt
                )
            )
        },
        modifier = modifier
    )
}

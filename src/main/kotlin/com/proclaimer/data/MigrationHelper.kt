package com.proclaimer.data

import com.proclaimer.model.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File

object MigrationHelper {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun checkMigrationNeeded(dataDir: File): Boolean {
        val songsFile = File(dataDir, "songs.json")
        if (!songsFile.exists() || songsFile.length() <= 0) return false
        val content = songsFile.readText().filterNot { it.isWhitespace() }
        return content != "[]"
    }

    fun migrate(dataDir: File): Boolean {
        val songsFile = File(dataDir, "songs.json")
        val libraryFile = File(dataDir, "library.json")
        val backupFile = File(dataDir, "songs.json.migrated")

        if (!songsFile.exists()) return false

        try {
            val songsContent = songsFile.readText()
            val songs = try {
                json.decodeFromString<List<Song>>(songsContent)
            } catch (e: Exception) {
                emptyList()
            }

            if (songs.isEmpty()) {
                if (songsFile.exists()) {
                    if (backupFile.exists()) backupFile.delete()
                    songsFile.renameTo(backupFile)
                }
                return true
            }

            val migratedItems = songs.map { song ->
                val contentBuilder = StringBuilder()
                song.verses.forEachIndexed { index, verse ->
                    if (index > 0) contentBuilder.append("\n\n")
                    contentBuilder.append("[${verse.label}]\n")
                    contentBuilder.append(verse.lines.joinToString("\n"))
                }

                LibraryItem(
                    id = song.id,
                    type = LibraryItemType.SONG,
                    label = song.title,
                    content = contentBuilder.toString(),
                    note = song.author,
                    category = song.category,
                    createdAt = song.createdAt,
                    updatedAt = song.updatedAt
                )
            }

            val existingItems = if (libraryFile.exists()) {
                try {
                    json.decodeFromString<List<LibraryItem>>(libraryFile.readText())
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }

            val mergedItems = (existingItems + migratedItems).distinctBy { it.id }

            libraryFile.writeText(json.encodeToString(mergedItems))

            if (songsFile.exists()) {
                if (backupFile.exists()) backupFile.delete()
                songsFile.renameTo(backupFile)
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}

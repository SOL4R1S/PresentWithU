package com.proclaimer.data

import com.proclaimer.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class SongRepository(private val dataDir: File) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val songsFile: File get() = File(dataDir, "songs.json")
    private val presentationsFile: File get() = File(dataDir, "presentations.json")
    private val bibleVersesFile: File get() = File(dataDir, "bible_verses.json")

    init {
        dataDir.mkdirs()
        if (!songsFile.exists()) songsFile.writeText(json.encodeToString(emptyList<Song>()))
        if (!presentationsFile.exists()) presentationsFile.writeText(json.encodeToString(emptyList<Presentation>()))
        if (!bibleVersesFile.exists()) bibleVersesFile.writeText(json.encodeToString(emptyList<BibleVerse>()))
    }

    // --- Songs ---

    fun getAllSongs(): List<Song> {
        return try {
            json.decodeFromString<List<Song>>(songsFile.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getSong(id: String): Song? {
        return getAllSongs().find { it.id == id }
    }

    fun saveSong(song: Song): Song {
        val songs = getAllSongs().toMutableList()
        val idx = songs.indexOfFirst { it.id == song.id }
        val updated = song.copy(updatedAt = System.currentTimeMillis())
        if (idx >= 0) {
            songs[idx] = updated
        } else {
            songs.add(updated)
        }
        songsFile.writeText(json.encodeToString(songs))
        return updated
    }

    fun deleteSong(id: String) {
        val songs = getAllSongs().toMutableList()
        songs.removeAll { it.id == id }
        songsFile.writeText(json.encodeToString(songs))
    }

    // --- Presentations ---

    fun getAllPresentations(): List<Presentation> {
        return try {
            json.decodeFromString<List<Presentation>>(presentationsFile.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getPresentation(id: String): Presentation? {
        return getAllPresentations().find { it.id == id }
    }

    fun savePresentation(presentation: Presentation): Presentation {
        val presentations = getAllPresentations().toMutableList()
        val idx = presentations.indexOfFirst { it.id == presentation.id }
        val updated = presentation.copy(updatedAt = System.currentTimeMillis())
        if (idx >= 0) {
            presentations[idx] = updated
        } else {
            presentations.add(updated)
        }
        presentationsFile.writeText(json.encodeToString(presentations))
        return updated
    }

    fun deletePresentation(id: String) {
        val presentations = getAllPresentations().toMutableList()
        presentations.removeAll { it.id == id }
        presentationsFile.writeText(json.encodeToString(presentations))
    }

    // --- User-Created Bible Verses ---
    // Users manually type their own verse text to avoid copyrighted translation data.

    fun getAllBibleVerses(): List<BibleVerse> {
        return try {
            json.decodeFromString<List<BibleVerse>>(bibleVersesFile.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveBibleVerse(verse: BibleVerse): BibleVerse {
        val verses = getAllBibleVerses().toMutableList()
        val idx = verses.indexOfFirst { it.id == verse.id }
        if (idx >= 0) {
            verses[idx] = verse
        } else {
            verses.add(verse)
        }
        bibleVersesFile.writeText(json.encodeToString(verses))
        return verse
    }

    fun deleteBibleVerse(id: String) {
        val verses = getAllBibleVerses().toMutableList()
        verses.removeAll { it.id == id }
        bibleVersesFile.writeText(json.encodeToString(verses))
    }
}

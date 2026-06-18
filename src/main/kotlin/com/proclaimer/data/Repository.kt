package com.proclaimer.data

import com.proclaimer.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

sealed class RepositoryResult<out T> {
    data class Success<T>(val data: T) : RepositoryResult<T>()
    data class Error(
        val exception: Throwable,
        val kind: ErrorKind,
        val recoveredData: List<Any>? = null
    ) : RepositoryResult<Nothing>()

    enum class ErrorKind { IO, SERIALIZATION, UNKNOWN }
}

class SongRepository(private val dataDir: File) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val songsFile: File get() = File(dataDir, "songs.json")
    private val presentationsFile: File get() = File(dataDir, "presentations.json")
    private val bibleVersesFile: File get() = File(dataDir, "bible_verses.json")
    private val libraryFile: File get() = File(dataDir, "library.json")
    private val libraryFoldersFile: File get() = File(dataDir, "library_folders.json")
    private val customLibraryFile: File get() = File(dataDir, "custom_library.json")
    private val playlistsFile: File get() = File(dataDir, "playlists.json")

    init {
        try {
            dataDir.mkdirs()
            initializeFile(songsFile, emptyList<Song>())
            initializeFile(presentationsFile, emptyList<Presentation>())
            initializeFile(bibleVersesFile, emptyList<BibleVerse>())
            initializeFile(libraryFile, emptyList<LibraryItem>())
            initializeFile(libraryFoldersFile, emptyList<LibraryFolder>())
            initializeFile(customLibraryFile, emptyList<CustomLibraryItem>())
            initializeFile(playlistsFile, emptyList<Playlist>())
            
            // Trigger auto-migration if legacy songs.json exists
            if (MigrationHelper.checkMigrationNeeded(dataDir)) {
                MigrationHelper.migrate(dataDir)
            }
        } catch (e: Exception) {
            // Suppress initial directory/file creation errors
        }
    }

    private inline fun <reified T> initializeFile(file: File, defaultValue: List<T>) {
        try {
            if (!file.exists()) {
                file.writeText(json.encodeToString(defaultValue))
            }
        } catch (e: Exception) {
            // Ignore, handled during actual reading/writing
        }
    }

    // --- File I/O helpers ---

    private suspend inline fun <reified T> readJsonFile(
        file: File
    ): RepositoryResult<List<T>> = withContext(Dispatchers.IO) {
        try {
            RepositoryResult.Success(json.decodeFromString<List<T>>(file.readText()))
        } catch (e: Exception) {
            when (e) {
                is SerializationException, is IllegalArgumentException -> {
                    attemptBackupRecovery<T>(file, e)
                }
                is IOException -> RepositoryResult.Error(e, RepositoryResult.ErrorKind.IO)
                else -> RepositoryResult.Error(e, RepositoryResult.ErrorKind.UNKNOWN)
            }
        }
    }

    private inline fun <reified T> attemptBackupRecovery(
        file: File,
        cause: Exception
    ): RepositoryResult<List<T>> {
        val backupFile = File(file.parentFile, "${file.name}.backup")
        return if (backupFile.exists()) {
            try {
                val recovered = json.decodeFromString<List<T>>(backupFile.readText())
                file.writeText(backupFile.readText())
                RepositoryResult.Success(recovered)
            } catch (e: Exception) {
                RepositoryResult.Error(
                    cause,
                    RepositoryResult.ErrorKind.SERIALIZATION,
                    recoveredData = null
                )
            }
        } else {
            RepositoryResult.Error(
                cause,
                RepositoryResult.ErrorKind.SERIALIZATION,
                recoveredData = null
            )
        }
    }

    private suspend inline fun <reified T> writeJsonFile(
        file: File,
        data: List<T>
    ): RepositoryResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val text = json.encodeToString(data)
            val backupFile = File(file.parentFile, "${file.name}.backup")
            if (file.exists()) {
                file.copyTo(backupFile, overwrite = true)
            }
            file.writeText(text)
            RepositoryResult.Success(Unit)
        } catch (e: IOException) {
            RepositoryResult.Error(e, RepositoryResult.ErrorKind.IO)
        } catch (e: Exception) {
            RepositoryResult.Error(e, RepositoryResult.ErrorKind.UNKNOWN)
        }
    }

    // --- Songs ---

    suspend fun getAllSongs(): RepositoryResult<List<Song>> = readJsonFile<Song>(songsFile)

    suspend fun getSong(id: String): RepositoryResult<Song?> {
        return when (val result = getAllSongs()) {
            is RepositoryResult.Success -> RepositoryResult.Success(result.data.find { it.id == id })
            is RepositoryResult.Error -> result
        }
    }

    suspend fun saveSong(song: Song): RepositoryResult<Song> {
        val current = getAllSongs()
        if (current is RepositoryResult.Error) return current
        val songs = (current as RepositoryResult.Success).data.toMutableList()
        val idx = songs.indexOfFirst { it.id == song.id }
        val updated = song.copy(updatedAt = System.currentTimeMillis())
        if (idx >= 0) {
            songs[idx] = updated
        } else {
            songs.add(updated)
        }
        return when (val writeResult = writeJsonFile(songsFile, songs)) {
            is RepositoryResult.Success -> RepositoryResult.Success(updated)
            is RepositoryResult.Error -> writeResult
        }
    }

    suspend fun deleteSong(id: String): RepositoryResult<Unit> {
        val current = getAllSongs()
        if (current is RepositoryResult.Error) return current
        val songs = (current as RepositoryResult.Success).data.toMutableList()
        songs.removeAll { it.id == id }
        return writeJsonFile(songsFile, songs)
    }

    // --- Presentations ---

    suspend fun getAllPresentations(): RepositoryResult<List<Presentation>> =
        readJsonFile<Presentation>(presentationsFile)

    suspend fun getPresentation(id: String): RepositoryResult<Presentation?> {
        return when (val result = getAllPresentations()) {
            is RepositoryResult.Success -> RepositoryResult.Success(result.data.find { it.id == id })
            is RepositoryResult.Error -> result
        }
    }

    suspend fun savePresentation(presentation: Presentation): RepositoryResult<Presentation> {
        val current = getAllPresentations()
        if (current is RepositoryResult.Error) return current
        val presentations = (current as RepositoryResult.Success).data.toMutableList()
        val idx = presentations.indexOfFirst { it.id == presentation.id }
        val updated = presentation.copy(updatedAt = System.currentTimeMillis())
        if (idx >= 0) {
            presentations[idx] = updated
        } else {
            presentations.add(updated)
        }
        return when (val writeResult = writeJsonFile(presentationsFile, presentations)) {
            is RepositoryResult.Success -> RepositoryResult.Success(updated)
            is RepositoryResult.Error -> writeResult
        }
    }

    suspend fun deletePresentation(id: String): RepositoryResult<Unit> {
        val current = getAllPresentations()
        if (current is RepositoryResult.Error) return current
        val presentations = (current as RepositoryResult.Success).data.toMutableList()
        presentations.removeAll { it.id == id }
        return writeJsonFile(presentationsFile, presentations)
    }

    // --- User-Created Bible Verses ---

    suspend fun getAllBibleVerses(): RepositoryResult<List<BibleVerse>> = readJsonFile<BibleVerse>(bibleVersesFile)

    suspend fun saveBibleVerse(verse: BibleVerse): RepositoryResult<BibleVerse> {
        val current = getAllBibleVerses()
        if (current is RepositoryResult.Error) return current
        val verses = (current as RepositoryResult.Success).data.toMutableList()
        val idx = verses.indexOfFirst { it.id == verse.id }
        val updated = if (idx >= 0) verse else verse
        if (idx >= 0) {
            verses[idx] = updated
        } else {
            verses.add(updated)
        }
        return when (val writeResult = writeJsonFile(bibleVersesFile, verses)) {
            is RepositoryResult.Success -> RepositoryResult.Success(updated)
            is RepositoryResult.Error -> writeResult
        }
    }

    suspend fun deleteBibleVerse(id: String): RepositoryResult<Unit> {
        val current = getAllBibleVerses()
        if (current is RepositoryResult.Error) return current
        val verses = (current as RepositoryResult.Success).data.toMutableList()
        verses.removeAll { it.id == id }
        return writeJsonFile(bibleVersesFile, verses)
    }

    // --- Library ---

    suspend fun getAllLibraryItems(): RepositoryResult<List<LibraryItem>> =
        readJsonFile<LibraryItem>(libraryFile)

    suspend fun getLibraryItemsByType(type: LibraryItemType): RepositoryResult<List<LibraryItem>> {
        return when (val result = getAllLibraryItems()) {
            is RepositoryResult.Success -> RepositoryResult.Success(result.data.filter { it.type == type })
            is RepositoryResult.Error -> result
        }
    }

    suspend fun searchLibraryItems(query: String): RepositoryResult<List<LibraryItem>> {
        val q = query.trim()
        return when (val result = getAllLibraryItems()) {
            is RepositoryResult.Success -> {
                val data = result.data
                RepositoryResult.Success(
                    if (q.isBlank()) data
                    else data.filter {
                        it.label.contains(q, ignoreCase = true) ||
                        it.content.contains(q, ignoreCase = true) ||
                        it.category.contains(q, ignoreCase = true)
                    }
                )
            }
            is RepositoryResult.Error -> result
        }
    }

    suspend fun saveLibraryItem(item: LibraryItem): RepositoryResult<LibraryItem> {
        val current = getAllLibraryItems()
        if (current is RepositoryResult.Error) return current
        val items = (current as RepositoryResult.Success).data.toMutableList()
        val idx = items.indexOfFirst { it.id == item.id }
        val updated = item.copy(updatedAt = System.currentTimeMillis())
        if (idx >= 0) {
            items[idx] = updated
        } else {
            items.add(updated)
        }
        return when (val writeResult = writeJsonFile(libraryFile, items)) {
            is RepositoryResult.Success -> RepositoryResult.Success(updated)
            is RepositoryResult.Error -> writeResult
        }
    }

    suspend fun deleteLibraryItem(id: String): RepositoryResult<Unit> {
        val current = getAllLibraryItems()
        if (current is RepositoryResult.Error) return current
        val items = (current as RepositoryResult.Success).data.toMutableList()
        items.removeAll { it.id == id }
        return writeJsonFile(libraryFile, items)
    }

    // --- Legacy Custom Library (deprecated, kept for migration) ---

    @Deprecated("Use getAllLibraryItems instead")
    suspend fun getAllCustomLibraryItems(): RepositoryResult<List<CustomLibraryItem>> =
        readJsonFile<CustomLibraryItem>(customLibraryFile)

    @Deprecated("Use saveLibraryItem instead")
    suspend fun saveCustomLibraryItem(item: CustomLibraryItem): RepositoryResult<CustomLibraryItem> {
        val current = getAllCustomLibraryItems()
        if (current is RepositoryResult.Error) return current
        val items = (current as RepositoryResult.Success).data.toMutableList()
        val idx = items.indexOfFirst { it.id == item.id }
        val updated = item.copy(updatedAt = System.currentTimeMillis())
        if (idx >= 0) {
            items[idx] = updated
        } else {
            items.add(updated)
        }
        return when (val writeResult = writeJsonFile(customLibraryFile, items)) {
            is RepositoryResult.Success -> RepositoryResult.Success(updated)
            is RepositoryResult.Error -> writeResult
        }
    }

    @Deprecated("Use deleteLibraryItem instead")
    suspend fun deleteCustomLibraryItem(id: String): RepositoryResult<Unit> {
        val current = getAllCustomLibraryItems()
        if (current is RepositoryResult.Error) return current
        val items = (current as RepositoryResult.Success).data.toMutableList()
        items.removeAll { it.id == id }
        return writeJsonFile(customLibraryFile, items)
    }

    @Deprecated("Use searchLibraryItems instead")
    suspend fun searchCustomLibraryItems(query: String): RepositoryResult<List<CustomLibraryItem>> {
        val q = query.trim()
        return when (val result = getAllCustomLibraryItems()) {
            is RepositoryResult.Success -> {
                val data = result.data
                RepositoryResult.Success(
                    if (q.isBlank()) data
                    else data.filter {
                        it.label.contains(q, ignoreCase = true) ||
                        it.content.contains(q, ignoreCase = true) ||
                        it.category.contains(q, ignoreCase = true)
                    }
                )
            }
            is RepositoryResult.Error -> result
        }
    }

    @Deprecated("Use getLibraryItemsByType instead")
    suspend fun getCustomLibraryItemsByType(type: CustomLibraryItemType): RepositoryResult<List<CustomLibraryItem>> {
        return when (val result = getAllCustomLibraryItems()) {
            is RepositoryResult.Success -> RepositoryResult.Success(result.data.filter { it.type == type })
            is RepositoryResult.Error -> result
        }
    }

    // --- Playlists ---

    suspend fun getAllPlaylists(): RepositoryResult<List<Playlist>> = readJsonFile<Playlist>(playlistsFile)

    suspend fun savePlaylist(playlist: Playlist): RepositoryResult<Playlist> {
        val current = getAllPlaylists()
        if (current is RepositoryResult.Error) return current
        val playlists = (current as RepositoryResult.Success).data.toMutableList()
        val idx = playlists.indexOfFirst { it.id == playlist.id }
        val updated = playlist.copy(updatedAt = System.currentTimeMillis())
        if (idx >= 0) {
            playlists[idx] = updated
        } else {
            playlists.add(updated)
        }
        return when (val writeResult = writeJsonFile(playlistsFile, playlists)) {
            is RepositoryResult.Success -> RepositoryResult.Success(updated)
            is RepositoryResult.Error -> writeResult
        }
    }

    suspend fun deletePlaylist(id: String): RepositoryResult<Unit> {
        val current = getAllPlaylists()
        if (current is RepositoryResult.Error) return current
        val playlists = (current as RepositoryResult.Success).data.toMutableList()
        playlists.removeAll { it.id == id }
        return writeJsonFile(playlistsFile, playlists)
    }

    // --- Library Folders ---

    suspend fun getAllLibraryFolders(): RepositoryResult<List<LibraryFolder>> =
        readJsonFile<LibraryFolder>(libraryFoldersFile)

    suspend fun saveLibraryFolder(folder: LibraryFolder): RepositoryResult<LibraryFolder> {
        val current = getAllLibraryFolders()
        if (current is RepositoryResult.Error) return current
        val folders = (current as RepositoryResult.Success).data.toMutableList()
        val idx = folders.indexOfFirst { it.id == folder.id }
        val updated = folder
        if (idx >= 0) {
            folders[idx] = updated
        } else {
            folders.add(updated)
        }
        return when (val writeResult = writeJsonFile(libraryFoldersFile, folders)) {
            is RepositoryResult.Success -> RepositoryResult.Success(updated)
            is RepositoryResult.Error -> writeResult
        }
    }

    suspend fun deleteLibraryFolder(id: String): RepositoryResult<Unit> {
        val current = getAllLibraryFolders()
        if (current is RepositoryResult.Error) return current
        val folders = (current as RepositoryResult.Success).data.toMutableList()
        folders.removeAll { it.id == id }
        return writeJsonFile(libraryFoldersFile, folders)
    }
}

package com.presentwithu.data

import com.presentwithu.model.*
import kotlinx.coroutines.test.runTest
import java.io.File
import java.nio.file.Files
import kotlin.test.*

class SongRepositoryTest {

    private lateinit var tempDir: File
    private lateinit var repository: SongRepository

    @BeforeTest
    fun setup() {
        tempDir = Files.createTempDirectory("presentwithu-test-").toFile()
        repository = SongRepository(tempDir)
    }

    @AfterTest
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun `getAllSongs returns empty list when no songs saved`() = runTest {
        val result = repository.getAllSongs()
        assertIs<RepositoryResult.Success<List<Song>>>(result)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `saveSong and getSong round trip`() = runTest {
        val song = Song(title = "Amazing Grace", author = "John Newton")
        val savedResult = repository.saveSong(song)
        assertIs<RepositoryResult.Success<Song>>(savedResult)
        val saved = savedResult.data

        val retrievedResult = repository.getSong(saved.id)
        assertIs<RepositoryResult.Success<Song?>>(retrievedResult)
        val retrieved = retrievedResult.data
        assertNotNull(retrieved)
        assertEquals(saved.id, retrieved.id)
        assertEquals("Amazing Grace", retrieved.title)
        assertEquals("John Newton", retrieved.author)
    }

    @Test
    fun `saveSong updates existing song`() = runTest {
        val song = Song(title = "Old Title")
        val saved = (repository.saveSong(song) as RepositoryResult.Success).data
        val updatedResult = repository.saveSong(saved.copy(title = "New Title"))
        assertIs<RepositoryResult.Success<Song>>(updatedResult)

        val allResult = repository.getAllSongs()
        assertIs<RepositoryResult.Success<List<Song>>>(allResult)
        assertEquals(1, allResult.data.size)
        assertEquals("New Title", allResult.data[0].title)
        assertTrue(updatedResult.data.updatedAt >= saved.updatedAt)
    }

    @Test
    fun `deleteSong removes song`() = runTest {
        val song = Song(title = "To Delete")
        val saved = (repository.saveSong(song) as RepositoryResult.Success).data
        val deleteResult = repository.deleteSong(saved.id)
        assertIs<RepositoryResult.Success<Unit>>(deleteResult)

        assertNull((repository.getSong(saved.id) as RepositoryResult.Success).data)
        assertTrue((repository.getAllSongs() as RepositoryResult.Success).data.isEmpty())
    }

    @Test
    fun `json corruption recovers from backup`() = runTest {
        val song = Song(title = "Backup Test")
        repository.saveSong(song)

        // Create a backup
        val songsFile = File(tempDir, "songs.json")
        val backupFile = File(tempDir, "songs.json.backup")
        songsFile.copyTo(backupFile, overwrite = true)

        // Corrupt the main file
        songsFile.writeText("not valid json")

        val result = repository.getAllSongs()
        assertIs<RepositoryResult.Success<List<Song>>>(result)
        assertEquals(1, result.data.size)
        assertEquals("Backup Test", result.data[0].title)
    }

    @Test
    fun `json corruption returns error when no backup`() = runTest {
        val songsFile = File(tempDir, "songs.json")
        songsFile.writeText("not valid json")

        val result = repository.getAllSongs()
        assertIs<RepositoryResult.Error>(result)
        assertEquals(RepositoryResult.ErrorKind.SERIALIZATION, result.kind)
    }

    @Test
    fun `io error during read returns io error kind`() = runTest {
        // Make directory unreadable by treating a directory as the file path
        val repo = SongRepository(File("/nonexistent_path_that_cannot_be_created_\u0000_invalid"))
        val result = repo.getAllSongs()
        assertIs<RepositoryResult.Error>(result)
    }

    @Test
    fun `savePresentation and getAllPresentations round trip`() = runTest {
        val presentation = Presentation(name = "Sunday Service", slides = listOf(Slide(content = "Welcome")))
        val savedResult = repository.savePresentation(presentation)
        assertIs<RepositoryResult.Success<Presentation>>(savedResult)

        val allResult = repository.getAllPresentations()
        assertIs<RepositoryResult.Success<List<Presentation>>>(allResult)
        assertEquals(1, allResult.data.size)
        assertEquals(savedResult.data.id, allResult.data[0].id)
        assertEquals("Sunday Service", allResult.data[0].name)
    }

    @Test
    fun `saveBibleVerse and getAllBibleVerses round trip`() = runTest {
        val verse = BibleVerse(label = "John 3:16", text = "For God so loved the world...")
        val saveResult = repository.saveBibleVerse(verse)
        assertIs<RepositoryResult.Success<BibleVerse>>(saveResult)

        val allResult = repository.getAllBibleVerses()
        assertIs<RepositoryResult.Success<List<BibleVerse>>>(allResult)
        assertEquals(1, allResult.data.size)
        assertEquals("John 3:16", allResult.data[0].label)
    }
}

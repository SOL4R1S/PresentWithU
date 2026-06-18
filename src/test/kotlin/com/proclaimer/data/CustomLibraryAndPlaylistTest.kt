package com.proclaimer.data

import com.proclaimer.model.*
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.*

class CustomLibraryAndPlaylistTest {

    private lateinit var tempDir: java.io.File
    private lateinit var repository: SongRepository

    @BeforeTest
    fun setup() {
        tempDir = Files.createTempDirectory("proclaimer-test-").toFile()
        repository = SongRepository(tempDir)
    }

    @AfterTest
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun `save and retrieve library items`() = runTest {
        val songItem = LibraryItem(
            type = LibraryItemType.SONG,
            label = "Amazing Grace",
            content = "content",
            category = "Worship"
        )
        val sermonItem = LibraryItem(
            type = LibraryItemType.SERMON,
            label = "Sunday Sermon",
            content = "sermon content",
            category = "Gospel"
        )

        assertIs<RepositoryResult.Success<LibraryItem>>(repository.saveLibraryItem(songItem))
        assertIs<RepositoryResult.Success<LibraryItem>>(repository.saveLibraryItem(sermonItem))

        val allResult = repository.getAllLibraryItems()
        assertIs<RepositoryResult.Success<List<LibraryItem>>>(allResult)
        assertEquals(2, allResult.data.size)

        val songsResult = repository.getLibraryItemsByType(LibraryItemType.SONG)
        assertIs<RepositoryResult.Success<List<LibraryItem>>>(songsResult)
        assertEquals(1, songsResult.data.size)
        assertEquals("Amazing Grace", songsResult.data[0].label)
    }

    @Test
    fun `search library items`() = runTest {
        repository.saveLibraryItem(
            LibraryItem(type = LibraryItemType.SONG, label = "Amazing Grace", content = "Lyrics here")
        )
        repository.saveLibraryItem(
            LibraryItem(type = LibraryItemType.SERMON, label = "Grace Message", content = "Sermon")
        )

        val resultsResult = repository.searchLibraryItems("grace")
        assertIs<RepositoryResult.Success<List<LibraryItem>>>(resultsResult)
        assertEquals(2, resultsResult.data.size)

        val resultsByLabelResult = repository.searchLibraryItems("Amazing")
        assertIs<RepositoryResult.Success<List<LibraryItem>>>(resultsByLabelResult)
        assertEquals(1, resultsByLabelResult.data.size)
    }

    @Test
    fun `delete library item`() = runTest {
        val saveResult = repository.saveLibraryItem(
            LibraryItem(type = LibraryItemType.SERVICE_ORDER, label = "Order 1")
        )
        assertIs<RepositoryResult.Success<LibraryItem>>(saveResult)
        val deleteResult = repository.deleteLibraryItem(saveResult.data.id)
        assertIs<RepositoryResult.Success<Unit>>(deleteResult)

        val allResult = repository.getAllLibraryItems()
        assertIs<RepositoryResult.Success<List<LibraryItem>>>(allResult)
        assertTrue(allResult.data.isEmpty())
    }

    @Test
    fun `legacy custom library API still works`() = runTest {
        val songItem = CustomLibraryItem(
            type = CustomLibraryItemType.SONG,
            label = "Amazing Grace",
            content = "content",
            category = "Worship"
        )
        assertIs<RepositoryResult.Success<CustomLibraryItem>>(repository.saveCustomLibraryItem(songItem))

        val allResult = repository.getAllCustomLibraryItems()
        assertIs<RepositoryResult.Success<List<CustomLibraryItem>>>(allResult)
        assertEquals(1, allResult.data.size)
    }

    @Test
    fun `save and retrieve playlist`() = runTest {
        val itemResult = repository.saveLibraryItem(
            LibraryItem(type = LibraryItemType.SONG, label = "Song 1")
        )
        assertIs<RepositoryResult.Success<LibraryItem>>(itemResult)
        val playlist = Playlist(
            name = "Sunday Service",
            items = listOf(
                PlaylistItem(type = PlaylistItemType.SONG, sourceId = itemResult.data.id, label = "Song 1")
            )
        )
        val savedResult = repository.savePlaylist(playlist)
        assertIs<RepositoryResult.Success<Playlist>>(savedResult)

        val allResult = repository.getAllPlaylists()
        assertIs<RepositoryResult.Success<List<Playlist>>>(allResult)
        assertEquals(1, allResult.data.size)
        assertEquals("Sunday Service", allResult.data[0].name)
        assertEquals(1, allResult.data[0].items.size)
        assertEquals(itemResult.data.id, allResult.data[0].items[0].sourceId)
    }

    @Test
    fun `delete playlist`() = runTest {
        val saved = repository.savePlaylist(Playlist(name = "Temp"))
        assertIs<RepositoryResult.Success<Playlist>>(saved)
        val deleteResult = repository.deletePlaylist(saved.data.id)
        assertIs<RepositoryResult.Success<Unit>>(deleteResult)

        val allResult = repository.getAllPlaylists()
        assertIs<RepositoryResult.Success<List<Playlist>>>(allResult)
        assertTrue(allResult.data.isEmpty())
    }
}

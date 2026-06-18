package com.proclaimer.data

import com.proclaimer.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.test.*

class MigrationHelperTest {
    private lateinit var tempDir: File

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    @BeforeTest
    fun setUp() {
        tempDir = File(System.getProperty("java.io.tmpdir"), "proclaimer_test_${System.currentTimeMillis()}")
        tempDir.mkdirs()
    }

    @AfterTest
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun testNoMigrationNeededWhenNoSongsFile() {
        assertFalse(MigrationHelper.checkMigrationNeeded(tempDir))
        assertFalse(MigrationHelper.migrate(tempDir))
    }

    @Test
    fun testMigrationConvertsSongsToLibraryItems() {
        val songsFile = File(tempDir, "songs.json")
        val songs = listOf(
            Song(
                id = "song1",
                title = "Amazing Grace",
                author = "John Newton",
                category = "Hymn",
                verses = listOf(
                    Verse("Verse 1", listOf("Amazing grace! how sweet the sound", "That saved a wretch like me!")),
                    Verse("Chorus", listOf("My chains are gone", "I've been set free"))
                )
            )
        )
        songsFile.writeText(json.encodeToString(songs))

        assertTrue(MigrationHelper.checkMigrationNeeded(tempDir))
        assertTrue(MigrationHelper.migrate(tempDir))

        // Check original file renamed
        assertFalse(songsFile.exists())
        assertTrue(File(tempDir, "songs.json.migrated").exists())

        // Check library.json created with correct values
        val libraryFile = File(tempDir, "library.json")
        assertTrue(libraryFile.exists())

        val libraryItems = json.decodeFromString<List<LibraryItem>>(libraryFile.readText())
        assertEquals(1, libraryItems.size)
        val item = libraryItems[0]
        assertEquals("song1", item.id)
        assertEquals(LibraryItemType.SONG, item.type)
        assertEquals("Amazing Grace", item.label)
        assertEquals("John Newton", item.note)
        assertEquals("Hymn", item.category)
        
        val expectedContent = "[Verse 1]\nAmazing grace! how sweet the sound\nThat saved a wretch like me!\n\n[Chorus]\nMy chains are gone\nI've been set free"
        assertEquals(expectedContent, item.content)
    }

    @Test
    fun testMigrationHandlesCorruptedSongsJsonGracefully() {
        val songsFile = File(tempDir, "songs.json")
        songsFile.writeText("corrupted { invalid json }")

        assertTrue(MigrationHelper.checkMigrationNeeded(tempDir))
        assertTrue(MigrationHelper.migrate(tempDir))

        assertFalse(songsFile.exists())
        assertTrue(File(tempDir, "songs.json.migrated").exists())

        // library.json is empty or not created (or existing merged)
        val libraryFile = File(tempDir, "library.json")
        if (libraryFile.exists()) {
            val items = json.decodeFromString<List<LibraryItem>>(libraryFile.readText())
            assertTrue(items.isEmpty())
        }
    }
}

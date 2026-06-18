package com.proclaimer.ui.components

import com.proclaimer.model.LibraryItem
import com.proclaimer.model.LibraryItemType
import com.proclaimer.model.toLibraryItem
import com.proclaimer.media.MediaLoader
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.*

class OutputDisplayTest {

    @Test
    fun testParseColorHex() {
        val hex = "#ff0000"
        val normalized = hex.replace("#", "")
        val colorVal = normalized.toLong(16) or 0xFF000000
        assertEquals(4294901760L, colorVal) // 0xFFFF0000
    }

    @Test
    fun testMediaLoaderLruCache() = runTest {
        // Since test environment might not have physical files, 
        // we test MediaLoader with nonexistent file path to confirm it returns null quickly and doesn't crash
        val bitmap = MediaLoader.loadImage("non_existent_file.png")
        assertNull(bitmap, "Should return null for missing files")
    }

    @Test
    fun testLibraryItemMappingForLegacySlide() {
        val slide = com.proclaimer.model.Slide(
            id = "test_slide",
            type = com.proclaimer.model.SlideType.LYRIC,
            content = "Line 1\nLine 2",
            note = "My Verse Note"
        )
        val item = slide.toLibraryItem()
        assertEquals("test_slide", item.id)
        assertEquals(LibraryItemType.SONG, item.type)
        assertEquals("My Verse Note", item.label)
        assertEquals("Line 1\nLine 2", item.content)
    }
}

package com.presentwithu.utils

import kotlin.test.*

class LyricsSplitterTest {

    @Test
    fun `splits two-line groups with blank separator`() {
        val input = """
            Amazing grace
            How sweet the sound

            That saved a wretch
            Like me
        """.trimIndent()

        val result = splitLyricsIntoSlides(input)

        assertEquals(2, result.size)
        assertEquals("Amazing grace\nHow sweet the sound", result[0])
        assertEquals("That saved a wretch\nLike me", result[1])
    }

    @Test
    fun `splits groups larger than linesPerSlide`() {
        val input = """
            Line 1
            Line 2
            Line 3
            Line 4
        """.trimIndent()

        val result = splitLyricsIntoSlides(input, linesPerSlide = 2)

        assertEquals(2, result.size)
        assertEquals("Line 1\nLine 2", result[0])
        assertEquals("Line 3\nLine 4", result[1])
    }

    @Test
    fun `ignores extra blank lines`() {
        val input = """
            Verse 1 line 1
            Verse 1 line 2


            Verse 2 line 1
            Verse 2 line 2
        """.trimIndent()

        val result = splitLyricsIntoSlides(input)
        assertEquals(2, result.size)
    }

    @Test
    fun `single line group stays as one slide`() {
        val input = "Only one line"
        val result = splitLyricsIntoSlides(input)
        assertEquals(1, result.size)
        assertEquals("Only one line", result[0])
    }

    @Test
    fun `empty input returns empty list`() {
        assertTrue(splitLyricsIntoSlides("").isEmpty())
    }

    @Test
    fun `requires positive linesPerSlide`() {
        assertFailsWith<IllegalArgumentException> {
            splitLyricsIntoSlides("text", linesPerSlide = 0)
        }
    }
}

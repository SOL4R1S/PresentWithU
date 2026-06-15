package com.proclaimer.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: String = generateId(),
    val title: String = "",
    val author: String = "",
    val verses: List<Verse> = emptyList(),
    val category: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class Verse(
    val label: String = "",       // "V1", "Chorus", "Bridge", "Tag"
    val lines: List<String> = emptyList()
)

@Serializable
data class Slide(
    val id: String = generateId(),
    val type: SlideType = SlideType.LYRIC,
    val content: String = "",
    val note: String = "",
    val backgroundColor: String = "#1a1a2e",
    val textColor: String = "#FFFFFF",
    val backgroundImagePath: String = "",
    val order: Int = 0
)

@Serializable
enum class SlideType {
    LYRIC,       // Song lyrics
    SCRIPTURE,   // Bible verse
    ANNOUNCEMENT,// Church announcements
    TITLE,       // Title slide
    BLANK,       // Blank/black slide
    IMAGE        // Full-screen image
}

@Serializable
data class Presentation(
    val id: String = generateId(),
    val name: String = "",
    val slides: List<Slide> = emptyList(),
    val songIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * A user-created Bible verse entry.
 * Users manually type verse text to avoid copyrighted translation data.
 */
@Serializable
data class BibleVerse(
    val id: String = generateId(),
    val label: String = "",          // User's custom label (e.g., "John 3:16")
    val translation: String = "",    // Which translation (user types this too)
    val text: String = "",
    val category: String = "",       // e.g. "Gospel", "Worship", "Comfort"
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class PlaylistItem(
    val id: String = generateId(),
    val type: PlaylistItemType = PlaylistItemType.SLIDE,
    val sourceId: String = "",      // Song ID, Presentation ID, etc.
    val label: String = "",
    val slideIndex: Int = 0
)

@Serializable
enum class PlaylistItemType {
    SLIDE,
    SONG,
    PRESENTATION,
    BIBLE_VERSE,
    MEDIA
}

fun generateId(): String {
    val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
    return (1..12).map { chars.random() }.joinToString("")
}

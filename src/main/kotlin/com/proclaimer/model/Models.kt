package com.proclaimer.model

import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Replaced by LibraryItem; kept for backward-compatible JSON migration")
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
@Deprecated("Replaced by LibraryItem; kept for backward-compatible JSON migration")
data class Verse(
    val label: String = "",       // "V1", "Chorus", "Bridge", "Tag"
    val lines: List<String> = emptyList()
)

@Serializable
data class LibraryFolder(
    val id: String = generateId(),
    val name: String = "",
    val path: String = "",
    val children: List<LibraryFolder> = emptyList(),
    val items: List<LibraryItem> = emptyList()
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

@Serializable
@Deprecated("Replaced by LibraryItem; kept for backward-compatible JSON migration")
data class CustomLibraryItem(
    val id: String = generateId(),
    val type: CustomLibraryItemType = CustomLibraryItemType.SONG,
    val label: String = "",
    val content: String = "",
    val metadata: String = "",
    val category: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
@Deprecated("Replaced by LibraryItemType; kept for backward-compatible JSON migration")
enum class CustomLibraryItemType {
    SONG,
    SERMON,
    SERVICE_ORDER
}

@Serializable
enum class LibraryItemType {
    SONG,
    SERMON,
    SERVICE_ORDER,
    ANNOUNCEMENT,
    TITLE,
    BLANK,
    IMAGE,
    VIDEO
}

@Serializable
data class LibraryItem(
    val id: String = generateId(),
    val type: LibraryItemType = LibraryItemType.SONG,
    val label: String = "",
    val content: String = "",
    val note: String = "",
    val metadata: String = "",
    val category: String = "",
    val backgroundColor: String = "#1a1a2e",
    val textColor: String = "#FFFFFF",
    val mediaPath: String = "",
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

fun Slide.toLibraryItem(): LibraryItem = LibraryItem(
    id = id,
    type = when (type) {
        SlideType.LYRIC -> LibraryItemType.SONG
        SlideType.SCRIPTURE -> LibraryItemType.SERMON
        SlideType.ANNOUNCEMENT -> LibraryItemType.ANNOUNCEMENT
        SlideType.TITLE -> LibraryItemType.TITLE
        SlideType.BLANK -> LibraryItemType.BLANK
        SlideType.IMAGE -> LibraryItemType.IMAGE
    },
    label = note,
    content = content,
    note = note,
    backgroundColor = backgroundColor,
    textColor = textColor,
    mediaPath = backgroundImagePath,
    order = order
)

@Serializable
data class Playlist(
    val id: String = generateId(),
    val name: String = "",
    val items: List<PlaylistItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

fun generateId(): String {
    val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
    return (1..12).map { chars.random() }.joinToString("")
}

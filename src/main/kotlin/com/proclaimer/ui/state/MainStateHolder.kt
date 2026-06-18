package com.proclaimer.ui.state

import com.proclaimer.data.RepositoryResult
import com.proclaimer.data.SongRepository

import com.proclaimer.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainStateHolder(
    private val repository: SongRepository,
    private val scope: CoroutineScope
) {
    private val _slides = MutableStateFlow(listOf<Slide>())
    val slides: StateFlow<List<Slide>> = _slides.asStateFlow()

    private val _currentSlideIndex = MutableStateFlow(0)
    val currentSlideIndex: StateFlow<Int> = _currentSlideIndex.asStateFlow()

    private val _songs = MutableStateFlow(listOf<Song>())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _presentations = MutableStateFlow(listOf<Presentation>())
    val presentations: StateFlow<List<Presentation>> = _presentations.asStateFlow()

    private val _currentPresentation = MutableStateFlow<Presentation?>(null)
    val currentPresentation: StateFlow<Presentation?> = _currentPresentation.asStateFlow()

    private val _bibleVerses = MutableStateFlow(listOf<BibleVerse>())
    val bibleVerses: StateFlow<List<BibleVerse>> = _bibleVerses.asStateFlow()

    private val _libraryItems = MutableStateFlow(listOf<LibraryItem>())
    val libraryItems: StateFlow<List<LibraryItem>> = _libraryItems.asStateFlow()

    @Deprecated("Use libraryItems instead")
    private val _customLibraryItems = MutableStateFlow(listOf<CustomLibraryItem>())

    private val _playlists = MutableStateFlow(listOf<Playlist>())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _setlist = MutableStateFlow(listOf<LibraryItem>())
    val setlist: StateFlow<List<LibraryItem>> = _setlist.asStateFlow()

    private val _setlistIndex = MutableStateFlow(0)
    val setlistIndex: StateFlow<Int> = _setlistIndex.asStateFlow()

    private val _isDragging = MutableStateFlow(false)
    val isDragging: StateFlow<Boolean> = _isDragging.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _showSongDialog = MutableStateFlow(false)
    val showSongDialog: StateFlow<Boolean> = _showSongDialog.asStateFlow()

    private val _editingSong = MutableStateFlow<Song?>(null)
    val editingSong: StateFlow<Song?> = _editingSong.asStateFlow()

    private val _showBibleDialog = MutableStateFlow(false)
    val showBibleDialog: StateFlow<Boolean> = _showBibleDialog.asStateFlow()

    private val _showPresentationDialog = MutableStateFlow(false)
    val showPresentationDialog: StateFlow<Boolean> = _showPresentationDialog.asStateFlow()

    private val _showCustomLibraryDialog = MutableStateFlow(false)
    val showCustomLibraryDialog: StateFlow<Boolean> = _showCustomLibraryDialog.asStateFlow()

    private val _showPlaylistDialog = MutableStateFlow(false)
    val showPlaylistDialog: StateFlow<Boolean> = _showPlaylistDialog.asStateFlow()

    private val _lastError = MutableStateFlow<Throwable?>(null)
    val lastError: StateFlow<Throwable?> = _lastError.asStateFlow()


    init {
        loadData()
    }

    fun loadData() {
        scope.launch {
            repository.getAllSongs().onSuccess { _songs.value = it }
            repository.getAllPresentations().onSuccess { _presentations.value = it }
            repository.getAllBibleVerses().onSuccess { _bibleVerses.value = it }
            repository.getAllLibraryItems().onSuccess { _libraryItems.value = it }
            repository.getAllPlaylists().onSuccess { _playlists.value = it }
        }
    }

    fun clearError() {
        _lastError.value = null
    }

    private inline fun <T> RepositoryResult<T>.onSuccess(action: (T) -> Unit): T? {
        return when (this) {
            is RepositoryResult.Success -> {
                action(data)
                data
            }
            is RepositoryResult.Error -> {
                _lastError.value = exception
                null
            }
        }
    }

    fun selectSlide(index: Int) {
        _currentSlideIndex.value = index
    }

    fun selectSetlistItem(index: Int) {
        _setlistIndex.value = index
    }

    fun addToSetlist(item: LibraryItem) {
        _setlist.value = _setlist.value + item
    }

    fun removeFromSetlist(index: Int) {
        val mutable = _setlist.value.toMutableList()
        if (index in mutable.indices) {
            mutable.removeAt(index)
            _setlist.value = mutable
            if (_setlistIndex.value >= mutable.size) {
                _setlistIndex.value = maxOf(0, mutable.size - 1)
            }
        }
    }

    fun reorderSetlist(fromIndex: Int, toIndex: Int) {
        val mutable = _setlist.value.toMutableList()
        if (fromIndex in mutable.indices && toIndex in mutable.indices) {
            val item = mutable.removeAt(fromIndex)
            mutable.add(toIndex, item)
            _setlist.value = mutable
            if (_setlistIndex.value == fromIndex) {
                _setlistIndex.value = toIndex
            } else if (_setlistIndex.value in minOf(fromIndex, toIndex)..maxOf(fromIndex, toIndex)) {
                if (fromIndex < toIndex) {
                    _setlistIndex.value = _setlistIndex.value - 1
                } else {
                    _setlistIndex.value = _setlistIndex.value + 1
                }
            }
        }
    }

    fun setDragging(dragging: Boolean) {
        _isDragging.value = dragging
    }

    fun addSlide() {
        _slides.value = _slides.value + Slide(
            type = SlideType.LYRIC,
            content = "",
            order = _slides.value.size
        )
        _currentSlideIndex.value = _slides.value.size - 1
    }

    fun addBlankSlide() {
        _slides.value = _slides.value + Slide(
            type = SlideType.BLANK,
            content = "",
            order = _slides.value.size
        )
        _currentSlideIndex.value = _slides.value.size - 1
    }

    fun addAnnouncementSlide() {
        _slides.value = _slides.value + Slide(
            type = SlideType.ANNOUNCEMENT,
            content = "",
            order = _slides.value.size
        )
        _currentSlideIndex.value = _slides.value.size - 1
    }

    fun deleteSlide(index: Int) {
        val mutable = _slides.value.toMutableList()
        if (index in mutable.indices) {
            mutable.removeAt(index)
            _slides.value = mutable
            if (_currentSlideIndex.value >= mutable.size) {
                _currentSlideIndex.value = mutable.size - 1
            }
        }
    }

    fun moveSlideUp(index: Int) {
        if (index <= 0) return
        val mutable = _slides.value.toMutableList()
        val item = mutable.removeAt(index)
        mutable.add(index - 1, item)
        _slides.value = mutable
        _currentSlideIndex.value = index - 1
    }

    fun moveSlideDown(index: Int) {
        if (index >= _slides.value.size - 1) return
        val mutable = _slides.value.toMutableList()
        val item = mutable.removeAt(index)
        mutable.add(index + 1, item)
        _slides.value = mutable
        _currentSlideIndex.value = index + 1
    }

    fun updateSlide(updated: Slide, index: Int) {
        val mutable = _slides.value.toMutableList()
        if (index in mutable.indices) {
            mutable[index] = updated
            _slides.value = mutable
        }
    }

    fun addSongToSlides(song: Song) {
        val songSlides = song.verses.flatMap { verse ->
            if (verse.lines.isEmpty()) {
                listOf(Slide(type = SlideType.LYRIC, content = "", note = verse.label))
            } else {
                verse.lines.chunked(2).map { chunk ->
                    Slide(
                        type = SlideType.LYRIC,
                        content = chunk.joinToString("\n"),
                        note = verse.label
                    )
                }
            }
        }
        _slides.value = _slides.value + songSlides
        _currentSlideIndex.value = _slides.value.size - songSlides.size
    }

    fun addBibleVerseToSlides(verse: BibleVerse) {
        if (verse.text.isBlank()) return
        _slides.value = _slides.value + Slide(
            type = SlideType.SCRIPTURE,
            content = verse.text,
            note = verse.label,
            order = _slides.value.size
        )
        _currentSlideIndex.value = _slides.value.size - 1
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun showSongEditor(song: Song? = null) {
        _editingSong.value = song
        _showSongDialog.value = true
    }

    fun dismissSongDialog() {
        _showSongDialog.value = false
    }

    fun saveSong(song: Song) {
        scope.launch {
            repository.saveSong(song).onSuccess {
                loadData()
                _showSongDialog.value = false
            }
        }
    }


    fun deleteSong(id: String) {
        scope.launch {
            repository.deleteSong(id).onSuccess { loadData() }
        }
    }


    fun showBibleDialog() {
        _showBibleDialog.value = true
    }

    fun dismissBibleDialog() {
        _showBibleDialog.value = false
    }

    fun saveBibleVerse(verse: BibleVerse) {
        scope.launch {
            repository.saveBibleVerse(verse).onSuccess { loadData() }
        }
    }


    fun deleteBibleVerse(id: String) {
        scope.launch {
            repository.deleteBibleVerse(id).onSuccess { loadData() }
        }
    }


    fun showPresentationDialog() {
        _showPresentationDialog.value = true
    }

    fun dismissPresentationDialog() {
        _showPresentationDialog.value = false
    }

    fun savePresentation(name: String) {
        scope.launch {
            val pres = _currentPresentation.value?.copy(
                name = name,
                slides = _slides.value,
                updatedAt = System.currentTimeMillis()
            ) ?: Presentation(
                name = name,
                slides = _slides.value
            )
            repository.savePresentation(pres).onSuccess {
                _currentPresentation.value = pres
                loadData()
                _showPresentationDialog.value = false
            }
        }
    }


    fun loadPresentation(pres: Presentation) {
        _currentPresentation.value = pres
        _slides.value = pres.slides
        _currentSlideIndex.value = 0
        _showPresentationDialog.value = false
    }

    fun deletePresentation(id: String) {
        scope.launch {
            repository.deletePresentation(id).onSuccess { loadData() }
        }
    }


    fun showCustomLibraryDialog() {
        _showCustomLibraryDialog.value = true
    }

    fun dismissCustomLibraryDialog() {
        _showCustomLibraryDialog.value = false
    }

    fun saveLibraryItem(item: LibraryItem) {
        scope.launch {
            repository.saveLibraryItem(item).onSuccess { loadData() }
        }
    }

    fun deleteLibraryItem(id: String) {
        scope.launch {
            repository.deleteLibraryItem(id).onSuccess { loadData() }
        }
    }

    fun addLibraryItemToSlides(item: LibraryItem) {
        when (item.type) {
            LibraryItemType.SONG -> {
                val chunks = item.content.split("\n\n")
                val songSlides = chunks.flatMap { chunk ->
                    chunk.lines().filter { it.isNotBlank() }.chunked(2).map { lines ->
                        Slide(
                            type = SlideType.LYRIC,
                            content = lines.joinToString("\n"),
                            note = item.label
                        )
                    }
                }
                _slides.value = _slides.value + songSlides
                _currentSlideIndex.value = _slides.value.size - songSlides.size
            }
            LibraryItemType.SERMON,
            LibraryItemType.SERVICE_ORDER,
            LibraryItemType.ANNOUNCEMENT -> {
                if (item.content.isNotBlank()) {
                    _slides.value = _slides.value + Slide(
                        type = SlideType.ANNOUNCEMENT,
                        content = item.content,
                        note = item.label,
                        order = _slides.value.size
                    )
                    _currentSlideIndex.value = _slides.value.size - 1
                }
            }
            LibraryItemType.VIDEO,
            LibraryItemType.IMAGE -> {
                _slides.value = _slides.value + Slide(
                    type = SlideType.IMAGE,
                    content = item.label,
                    note = item.metadata,
                    backgroundImagePath = item.content,
                    order = _slides.value.size
                )
                _currentSlideIndex.value = _slides.value.size - 1
            }
            LibraryItemType.TITLE,
            LibraryItemType.BLANK -> {
                _slides.value = _slides.value + Slide(
                    type = if (item.type == LibraryItemType.TITLE) SlideType.TITLE else SlideType.BLANK,
                    content = item.content,
                    note = item.label,
                    order = _slides.value.size
                )
                _currentSlideIndex.value = _slides.value.size - 1
            }
        }
    }

    @Deprecated("Use saveLibraryItem instead")
    fun saveCustomLibraryItem(item: CustomLibraryItem) {
        scope.launch {
            repository.saveCustomLibraryItem(item).onSuccess { loadData() }
        }
    }

    @Deprecated("Use deleteLibraryItem instead")
    fun deleteCustomLibraryItem(id: String) {
        scope.launch {
            repository.deleteCustomLibraryItem(id).onSuccess { loadData() }
        }
    }

    @Deprecated("Use addLibraryItemToSlides instead")
    fun addCustomLibraryItemToSlides(item: CustomLibraryItem) {
        val migrated = LibraryItem(
            id = item.id,
            type = when (item.type) {
                CustomLibraryItemType.SONG -> LibraryItemType.SONG
                CustomLibraryItemType.SERMON -> LibraryItemType.SERMON
                CustomLibraryItemType.SERVICE_ORDER -> LibraryItemType.SERVICE_ORDER
            },
            label = item.label,
            content = item.content,
            metadata = item.metadata,
            category = item.category,
            createdAt = item.createdAt,
            updatedAt = item.updatedAt
        )
        addLibraryItemToSlides(migrated)
    }

    fun showPlaylistDialog() {
        _showPlaylistDialog.value = true
    }

    fun dismissPlaylistDialog() {
        _showPlaylistDialog.value = false
    }

    fun savePlaylist(playlist: Playlist) {
        scope.launch {
            repository.savePlaylist(playlist).onSuccess { loadData() }
        }
    }


    fun deletePlaylist(id: String) {
        scope.launch {
            repository.deletePlaylist(id).onSuccess { loadData() }
        }
    }

    fun applyPlaylist(playlist: Playlist) {
        val newSlides = mutableListOf<Slide>()
        playlist.items.forEach { item ->
            when (item.type) {
                PlaylistItemType.SONG -> {
                    val libraryItem = _libraryItems.value.find {
                        it.id == item.sourceId && it.type == LibraryItemType.SONG
                    }
                    if (libraryItem != null) {
                        newSlides.addAll(
                            libraryItem.content.split("\n\n").flatMap { chunk ->
                                chunk.lines().filter { it.isNotBlank() }.chunked(2).map { lines ->
                                    Slide(type = SlideType.LYRIC, content = lines.joinToString("\n"), note = libraryItem.label)
                                }
                            }
                        )
                    }
                }
                PlaylistItemType.PRESENTATION -> {
                    val pres = _presentations.value.find { it.id == item.sourceId }
                    if (pres != null) newSlides.addAll(pres.slides)
                }
                PlaylistItemType.BIBLE_VERSE -> {
                    val verse = _bibleVerses.value.find { it.id == item.sourceId }
                    if (verse != null && verse.text.isNotBlank()) {
                        newSlides.add(Slide(type = SlideType.SCRIPTURE, content = verse.text, note = verse.label))
                    }
                }
                else -> { /* ignore */ }
            }
        }
        _slides.value = newSlides
        _currentSlideIndex.value = 0
    }

    fun getCurrentSlides(): List<Slide> = _slides.value

    fun getCurrentSlideIndex(): Int = _currentSlideIndex.value
}

package com.presentwithu.utils

/**
 * Splits lyrics text into slide-sized chunks following the ProPresenter-style convention:
 * - Blank lines separate groups
 * - Each group becomes one or more slides, with up to [linesPerSlide] lines per slide
 *
 * Example input:
 *   Amazing grace
 *   How sweet the sound
 *
 *   That saved a wretch
 *   Like me
 *
 * With linesPerSlide = 2, produces:
 *   ["Amazing grace\nHow sweet the sound", "That saved a wretch\nLike me"]
 */
fun splitLyricsIntoSlides(text: String, linesPerSlide: Int = 2): List<String> {
    require(linesPerSlide > 0) { "linesPerSlide must be positive" }

    val groups = text.trim().split(Regex("\n{2,}"))
    val result = mutableListOf<String>()

    for (group in groups) {
        val lines = group.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) continue

        lines.chunked(linesPerSlide).forEach { chunk ->
            result.add(chunk.joinToString("\n"))
        }
    }

    return result
}

/**
 * Convenience function that returns a list of content strings ready to become Slide objects.
 */
fun splitLyricsForPresentation(text: String, linesPerSlide: Int = 2): List<String> {
    return splitLyricsIntoSlides(text, linesPerSlide)
}

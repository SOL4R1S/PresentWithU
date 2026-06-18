package com.proclaimer.media

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.imageio.ImageIO

object MediaLoader {
    private val cache = LinkedHashMap<String, ImageBitmap>(20, 0.75f, true)
    private const val MAX_CACHE_SIZE = 20

    suspend fun loadImage(path: String): ImageBitmap? = withContext(Dispatchers.IO) {
        if (path.isBlank()) return@withContext null

        synchronized(cache) {
            val cached = cache[path]
            if (cached != null) return@withContext cached
        }

        try {
            val file = File(path)
            if (!file.exists()) return@withContext null

            val image = ImageIO.read(file) ?: return@withContext null
            val bitmap = image.toComposeImageBitmap()

            synchronized(cache) {
                if (cache.size >= MAX_CACHE_SIZE) {
                    val eldestKey = cache.keys.firstOrNull()
                    if (eldestKey != null) {
                        cache.remove(eldestKey)
                    }
                }
                cache[path] = bitmap
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

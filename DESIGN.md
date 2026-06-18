# Proclaimer App Design & Architecture Audit

## 1. Gradle Version Audit

Here is a review of the current major dependencies and their latest available versions:

*   **Kotlin:**
    *   *Current:* 1.9.22
    *   *Latest:* 2.4.0 (or 2.3.10 required by the latest Compose Multiplatform)
    *   *Compatibility Concerns:* Updating to Kotlin 2.x introduces the K2 compiler, which may require minor syntax updates or plugin adjustments. Compose compiler is now bundled directly with Kotlin in 2.x, so the `org.jetbrains.kotlin.plugin.compose` plugin will be required instead of matching the Compose compiler version manually.
*   **Compose Multiplatform:**
    *   *Current:* 1.6.0
    *   *Latest:* 1.11.1
    *   *Compatibility Concerns:* Minimum Kotlin requirement for 1.11.1 is Kotlin 2.3.10. It brings native iOS text input and major performance improvements, along with v2 testing APIs.
*   **kotlinx-serialization-json:**
    *   *Current:* 1.6.2
    *   *Latest:* 1.11.0
    *   *Compatibility Concerns:* Based on Kotlin 2.3.20. It introduces new `JsonException` APIs which will require refactoring if the app does any explicit JSON error catching.
*   **kotlinx-coroutines:**
    *   *Current:* 1.7.3
    *   *Latest:* 1.11.0
    *   *Compatibility Concerns:* Fully compatible with Kotlin 2.x.

**Recommendation:** Update all libraries to their latest versions simultaneously to ensure compatibility, specifically adopting Kotlin 2.4.0 and Compose 1.11.1.

---

## 2. GUI Review

### Layout and Visual Polish
*   **Pros:** The app effectively uses Material 3 components (`Surface`, `Cards`, `Buttons`) and creates a clear layout structure. The dark theme is very appropriate for low-light AV environments.
*   **Cons:** The UI lacks visual flair. Hardcoded colors (e.g., `Color(0xFF0D0D1A)`, `Color.White`) are used instead of relying entirely on the `MaterialTheme` color scheme, which breaks theming consistency. 
*   **Gradients & Polish:** Gradients are entirely missing. Using `Brush.verticalGradient` or `Brush.linearGradient` for the slide previews or presenter view background could significantly elevate the premium feel of the app. 
*   **Animations:** There are no apparent slide transition animations (like crossfades using `AnimatedContent`). This makes switching between slides feel abrupt.

### Typography
*   The `ProclaimerTypography` in `Theme.kt` is comprehensive but relies on system default fonts. For a presentation app where legibility and aesthetic are paramount, a modern, clean font like *Inter*, *Roboto*, or *Outfit* should be loaded and applied to the `FontFamily`.

### Responsiveness / Window Sizing
*   The `Main.kt` sets a hardcoded starting window size of `1400x900`. 
*   Panels in `MainScreen.kt` use hardcoded widths (e.g., `Modifier.width(260.dp)` for left and right panels). While fine for large desktop monitors, this may cause layout clipping or cramping on smaller laptop screens. Using `Modifier.weight()` or a responsive adaptive layout strategy is recommended.

### Missing Features or UX gaps
*   **Keyboard Shortcuts:** `PresenterScreen.kt` has a comment `// Keyboard shortcuts (handled via buttons for desktop)` but no actual `onKeyEvent` listeners are implemented. Keyboard navigation (Left/Right arrows) is critical for presentation software.
*   **Empty States:** While the Bible library handles empty states nicely, the slide viewer and presenter screens may look bare or confusing without slides.

---

## 3. Code Quality

### Architecture
*   **High Coupling:** The application lacks a clear presentation architecture (like MVVM or MVI). Global state variables and business logic (like updating lists after database changes using `refreshSongs()`) are managed directly inside the Compose UI layer (`MainScreen.kt`). 
*   **Separation of Concerns:** The UI layer is tightly coupled to the `SongRepository`. A `ViewModel` or a global state holder class should be introduced to hoist business logic out of the view layer.

### State Management
*   **Over-reliance on `remember { mutableStateOf(...) }`:** Huge amounts of state are held locally in `MainScreen` (slides, songs, bible verses, dialog visibility). This can lead to massive recompositions whenever a small piece of state changes. State should be migrated to `StateFlow` within a dedicated state holder.

### Error Handling
*   **Swallowing Exceptions:** In `Repository.kt`, JSON decoding functions (`getAllSongs`, `getAllPresentations`) catch all exceptions and silently return an `emptyList()`. If the JSON file is corrupted, the user's data silently disappears without any error logging, user feedback, or backup recovery attempts.
*   There is no input validation or error feedback when saving empty slides or songs.

### Performance
*   **Synchronous File I/O on the Main Thread:** `Repository.kt` reads from and writes to `File` objects synchronously using `.readText()` and `.writeText()`. When the user saves a presentation or song, it blocks the main UI thread. Even though `kotlinx-coroutines` is in the `build.gradle.kts`, it is completely unused.
*   **JSON Serialization Cost:** Re-encoding the entire list of songs or presentations to JSON for every single save or delete operation will severely degrade performance as the database grows.
*   **Recommendation:** Offload all repository operations to `Dispatchers.IO` using coroutines and consider using an actual local database (like Room or SQLDelight) rather than flat JSON files for better scalability.

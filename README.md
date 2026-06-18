# PresentWithU 🎤

**Church Presentation Software** — Kotlin + Compose Multiplatform Desktop

A clean, modern alternative to ProPresenter for churches. Built with Kotlin and Compose Desktop, targeting **macOS**, **Windows**, and **Linux**.

## Features

| Feature | Description |
|---------|-------------|
| 📝 **Slide Editor** | Create and edit slides — lyrics, scriptures, announcements, titles |
| 🎵 **Song Library** | Store songs with verses (V1, V2, Chorus, Bridge) for quick access |
| 📖 **Bible Verse Library** | User-created personal Bible verse library (no copyrighted text included) |
| 🖥️ **Presenter View** | Full presenter interface with audience preview + navigation |
| 👁️ **Stage Display** | Confidence monitor for stage talent with next-slide preview |
| 🎨 **Dark Theme** | Low-light friendly design optimized for church AV environments |
| 💾 **Save/Load** | JSON-based persistence — no database needed |
| 🎬 **Slide Types** | Lyric, Scripture, Announcement, Title, Blank, Image |

## Requirements

- **JDK 17** or later (download from [Adoptium](https://adoptium.net/))
- macOS, Windows, or Linux

## Quick Start

```bash
# 1. Build the project
./gradlew build

# 2. Run
./gradlew run

# 3. Package native installer
./gradlew packageDmg        # macOS
./gradlew packageMsi        # Windows
./gradlew packageDeb        # Linux
```

### macOS

```bash
brew install openjdk@17
./gradlew run
```

### Windows

```powershell
# Install JDK 17 from https://adoptium.net/
.\gradlew.bat run
```

## Usage

1. **Create slides** in the main editor — type content, choose slide type
2. **Add songs** from the Song Library tab
3. **Look up Bible verses** with the Bible tool
4. **Start Presentation** to go fullscreen with presenter controls
5. **Stage Display** opens a separate presentation-optimized window

## Project Structure

```
presentwithu/
├── build.gradle.kts          # Compose Desktop build config
├── settings.gradle.kts
├── src/main/kotlin/
│   └── com/presentwithu/
│       ├── Main.kt           # Entry point
│       ├── model/Models.kt   # Data models (Song, Slide, Presentation)
│       ├── data/Repository.kt # JSON file storage + Bible data
│       └── ui/
│           ├── App.kt        # Screen navigation
│           ├── theme/Theme.kt # Material 3 dark theme
│           ├── components/   # Reusable UI components
│           │   ├── SlideList.kt
│           │   ├── SlideEditor.kt
│           │   ├── SongLibrary.kt
│           │   └── PresentationView.kt
│           └── screens/      # Full-screen views
│               ├── MainScreen.kt
│               ├── PresenterScreen.kt
│               └── StageDisplayScreen.kt
└── gradle/
    └── wrapper/
```

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose Desktop (Material 3)
- **Build:** Gradle with Compose Multiplatform plugin
- **Storage:** Local JSON files via kotlinx-serialization
- **Targets:** macOS (.dmg), Windows (.msi), Linux (.deb)

## Git Hooks

This repository uses local git hooks to ensure tests are run before commits and pushes.

- `pre-commit`: Runs `./gradlew test --no-daemon` before each commit.
- `pre-push`: Runs `./gradlew test --no-daemon` before each push.

To set up the git hooks in your workspace:
```bash
cp pre-commit pre-push .git/hooks/
chmod +x .git/hooks/pre-commit
chmod +x .git/hooks/pre-push
```

## License

MIT

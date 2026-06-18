import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.4.0"
    id("org.jetbrains.compose") version "1.12.0-alpha01"
    kotlin("plugin.serialization") version "2.4.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0"
}

group = "com.presentwithu"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material3:material3-desktop:1.7.3")
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.7.3")

    // Serialization for JSON storage
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

compose.desktop {
    application {
        mainClass = "com.presentwithu.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "PresentWithU"
            packageVersion = "1.0.0"
            description = "Church Presentation Software"
            vendor = "PresentWithU"

            linux {
                iconFile.set(project.file("src/main/resources/icon.png"))
            }

            windows {
                iconFile.set(project.file("src/main/resources/icon.ico"))
                menuGroup = "PresentWithU"
                upgradeUuid = "e3a2c1b0-4d5f-6a7b-8c9d-0e1f2a3b4c5d"
            }

            macOS {
                iconFile.set(project.file("src/main/resources/icon.icns"))
                bundleID = "com.presentwithu.app"
            }
        }
    }
}

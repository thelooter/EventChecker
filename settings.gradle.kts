plugins {
    id("com.gradle.develocity") version "3.18"
}

rootProject.name = "eventchecker"

develocity {
    server = "https://scans.gradle.com"
}
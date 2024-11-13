plugins {
    id("com.gradle.develocity") version "3.18.2"
}

rootProject.name = "eventchecker"

develocity {
    server = "https://scans.gradle.com"
}
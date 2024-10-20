plugins {
    id("com.gradle.develocity") version "3.18.1"
}

rootProject.name = "eventchecker"

develocity {
    server = "https://scans.gradle.com"
}
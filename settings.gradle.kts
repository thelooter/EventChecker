plugins {
    id("com.gradle.develocity") version "3.17.5"
}

rootProject.name = "eventchecker"

develocity {
    server = "https://scans.gradle.com"
}
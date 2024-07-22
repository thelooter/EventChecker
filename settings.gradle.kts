plugins {
    id("com.gradle.develocity") version "3.17.6"
}

rootProject.name = "eventchecker"

develocity {
    server = "https://scans.gradle.com"
}
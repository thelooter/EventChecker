plugins {
    id("com.gradle.develocity") version "3.17.3"
}

rootProject.name = "eventchecker"

develocity {
    server = "https://scans.gradle.com"
}
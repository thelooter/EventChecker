plugins {
    id("com.gradle.develocity") version "3.17.4"
}

rootProject.name = "eventchecker"

develocity {
    server = "https://scans.gradle.com"
}
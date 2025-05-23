plugins {
    id("com.gradle.develocity") version "4.0.1"
}

rootProject.name = "eventchecker"

develocity {
    server = "https://scans.gradle.com"
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = if (System.getenv("GITHUB_WORKFLOW").isNullOrEmpty()) "yes" else "no"
    }
}
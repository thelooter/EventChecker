plugins {
    id("com.gradle.enterprise") version("3.15.1")
}

rootProject.name = "eventchecker"

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
plugins {
    id("com.gradle.enterprise") version("3.17.3")
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
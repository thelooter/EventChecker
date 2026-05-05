plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.gradleup.shadow)
    alias(libs.plugins.jacoco)
    alias(libs.plugins.maven.publish)
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.jetbrains.annotations)

    implementation(libs.kotlin.stdlib)
    implementation(libs.classgraph)
    implementation(libs.apache.commons.lang3)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockbukkit)
    testImplementation(libs.hamcrest)

    testRuntimeOnly(libs.junit.platform.launcher)

}

dependencyLocking {
    lockAllConfigurations()

    ignoredDependencies.add("io.papermc.paper:paper-api")
}

version = "1.5.0"
group = "de.thelooter"

tasks {

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    kotlin {
        jvmToolchain(21)
    }

    jar {
        archiveVersion.set(project.version.toString())
    }

    shadowJar {
        relocate("io.github.classgraph", "de.thelooter.classgraph")
        relocate("org.apache.commons", "de.thelooter.commons")

        archiveBaseName.set("eventchecker")
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())

        mergeServiceFiles()
    }

    assemble {
        dependsOn(shadowJar)
    }

    test {
        useJUnitPlatform()
        maxParallelForks = providers.gradleProperty("test.maxParallelForks")
            .map(String::toInt)
            .getOrElse((Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1))

        finalizedBy(jacocoTestReport)
    }

    jacoco {
        toolVersion = "0.8.12"
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }

    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.85".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.60".toBigDecimal()
                }
            }
        }

        dependsOn(jacocoTestReport)
    }

    check {
        dependsOn(jacocoTestReport)
        dependsOn(jacocoTestCoverageVerification)
    }

    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "eventchecker"
            version = project.version.toString()

            artifact(tasks.shadowJar)
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/thelooter/EventChecker")
            credentials {
                username =
                    providers.gradleProperty("gpr.user")
                        .orElse(providers.environmentVariable("GITHUB_ACTOR"))
                        .orNull
                password =
                    providers.gradleProperty("gpr.key")
                        .orElse(providers.environmentVariable("GITHUB_TOKEN"))
                        .orNull
            }
        }
    }
}
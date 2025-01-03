import java.lang.Runtime

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.github.goooler.shadow") version "8.1.8"
    jacoco
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:26.0.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
    implementation("io.github.classgraph:classgraph:4.8.179")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.23.0")
    testImplementation("org.hamcrest:hamcrest-library:3.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

val jarVersion = "1.5.0"

tasks {

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    shadowJar {
        relocate("io.github.classgraph", "de.thelooter.classgraph")
        relocate("org.apache.commons", "de.thelooter.commons")

        archiveFileName.set("eventchecker-$jarVersion.jar")
    }

    jar {
        dependsOn(shadowJar)
    }

    test {
        useJUnitPlatform()
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
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
        dependsOn(test)
    }

    check {
        dependsOn(jacocoTestReport)
    }

}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.thelooter"
            artifactId = "eventchecker"
            version = jarVersion

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/thelooter/EventChecker")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
plugins {
    kotlin("jvm") version "1.9.24"
    id("com.github.johnrengelman.shadow") version "8.1.1"
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
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.1.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
    implementation("io.github.classgraph:classgraph:4.8.172")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.86.1")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
}

val jarVersion = "1.4.0"

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
        dependsOn(test)
    }

    test {
        useJUnitPlatform()
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
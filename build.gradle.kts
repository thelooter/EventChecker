plugins {
    kotlin("jvm") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    jacoco
    id("org.sonarqube") version "4.4.1.3373"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.0.1")

    implementation("io.github.classgraph:classgraph:4.8.162")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.31.0")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
}

val jarVersion = "1.2.0"

tasks {

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    shadowJar {
        relocate("io.github.classgraph", "de.thelooter.classgraph")
        relocate("org.apache.commons", "de.thelooter.commons")

        archiveFileName.set("EventChecker-$jarVersion.jar")
    }

    jar {
        dependsOn(shadowJar)
        dependsOn(test)
    }

    test {
        useJUnitPlatform()
    }

    jacoco {
        toolVersion = "0.8.10"
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
            html.required.set(true)

            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }

}

sonar {
    properties {
        property("sonar.projectKey", "thelooter_EventChecker")
        property("sonar.organization", "thelooter")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime
import java.time.ZoneId
import java.util.Properties

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.distsDirectory

val ktorVersion: String by project
val cliktVersion: String by project
val hopliteVersion: String by project
val slf4jVersion: String by project

val versionPropertiesFile = "${projectDir}/version.properties"

fun String.runCommand(currentWorkingDir: File = file("./")): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        workingDir = currentWorkingDir
        commandLine = this@runCommand.split("\\s".toRegex())
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}

fun getRevision(): String {
    return "git rev-parse --short=7 HEAD".runCommand()
}

fun getProperties(file: String, key: String): String {
    val fileInputStream = FileInputStream(file)
    val props = Properties()
    props.load(fileInputStream)
    return props.getProperty(key)
}

fun getVersion(): String {
    return getProperties(versionPropertiesFile, "version")
}

fun getStage(): String {
    return getProperties(versionPropertiesFile, "stage")
}

plugins {
    kotlin("jvm") version "1.8.21"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "tech.ixor"
version = getVersion() + "-" + getStage() + "+" + getRevision()

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    val versionProperties by registering(WriteProperties::class) {
        destinationFile.set { file("${buildDir}/resources/main/version.properties") }
        encoding = "UTF-8"
        property("version", getVersion())
        property("stage", getStage())
        property("revision", getRevision())
        property("buildDate",
            ZonedDateTime
                .now(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("E, MMM dd yyyy"))
        )
    }

    var shadowJarVersion = getVersion()
    shadowJar {
        if (getStage() == "dev" || getStage() == "alpha" || getStage() == "beta" || getStage() == "rc") {
            shadowJarVersion = shadowJarVersion + "-" + getStage()
        }
        shadowJarVersion = shadowJarVersion + "+" + getRevision()
        archiveVersion.set(shadowJarVersion)
        archiveClassifier.set("")
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        exclude("conf/config.yaml")
        from(versionProperties)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // HTTP client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-java:$ktorVersion")
    // CLI parser
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
    // Configurations Loader
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")
    // Logging
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("LCTT.Toolkit")
        mergeServiceFiles()
    }
}

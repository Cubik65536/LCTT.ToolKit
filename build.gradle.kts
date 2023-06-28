import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktorVersion: String by project
val cliktVersion: String by project
val hopliteVersion: String by project

plugins {
    kotlin("jvm") version "1.8.21"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "tech.ixor"
version = "0.0.1-alpha"

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

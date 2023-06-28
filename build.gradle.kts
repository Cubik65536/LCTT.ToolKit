val ktorVersion: String by project
val cliktVersion: String by project

plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "tech.ixor"
version = "0.0.1-alpha"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}
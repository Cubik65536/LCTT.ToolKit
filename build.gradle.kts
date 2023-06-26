plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "tech.ixor"
version = "0.0.1-alpha"

repositories {
    mavenCentral()
}



kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}
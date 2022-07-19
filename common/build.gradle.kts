plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.uten2c"
version = "1.0-SNAPSHOT"

dependencies {
    shadow("org.jetbrains:annotations:23.0.0")
}

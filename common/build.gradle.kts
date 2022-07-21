plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.uten2c"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://libraries.minecraft.net")
}

dependencies {
    shadow("org.jetbrains:annotations:23.0.0")
    shadow("com.mojang:brigadier:1.0.18")
}

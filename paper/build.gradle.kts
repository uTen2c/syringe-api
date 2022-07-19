plugins {
    id("io.papermc.paperweight.userdev") version "1.3.7"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.uten2c"
version = "1.0-SNAPSHOT"

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
    implementation(project(":common"))
    implementation(project(":paper-api"))
}

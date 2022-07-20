plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.uten2c"
version = Version.PROJECT

dependencies {
    shadow("io.papermc.paper:paper-api:${Version.PAPER}")
    implementation(project(":common"))
}

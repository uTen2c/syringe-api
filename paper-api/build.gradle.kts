plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.uten2c"
version = "1.0-SNAPSHOT"

dependencies {
    shadow("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    implementation(project(":common"))
}

plugins {
    id("io.papermc.paperweight.userdev") version "1.3.7"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.uten2c"
version = Version.PROJECT

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to project.version))
    }
}

dependencies {
    paperDevBundle(Version.PAPER)
    implementation(project(":common"))
    implementation(project(":paper-api"))
}

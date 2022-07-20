pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net")
        maven("https://repo.papermc.io/repository/maven-public/")
        gradlePluginPortal()
    }
}

rootProject.name = "syringe-api"

include("common", "fabric", "paper-api", "paper")

plugins {
    `java-library`
}

group = "dev.uten2c"
version = Version.PROJECT

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

subprojects {
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

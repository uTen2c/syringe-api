plugins {
    id("fabric-loom") version "0.12-SNAPSHOT"
}

group = "dev.uten2c"
version = Version.PROJECT

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to project.version,
                "minecraft" to Version.MINECRAFT
            )
        )
    }
}

sourceSets {
    val main = getByName("main")
    create("testmod") {
        compileClasspath += main.compileClasspath
        compileClasspath += main.output
        runtimeClasspath += main.runtimeClasspath
    }
}

fun DependencyHandlerScope.includeAndImplementation(dep: Any) {
    implementation(dep)
    include(dep)
}

dependencies {
    minecraft("com.mojang:minecraft:${Version.MINECRAFT}")
    mappings("net.fabricmc:yarn:${Version.YARN}:v2")
    modImplementation("net.fabricmc:fabric-loader:${Version.LOADER}")
    arrayOf("fabric-api-base", "fabric-networking-api-v1", "fabric-command-api-v2", "fabric-registry-sync-v0")
        .map { fabricApi.module(it, Version.FABRIC) }
        .forEach(::modImplementation)
    includeAndImplementation(project(":common"))
}

loom {
    accessWidenerPath.set(file("src/main/resources/syringe-api.accesswidener"))
    runs {
        create("testmodServer") {
            server()
            ideConfigGenerated(true)
            name("Testmod Server")
            source(sourceSets.getByName("testmod"))
        }
    }
}

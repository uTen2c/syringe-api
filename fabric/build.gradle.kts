plugins {
    id("fabric-loom") version "0.12-SNAPSHOT"
}

group = "dev.uten2c"
version = "1.0-SNAPSHOT"

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to project.version))
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
    minecraft("com.mojang:minecraft:1.19")
    mappings("net.fabricmc:yarn:1.19+build.4:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.8")
    arrayOf("fabric-api-base", "fabric-networking-api-v1", "fabric-command-api-v2")
        .map { fabricApi.module(it, "0.57.0+1.19") }
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

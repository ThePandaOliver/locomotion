@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow")
}

val loader = prop("loom.platform")!!
val minecraft: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}.project

version = "${prop("mod.version")}+$minecraft"
base {
    archivesName.set("${prop("mod.id")}-$loader")
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    silentMojangMappingsLicense()

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runs {
        val runDir = "../../../.runs"

        named("client") {
            client()
            configName = "Client"
            runDir("$runDir/client")
            source(sourceSets["main"])
            programArgs("--username=Dev")
        }
        named("server") {
            server()
            configName = "Server"
            runDir("$runDir/server")
            source(sourceSets["main"])
        }
    }

    runConfigs.all {
        isIdeConfigGenerated = true
    }

    forge.convertAccessWideners = true
    forge.mixinConfigs(
            "locomotion-common.mixins.json",
            "locomotion-forge.mixins.json",
    )
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations {
    compileClasspath.get().extendsFrom(commonBundle)
    runtimeClasspath.get().extendsFrom(commonBundle)
    get("developmentForge").extendsFrom(commonBundle)
}

repositories {
    maven("https://maven.parchmentmc.org/")
    maven("https://maven.minecraftforge.net")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${versionProp("parchment_minecraft_version")}:${versionProp("parchment_mappings_version")}@zip")
    })
    forge("net.minecraftforge:forge:$minecraft-${versionProp("forge_loader")}")

    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionForge")) { isTransitive = false }
}

tasks.processResources {
    applyProperties(project, listOf("META-INF/mods.toml", "${prop("mod.id")}-forge.mixin.json", "pack.mcmeta"))
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    injectAccessWidener = true
    input = tasks.shadowJar.get().archiveFile
    archiveClassifier = null
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    archiveClassifier = "dev"
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

tasks.register<Copy>("buildAndCollect") {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${prop("mod.version")}}/$loader"))
    dependsOn("build")
}
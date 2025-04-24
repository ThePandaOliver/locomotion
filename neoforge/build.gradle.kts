@file:Suppress("UnstableApiUsage")

val loader = prop("loom.platform")!!
val minecraft = prop("version.minecraft")

version = "${prop("mod.version")}+$minecraft-playtesting"
base {
    archivesName.set("${prop("mod.id")}-$loader")
}

architectury {
    platformSetupLoomIde()
    neoForge()
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
    get("developmentNeoForge").extendsFrom(commonBundle)
}

repositories {
    maven("https://maven.parchmentmc.org/")
    maven("https://maven.neoforged.net/releases/")

    maven("https://maven.isxander.dev/releases")
    maven("https://api.modrinth.com/maven")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${versionProp("parchment_minecraft_version")}:${versionProp("parchment_mappings_version")}@zip")
    })
    neoForge("net.neoforged:neoforge:${versionProp("neoforge_loader")}")

    commonBundle(project(rootProject.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(rootProject.path, "transformProductionNeoForge")) { isTransitive = false }

    // Mod implementations
    //runtimeOnly("dev.isxander:yet-another-config-lib:${versionProp("yacl_version")}-neoforge")
}

tasks.processResources {
    applyProperties(project, listOf("META-INF/neoforge.mods.toml", "${prop("mod.id")}-neoforge.mixin.json", "pack.mcmeta"))
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
    val java = JavaVersion.VERSION_21
    targetCompatibility = java
    sourceCompatibility = java
}
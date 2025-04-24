@file:Suppress("UnstableApiUsage")

val loader = prop("loom.platform")!!
val minecraft = prop("version.minecraft")

version = "${prop("mod.version")}+$minecraft-playtesting"
base {
    archivesName.set("${prop("mod.id")}-$loader")
}

architectury {
    platformSetupLoomIde()
    fabric()
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
    get("developmentFabric").extendsFrom(commonBundle)
}

repositories {
    maven("https://maven.parchmentmc.org/")

    maven("https://maven.terraformersmc.com/")
    maven("https://maven.isxander.dev/releases")
    maven("https://api.modrinth.com/maven")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${versionProp("parchment_minecraft_version")}:${versionProp("parchment_mappings_version")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${versionProp("fabric_loader")}")

    commonBundle(project(rootProject.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(rootProject.path, "transformProductionFabric")) { isTransitive = false }

    // Mod implementations
    modImplementation("net.fabricmc.fabric-api:fabric-api:${versionProp("fabric_api_version")}")
    modImplementation("dev.isxander:yet-another-config-lib:${versionProp("yacl_version")}-fabric")
    modImplementation("com.terraformersmc:modmenu:${versionProp("modmenu_version")}")
    modImplementation("maven.modrinth:sodium:${versionProp("sodium_version")}")
    modImplementation("maven.modrinth:iris:${versionProp("iris_version")}")

    // Iris dependencies
    runtimeOnly("org.antlr:antlr4-runtime:4.13.1")
    runtimeOnly("io.github.douira:glsl-transformer:2.0.1")
    runtimeOnly("org.anarres:jcpp:1.4.14")
}

tasks.processResources {
    applyProperties(project, listOf("fabric.mod.json", "${prop("mod.id")}-fabric.mixin.json"))
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
@file:Suppress("UnstableApiUsage")

plugins {
	id("dev.architectury.loom") version "1.10-SNAPSHOT"
	id("architectury-plugin") version "3.4-SNAPSHOT"
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

val minecraft = prop("version.minecraft")

subprojects {
	apply(plugin = "dev.architectury.loom")
	apply(plugin = "architectury-plugin")
	apply(plugin = "com.github.johnrengelman.shadow")

	version = "${prop("mod.version")}+$minecraft-playtesting"
	base {
		archivesName.set("${prop("mod.id")}-common")
	}
}

val supportedLoaders = mutableListOf<String>()
if (findProject(":fabric") != null) supportedLoaders += "fabric"
if (findProject(":neoforge") != null) supportedLoaders += "neoforge"
architectury.common(supportedLoaders)

loom {
	silentMojangMappingsLicense()
	accessWidenerPath = rootProject.file("src/main/resources/${prop("mod.id")}.accesswidener")

	decompilers {
		get("vineflower").apply { // Adds names to lambdas - useful for mixins
			options.put("mark-corresponding-synthetics", "1")
		}
	}
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

	// Mod implementations
	modCompileOnly("dev.isxander:yet-another-config-lib:${versionProp("yacl_version")}-fabric")
}

tasks.processResources {
	applyProperties(project, listOf("${prop("mod.id")}-common.mixin.json"))
}

java {
	withSourcesJar()
	val java = JavaVersion.VERSION_21
	targetCompatibility = java
	sourceCompatibility = java
}
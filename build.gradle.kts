plugins {
	id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
}

version = providers.gradleProperty("mod_version").get()
group = "dev.tonimatas"

repositories {
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven")
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
	implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")

	implementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")

	// Roguelike Dungeons
	implementation("maven.modrinth:MFpvbNGE:FIYUpGUY")
	// Lootr
	implementation("maven.modrinth:EltpO5cN:1viskfeN")
}

tasks.processResources {
	val version = version
	inputs.property("version", version)
	inputs.property("minecraft_version_range", providers.gradleProperty("minecraft_version_range").get())

	filesMatching("fabric.mod.json") {
		expand("version" to version, "minecraft_version_range" to providers.gradleProperty("minecraft_version_range").get())
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 25
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
	val projectName = project.name
	inputs.property("projectName", projectName)

	from("LICENSE") {
		rename { "${it}_$projectName" }
	}
}

import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    // Unique plugins for this module
}

dependencies {
    // Unique dependencies for this module
    api(project(":generic-jar"))
    api(project(":spigot-utils"))

    api("org.apache.httpcomponents.client5:httpclient5:5.4-beta1")
    api("org.apache.httpcomponents.core5:httpcore5:5.3-beta1")

    // Spigot Libraries
    compileOnly(project.property("lowestSpigotDep") as String)
}

tasks {
    shadowJar {
        dependsOn(project(":generic-jar").tasks.shadowJar.get())
        dependsOn(project(":generic-utils").tasks.shadowJar.get())
        archiveBaseName.set("KamiCommon")
    }
    jar {
        // Starting with 1.20.5 Paper we can choose not to reobf the jar, leaving it mojang mapped
        //  we forfeit spigot compatability, but it will natively work on paper
        // The following manifest attribute notifies paper that this jar need not be deobfuscated
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang+yarn"
        }
        archiveBaseName.set("KamiCommon")
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to rootProject.name,
            "version" to rootProject.version,
            "description" to rootProject.description,
            "date" to DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
        filesMatching("**/version.json") {
            expand(props)
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            groupId = rootProject.group.toString()
            artifactId = project.name
            version = rootProject.version.toString()
            project.extensions.getByType<com.github.jengelman.gradle.plugins.shadow.ShadowExtension>().component(this)
        }
    }

    repositories {
        maven {
            credentials {
                username = System.getenv("LUXIOUS_NEXUS_USER")
                password = System.getenv("LUXIOUS_NEXUS_PASS")
            }
            // Select URL based on version (if it's a snapshot or not)
            url = if (project.version.toString().endsWith("-SNAPSHOT")) {
                uri("https://repo.luxiouslabs.net/repository/maven-snapshots/")
            }else {
                uri("https://repo.luxiouslabs.net/repository/maven-releases/")
            }
        }
    }
}
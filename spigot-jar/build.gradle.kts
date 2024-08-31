import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    // Unique plugins for this module
}

var httpclient = "org.apache.httpcomponents.client5:httpclient5:5.4-beta1"
var httpcore = "org.apache.httpcomponents.core5:httpcore5:5.3-beta1"
dependencies {
    // Unique dependencies for this module
    api(project(":generic-jar")); implementation(project(":generic-jar"))
    api(project(":spigot-utils")); compileOnly(project(":spigot-utils"))

    api(httpclient); implementation(httpclient)
    api(httpcore); implementation(httpcore)

    // Spigot Libraries
    compileOnly(project.property("lowestSpigotDep") as String)
}

tasks {
    shadowJar {
        dependsOn(project(":generic-jar").tasks.shadowJar) // Gradle complained...
        dependsOn(project(":generic-utils").tasks.shadowJar) // Gradle complained...
        archiveBaseName.set("KamiCommon")

        dependsOn(project(":spigot-utils").tasks.shadowJar)
        from(project(":spigot-utils").tasks.shadowJar.get().outputs)
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
        create<MavenPublication>("mavenJava") {
            groupId = rootProject.group.toString()
            artifactId = project.name
            version = rootProject.version.toString()
            from(components["java"])
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
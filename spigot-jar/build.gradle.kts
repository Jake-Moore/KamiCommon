import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    // Unique plugins for this module
    id("io.github.goooler.shadow")
    id("maven-publish")
}

var httpclient = "org.apache.httpcomponents.client5:httpclient5:5.4-alpha2"
var httpcore = "org.apache.httpcomponents.core5:httpcore5:5.3-alpha2"
dependencies {
    // Unique dependencies for this module
    shadow(httpclient); shadow(httpcore)
    shadow(files(project(":generic-jar")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )
    shadow(files(project(":spigot-utils")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )

    compileOnly(project.property("lowestSpigotDep") as String)

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}

tasks {
    publish.get().dependsOn(build)
    build.get().dependsOn(shadowJar)
    shadowJar {
        archiveClassifier.set("")
        // archiveFileName.set("${rootProject.name}-${project.version}.jar") // messes up publishing
        configurations = listOf(project.configurations.shadow.get())

        relocate("org.apache.hc.client5", "com.kamikazejam.kamicommon.hc.client5")
        relocate("org.apache.hc.core5", "com.kamikazejam.kamicommon.hc.core5")

        from(project(":generic-jar").tasks.shadowJar.get().outputs)
        from(project(":spigot-utils").tasks.shadowJar.get().outputs)

    }
    jar {
        // Starting with 1.20.5 Paper we can choose not to reobf the jar, leaving it mojang mapped
        //  we forfeit spigot compatability, but it will natively work on paper
        // The following manifest attribute notifies paper that this jar need not be deobfuscated
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang+yarn"
        }
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
            url = uri("https://nexus.luxiouslabs.net/public")
            credentials {
                username = System.getenv("LUXIOUS_NEXUS_USER")
                password = System.getenv("LUXIOUS_NEXUS_PASS")
            }
        }
    }
}

tasks.processResources {
    val props = mapOf("version" to rootProject.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}


// not required, but useful to see what's in the jar
tasks.register<Copy>("unpackShadow") {
    dependsOn(tasks.shadowJar)
    from(zipTree(layout.buildDirectory.dir("libs").map { it.file(tasks.shadowJar.get().archiveFileName) }))
    into(layout.buildDirectory.dir("unpacked-shadow"))
}
tasks.getByName("build").finalizedBy(tasks.getByName("unpackShadow"))
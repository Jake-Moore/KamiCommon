import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    // Unique plugins for this module
    id("com.gradleup.shadow")
}

dependencies {
    // Unique dependencies for this module
    implementation(project(":shared-jar"))
    implementation(project(":spigot-utils"))

    implementation("org.apache.httpcomponents.client5:httpclient5:5.4-beta1")
    implementation("org.apache.httpcomponents.core5:httpcore5:5.3-beta1")

    // Spigot Libraries
    compileOnly(project.property("lowestSpigotDep") as String)
}

tasks {
    publish.get().dependsOn(build.get())
    build.get().dependsOn(shadowJar)

    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("KamiCommon")

        // From particlenativeapi
        exclude("LICENSE*", "META-INF/LICENSE*")
        exclude("License*", "META-INF/License*")

        // KamiCommonNMS
        relocate("com.cryptomorin.xseries", "com.kamikazejam.kamicommon.xseries")
        relocate("com.github.fierioziy.particlenativeapi", "com.kamikazejam.kamicommon.particleapi")
        relocate("de.tr7zw.changeme.nbtapi", "com.kamikazejam.kamicommon.nbtapi")
        // shared-jar
        relocate("com.zaxxer.hikari", "com.kamikazejam.kamicommon.hikari")
        relocate("org.apache.commons.pool2", "com.kamikazejam.kamicommon.commons.pool2")
        relocate("com.mysql", "com.kamikazejam.kamicommon.mysql")
        relocate("com.rabbitmq", "com.kamikazejam.kamicommon.rabbitmq")
        relocate("org.slf4j", "com.kamikazejam.kamicommon.slf4j")
        relocate("io.netty", "com.kamikazejam.kamicommon.netty")
        relocate("reactor", "com.kamikazejam.kamicommon.reactor")
        relocate("org.reactivestreams", "com.kamikazejam.kamicommon.reactivestreams")
        relocate("io.lettuce.core", "com.kamikazejam.kamicommon.lettuce.core")
        // standalone-utils
        relocate("org.yaml.snakeyaml", "com.kamikazejam.kamicommon.snakeyaml")
        relocate("org.json", "com.kamikazejam.kamicommon.json")
        // standalone-jar
        relocate("com.google.gson", "com.kamikazejam.kamicommon.gson")
        relocate("com.google.errorprone", "com.kamikazejam.kamicommon.errorprone")
        // spigot-utils
        relocate("org.apache.commons.text", "com.kamikazejam.kamicommon.text")
        relocate("org.apache.commons.lang3", "com.kamikazejam.kamicommon.lang3")
        // spigot-jar
        relocate("org.apache.hc.client5", "com.kamikazejam.kamicommon.hc.client5")
        relocate("org.apache.hc.core5", "com.kamikazejam.kamicommon.hc.core5")
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

gradle.projectsEvaluated {
    tasks.getByName("publishShadowPublicationToMavenRepository").dependsOn(tasks.jar)
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

tasks.register<Copy>("unpackShadow") {
    dependsOn(tasks.shadowJar)
    from(zipTree(layout.buildDirectory.dir("libs").map { it.file(tasks.shadowJar.get().archiveFileName) }))
    into(layout.buildDirectory.dir("unpacked-shadow"))
}
tasks.getByName("build").finalizedBy(tasks.getByName("unpackShadow"))
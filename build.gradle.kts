import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

@Suppress("PropertyName")
var VERSION = "3.5.0.7-SNAPSHOT"

plugins { // needed for the subprojects section to work
    id("java")
    id("java-library")
    id("maven-publish")
    id("io.github.goooler.shadow") version "8.1.8"
    id("io.papermc.paperweight.userdev") version "1.7.2" apply false
}

ext {
    // reduced is just a re-zipped version of the original, without some conflicting libraries
    //  gson, org.json, com.yaml.snakeyaml
    set("lowestSpigotDep", "net.techcable.tacospigot:server:1.8.8-R0.2-REDUCED")    // luxious nexus (public)
}

allprojects {
    group = "com.kamikazejam.kamicommon"
    version = VERSION
    description = "KamikazeJAM's common library for Spigot and Standalone projects."

    repositories {
        mavenLocal()
        mavenCentral()
        // PaperMC & SpigotMC
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        // Luxious Nexus
        maven("https://repo.luxiouslabs.net/repository/maven-public/")
        // Spigot Plugin Repos
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://mvn.lumine.io/repository/maven-public/") {
            content {
                includeGroup("io.lumine")
                excludeGroup("org.jetbrains")
            }
        }
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        // Misc repos
        maven("https://jitpack.io")
        gradlePluginPortal()
    }

    // We want UTF-8 for everything
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    // The spigot jars have versions with -SNAPSHOT which get downloaded every day
    //  This is pointless, so lets tell gradle to wait a year
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(365, "days")
        resolutionStrategy.cacheDynamicVersionsFor(365, "days")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "io.github.goooler.shadow")

    // Provision Java 17 all subprojects (new modules have version 21 configured)
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    dependencies {
        // Lombok
        compileOnly("org.projectlombok:lombok:1.18.34")
        annotationProcessor("org.projectlombok:lombok:1.18.34")
        testImplementation("org.projectlombok:lombok:1.18.34")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

        // IntelliJ annotations
        compileOnly("org.jetbrains:annotations:24.1.0")
    }

    // Configure shadowJar, including all relocations that are needed anywhere
    tasks.withType(ShadowJar::class.java).configureEach {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.getByName("shadow"))

        // List of relocation exclusions:
        // - Jackson libraries

        // --------------------------------------------------- //
        // *** Relocations (will apply to ALL subprojects) *** //
        // --------------------------------------------------- //
        // standalone-utils
        relocate("org.yaml.snakeyaml", "com.kamikazejam.kamicommon.snakeyaml")
        relocate("org.json", "com.kamikazejam.kamicommon.json")
        // generic-jar
        relocate("com.zaxxer.hikari", "com.kamikazejam.kamicommon.hikari")
        relocate("org.apache.commons.pool2", "com.kamikazejam.kamicommon.commons.pool2")
        relocate("com.mysql", "com.kamikazejam.kamicommon.mysql")
        relocate("com.rabbitmq", "com.kamikazejam.kamicommon.rabbitmq")
        relocate("org.slf4j", "com.kamikazejam.kamicommon.slf4j")
        relocate("io.netty", "com.kamikazejam.kamicommon.netty")
        relocate("reactor", "com.kamikazejam.kamicommon.reactor")
        relocate("org.reactivestreams", "com.kamikazejam.kamicommon.reactivestreams")
        relocate("io.lettuce.core", "com.kamikazejam.kamicommon.lettuce.core")
        // standalone-jar
        relocate("com.google.gson", "com.kamikazejam.kamicommon.gson")
        relocate("com.google.errorprone", "com.kamikazejam.kamicommon.errorprone")
        // spigot-nms:api
        relocate("com.cryptomorin.xseries", "com.kamikazejam.kamicommon.xseries")
        relocate("com.github.fierioziy.particlenativeapi", "com.kamikazejam.kamicommon.particleapi")
        relocate("de.tr7zw.changeme.nbtapi", "com.kamikazejam.kamicommon.nbt.nbtapi")
    }
    tasks.publish.get().dependsOn(tasks.build)
    tasks.build.get().dependsOn(tasks.withType(ShadowJar::class.java))
}

// Disable root project build
tasks.jar.get().enabled = false

tasks {
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

plugins {
    // Unique plugins for this module
    id("io.github.goooler.shadow")
    id("maven-publish")
}

dependencies {
    // Submodules are compile-only since they are included in the shadowJar task configuration: from(...)
    compileOnly(files(project(":spigot-nms") // which contains standalone-utils
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )

    shadow("com.google.code.gson:gson:2.11.0")
    shadow("org.apache.commons:commons-text:1.12.0") // primarily for LevenshteinDistance
    shadow("de.tr7zw:item-nbt-api:2.13.1")

    compileOnly(project.property("lowestSpigotDep") as String)
    compileOnly("me.clip:placeholderapi:2.11.6") // TODO soft depend

    // Spigot Libs (soft-depend)
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.3-beta-14")
    compileOnly("net.citizensnpcs:citizens-main:2.0.35-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("io.lumine:Mythic-Dist:5.6.2")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.19")
    // Combat Integrations
    compileOnly("net.minelink:CombatTagPlus:1.3.1")
    compileOnly("me.nochance:PvPManager:3.15.9")
    compileOnly("nl.marido.deluxecombat:DeluxeCombat:1.40.5")
}

tasks {
    publish.get().dependsOn(build)
    build.get().dependsOn(shadowJar)
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        relocate("com.google.gson", "com.kamikazejam.kamicommon.gson")
        relocate("org.apache.commons.text", "com.kamikazejam.kamicommon.text")
        relocate("de.tr7zw.changeme.nbtapi", "com.kamikazejam.kamicommon.nbt.nbtapi")
        relocate("org.apache.commons.lang3", "com.kamikazejam.kamicommon.lang3")
        relocate("com.google.errorprone", "com.kamikazejam.kamicommon.errorprone")

        from(project(":spigot-nms").tasks.shadowJar.get().outputs)
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

// ONLY REQUIRED IF: you are using Solution 2 with the modified dependency
tasks.register<Copy>("unpackShadow") {
    dependsOn(tasks.shadowJar)
    from(zipTree(layout.buildDirectory.dir("libs").map { it.file(tasks.shadowJar.get().archiveFileName) }))
    into(layout.buildDirectory.dir("unpacked-shadow"))
}
tasks.getByName("build").finalizedBy(tasks.getByName("unpackShadow"))
plugins {
    // Unique plugins for this module
}

dependencies {
    // Add NMS library from KamiCommonNMS
    api("com.kamikazejam.kamicommon:spigot-nms:1.0.9")
    api(project(":standalone-utils"))
    api(project(":shared-utils"))

    api("com.google.code.gson:gson:2.11.0")
    api("org.apache.commons:commons-text:1.13.0") // primarily for LevenshteinDistance

    compileOnly(project.property("lowestSpigotDep") as String)

    // Spigot Libs (soft-depend)
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.3-beta-14")
    compileOnly("net.citizensnpcs:citizens-main:2.0.37-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("io.lumine:Mythic-Dist:5.7.2")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.19")
    // Combat Integrations
    compileOnly("net.minelink:CombatTagPlus:1.3.1")
    compileOnly("me.nochance:PvPManager:3.15.9")
    compileOnly("nl.marido.deluxecombat:DeluxeCombat:1.40.5")
}

tasks {
    publish.get().dependsOn(build.get())
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
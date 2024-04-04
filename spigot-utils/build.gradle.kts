plugins {
    // Unique plugins for this module
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

var json = "org.json:json:20240303"
var gson = "com.google.code.gson:gson:2.10.1"
var commonsText = "org.apache.commons:commons-text:1.11.0"
dependencies {
    api(project(":spigot-nms")); shadow(project(":spigot-nms")) // which contains standalone-utils
    api(json); shadow(json)
    api(gson); shadow(gson)
    api(commonsText); shadow(commonsText) // primarily for LevenshteinDistance

    compileOnly(project.property("lowestSpigotDep") as String)
    compileOnly("me.clip:placeholderapi:2.11.5") // TODO soft depend

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        dependencies {
            include(dependency(json))
            include(dependency(gson))
            include(dependency(commonsText))
        }
        relocate("com.google.gson", "com.kamikazejam.kamicommon.gson")
        relocate("org.json", "com.kamikazejam.kamicommon.json")
        relocate("org.apache.commons.text", "com.kamikazejam.kamicommon.text")

        from(project(":spigot-nms").tasks.shadowJar.get().outputs)
    }
    test {
        useJUnitPlatform()
    }
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
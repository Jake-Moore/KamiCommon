plugins {
    // Unique plugins for this module
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

dependencies {
    api(project(":generic-jar")); shadow(project(":generic-jar"))
    api(project(":standalone-utils")); shadow(project(":standalone-utils"))
}
tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("")
        from(project(":generic-jar").tasks.shadowJar.get().outputs)
        from(project(":standalone-utils").tasks.shadowJar.get().outputs)

        configurations = listOf(project.configurations.shadow.get())
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
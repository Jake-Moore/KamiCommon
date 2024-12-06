plugins {
    // Unique plugins for this module
}

dependencies {
    // Unique dependencies for this module
}

tasks {
    publish.get().dependsOn(build.get())
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
            // Only allow valid SemVer release versions for the releases repository
            url = if (project.version.toString().matches(Regex("^\\d+\\.\\d+\\.\\d+$"))) {
                uri("https://repo.luxiouslabs.net/repository/maven-releases/")
            } else {
                uri("https://repo.luxiouslabs.net/repository/maven-snapshots/")
            }
        }
    }
}
plugins {
    // Unique plugins for this module
}

dependencies {
    // Both shared-jar and standalone-utils inherit from shared-utils
    // We should exclude one of them to avoid duplicate classes
    api(project(":shared-jar"))
    api(project(":standalone-utils")) {
        exclude(group = "com.kamikazejam.kamicommon", module = "shared-utils")
    }

    // org.json (standalone-utils) and google gson needed for for jedis (in :shared-jar) to work properly
    api("com.google.code.gson:gson:2.11.0")
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
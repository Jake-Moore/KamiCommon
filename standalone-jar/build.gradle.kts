plugins {
    // Unique plugins for this module
}

dependencies {
    // Both shared-jar and standalone-utils inherit from shared-utils
    // We should exclude one of them to avoid duplicate classes
    api(project(":shared-jar"))
    api(project(":standalone-utils")) {
        // Must exclude one copy of shared-utils, since both shared-jar and standalone-utils include it
        exclude(group = "com.kamikazejam.kamicommon", module = "shared-utils")
    }
}

tasks {
    publish.get().dependsOn(build.get())
}

@Suppress("UNCHECKED_CAST")
val getPublishingVersion = rootProject.extra["getPublishingVersion"] as () -> Pair<String, Boolean>?

publishing {
    val versionData = getPublishingVersion() ?: run {
        logger.warn("⚠️ Skipping publication: VERSION '${rootProject.version}' is not valid.")
        return@publishing
    }
    val resolvedVersion = versionData.first
    val isSnapshot = versionData.second

    publications {
        create<MavenPublication>("shadow") {
            groupId = rootProject.group.toString()
            artifactId = project.name
            version = resolvedVersion
            from(components["java"])
        }
    }

    repositories {
        maven {
            credentials {
                username = System.getenv("LUXIOUS_NEXUS_USER")
                password = System.getenv("LUXIOUS_NEXUS_PASS")
            }
            // getPublishingVersion will append "-SNAPSHOT" if the version is not a SemVer release version
            url = if (!isSnapshot) {
                uri("https://repo.luxiouslabs.net/repository/maven-releases/")
            } else {
                uri("https://repo.luxiouslabs.net/repository/maven-snapshots/")
            }
        }
    }
}
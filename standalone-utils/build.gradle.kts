plugins {
    // Unique plugins for this module
}

repositories {
    mavenCentral()
}

var snakeYaml = "org.yaml:snakeyaml:2.3"
var json = "org.json:json:20250107"
dependencies {
    api(project(":shared-utils"))
    // Unique dependencies for this module
    api(snakeYaml)
    api(json)

    // Testing Dependencies
    testImplementation(snakeYaml)
    testImplementation(json)
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
            url = if (!isSnapshot) {
                uri("https://repo.luxiouslabs.net/repository/maven-releases/")
            } else {
                uri("https://repo.luxiouslabs.net/repository/maven-snapshots/")
            }
        }
    }
}
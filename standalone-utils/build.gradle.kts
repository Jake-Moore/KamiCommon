plugins {
    // Unique plugins for this module
}

var snakeYaml = "org.yaml:snakeyaml:2.3"
var json = "org.json:json:20240303"
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
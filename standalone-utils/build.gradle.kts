plugins {
    // Unique plugins for this module
}

var snakeYaml = "org.yaml:snakeyaml:2.2"
var json = "org.json:json:20240303"
dependencies {
    api(project(":generic-utils")); implementation(project(":generic-utils"))
    // Unique dependencies for this module
    api(snakeYaml); implementation(snakeYaml)
    api(json); implementation(json)

    // Testing Dependencies
    testImplementation(snakeYaml)
    testImplementation(json)
    testImplementation("org.jetbrains:annotations:24.1.0")
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

tasks {
    shadowJar {
        dependsOn(project(":generic-utils").tasks.shadowJar) // Gradle complained...
    }
    test {
        dependsOn(project(":generic-utils").tasks.shadowJar) // Gradle complained...
    }
}

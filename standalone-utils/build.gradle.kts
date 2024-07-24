plugins {
    // Unique plugins for this module
    id("io.github.goooler.shadow")
    id("maven-publish")
}

var snakeYaml = "org.yaml:snakeyaml:2.2"
var json = "org.json:json:20240303"
dependencies {
    shadow(project(":generic-utils"))
    // Unique dependencies for this module
    shadow(snakeYaml); testImplementation(snakeYaml)
    shadow(json); testImplementation(json)
    testImplementation("org.jetbrains:annotations:24.1.0")
}

tasks {
    publish.get().dependsOn(build)
    build.get().dependsOn(shadowJar)
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        relocate("org.yaml.snakeyaml", "com.kamikazejam.kamicommon.snakeyaml")
        relocate("org.json", "com.kamikazejam.kamicommon.json")
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
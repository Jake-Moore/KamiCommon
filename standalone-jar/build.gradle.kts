plugins {
    // Unique plugins for this module
    id("io.github.goooler.shadow")
    id("maven-publish")
}

dependencies {
    shadow(files(project(":generic-jar")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )
    shadow(files(project(":standalone-utils")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )

    // org.json (standalone-utils) and google gson needed for for jedis (in :generic-jar) to work properly
    shadow("com.google.code.gson:gson:2.11.0")
}
tasks {
    publish.get().dependsOn(build)
    build.get().dependsOn(shadowJar)
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        relocate("com.google.gson", "com.kamikazejam.kamicommon.gson")
        relocate("org.json", "com.kamikazejam.kamicommon.json")
        relocate("com.google.errorprone", "com.kamikazejam.kamicommon.errorprone")
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


// not required, but useful to see jar contents
tasks.register<Copy>("unpackShadow") {
    dependsOn(tasks.shadowJar)
    from(zipTree(layout.buildDirectory.dir("libs").map { it.file(tasks.shadowJar.get().archiveFileName) }))
    into(layout.buildDirectory.dir("unpacked-shadow"))
}
tasks.getByName("build").finalizedBy(tasks.getByName("unpackShadow"))
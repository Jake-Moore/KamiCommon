plugins {
    // Unique plugins for this module
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

dependencies {
    shadow(files(project(":generic-jar")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )
    shadow(files(project(":standalone-utils")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )

    // Both needed for jedis in :generic-jar to work properly
    shadow("org.json:json:20240303")
    shadow("com.google.code.gson:gson:2.10.1")

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}
tasks {
    publish.get().dependsOn(build)
    build.get().dependsOn(shadowJar)
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        relocate("com.google.gson", "com.kamikazejam.kamicommon.gson")
        relocate("org.json", "com.kamikazejam.kamicommon.json")
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


// not required, but useful to see jar contents
tasks.register<Copy>("unpackShadow") {
    dependsOn(tasks.shadowJar)
    from(zipTree(layout.buildDirectory.dir("libs").map { it.file(tasks.shadowJar.get().archiveFileName) }))
    into(layout.buildDirectory.dir("unpacked-shadow"))
}
tasks.getByName("build").finalizedBy(tasks.getByName("unpackShadow"))
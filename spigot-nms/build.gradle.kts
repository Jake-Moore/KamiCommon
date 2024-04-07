plugins {
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

dependencies {
    // Unique dependencies for this module
    implementation(files(project(":spigot-nms:api")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )
    implementation(project(":spigot-nms:v1_8_R1"))
    implementation(project(":spigot-nms:v1_8_R2"))
    implementation(project(":spigot-nms:v1_8_R3"))
    implementation(project(":spigot-nms:v1_9_R1"))
    implementation(project(":spigot-nms:v1_9_R2"))
    implementation(project(":spigot-nms:v1_10_R1"))
    implementation(project(":spigot-nms:v1_11_R1"))
    implementation(project(":spigot-nms:v1_12_R1"))
    implementation(project(":spigot-nms:v1_13_R1"))
    implementation(project(":spigot-nms:v1_13_R2"))
    implementation(project(":spigot-nms:v1_14_R1"))
    implementation(project(":spigot-nms:v1_15_R1"))
    implementation(project(":spigot-nms:v1_16_R1"))
    implementation(project(":spigot-nms:v1_16_R2"))
    implementation(project(":spigot-nms:v1_16_R3"))
    shadow(project(":spigot-nms:v1_17_R1"))
    shadow(project(":spigot-nms:v1_18_R1"))
    shadow(project(":spigot-nms:v1_18_R2"))
    shadow(project(":spigot-nms:v1_19_R1"))
    shadow(project(":spigot-nms:v1_19_R2"))
    shadow(project(":spigot-nms:v1_19_R3"))
    shadow(project(":spigot-nms:v1_20_R1"))
    shadow(project(":spigot-nms:v1_20_R2"))
    shadow(project(":spigot-nms:v1_20_R3"))

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)

    compileOnly(project.property("lowestSpigotDep") as String)
}

tasks {
    build.get().dependsOn(shadowJar)
    shadowJar {
        archiveClassifier.set("")
        from(project(":spigot-nms:v1_17_R1").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_18_R1").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_18_R2").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_19_R1").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_19_R2").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_19_R3").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_20_R1").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_20_R2").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_20_R3").tasks.getByName("reobfJar").outputs)
    }
    test {
        useJUnitPlatform()
    }
}
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

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

// ONLY REQUIRED IF: you are using Solution 2 with the modified dependency
tasks.register<Copy>("unpackShadow") {
    dependsOn(tasks.shadowJar)
    from(zipTree(layout.buildDirectory.dir("libs").map { it.file(tasks.shadowJar.get().archiveFileName) }))
    into(layout.buildDirectory.dir("unpacked-shadow"))
}
tasks.getByName("build").finalizedBy(tasks.getByName("unpackShadow"))
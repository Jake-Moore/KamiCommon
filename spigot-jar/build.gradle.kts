plugins {
    // Unique plugins for this module
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

var httpclient = "org.apache.httpcomponents.client5:httpclient5:5.3.1"
dependencies {
    // Unique dependencies for this module
    api(httpclient); shadow(httpclient)
    api(project(":generic-jar")); shadow(project(":generic-jar"))
    api(project(":spigot-utils")); shadow(project(":spigot-utils"))

    compileOnly(project.property("lowestSpigotDep") as String)

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        dependencies {
            include(dependency(httpclient))
        }
        relocate("org.apache.hc.client5", "com.kamikazejam.kamicommon.hc.client5")

        from(project(":generic-jar").tasks.shadowJar.get().outputs)
        from(project(":spigot-utils").tasks.shadowJar.get().outputs)

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

tasks.processResources {
    val props = mapOf("version" to rootProject.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    // Unique plugins for this module
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

var yaml = "org.yaml:snakeyaml:2.2"
dependencies {
    // Unique dependencies for this module
    api(yaml); shadow(yaml)

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    publish {
        dependsOn(build)
    }
    shadowJar {
        archiveClassifier.set("")
        dependencies {
            include(dependency(yaml))
        }

        relocate("org.yaml.snakeyaml", "com.kamikazejam.kamicommon.snakeyaml")
    }
    test {
        useJUnitPlatform()
    }
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = rootProject.group.toString() + "." + rootProject.name
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
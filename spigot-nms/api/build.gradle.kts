plugins {
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

var xseries = "com.github.cryptomorin:XSeries:9.9.0"
dependencies {
    api(project(":standalone-utils")); shadow(project(":standalone-utils"))
    api(xseries); shadow(xseries)

    compileOnly(project.property("lowestSpigotDep") as String)

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        dependencies {
            include(dependency(xseries))
        }
        relocate("com.cryptomorin.xseries", "com.kamikazejam.kamicommon.xseries")

        from(project(":standalone-utils").tasks.shadowJar.get().outputs)
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
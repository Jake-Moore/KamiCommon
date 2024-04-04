plugins {
    // Unique plugins for this module
    id("com.github.johnrengelman.shadow")
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
    shadowJar {
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
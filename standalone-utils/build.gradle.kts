plugins {
    // Unique plugins for this module
}

repositories {
    // Unique repos for this module
}

dependencies {
    // Unique dependencies for this module
    api("org.yaml:snakeyaml:2.2")

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}

tasks.test {
    useJUnitPlatform()
}
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8
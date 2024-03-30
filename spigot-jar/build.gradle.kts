plugins {
    // Unique plugins for this project
}

dependencies {
    // Unique dependencies for this project
    api("org.apache.httpcomponents.client5:httpclient5:5.3.1")

    api(project(":generic-jar"))
    api(project(":spigot-nms"))
    api(project(":spigot-utils"))
    compileOnly(project.property("lowestSpigotDep") as String)

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}

tasks.test {
    useJUnitPlatform()
}
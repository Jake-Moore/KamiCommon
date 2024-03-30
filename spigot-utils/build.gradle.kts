plugins {
    // Unique plugins for this module
}

repositories {
    // Unique repos for this module
}

dependencies {
    api(project(":spigot-nms")) // which contains standalone-utils
    api("com.google.code.gson:gson:2.10.1")
    api("org.json:json:20231013")

    compileOnly(project.property("lowestSpigotDep") as String)
    compileOnly("me.clip:placeholderapi:2.11.5") // TODO soft depend

    implementation("org.apache.commons:commons-text:1.11.0") // primarily for LevenshteinDistance

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}

tasks.test {
    useJUnitPlatform()
}
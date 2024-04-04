plugins {
    // Unique plugins for this module
    id("com.github.johnrengelman.shadow")
}

repositories {
    // Unique repos for this module
}

dependencies {
    api(project(":spigot-nms")) // which contains standalone-utils
    api("org.json:json:20231013")
    api("com.google.code.gson:gson:2.10.1")

    api("org.apache.commons:commons-text:1.11.0") // primarily for LevenshteinDistance

    compileOnly(project.property("lowestSpigotDep") as String)
    compileOnly("me.clip:placeholderapi:2.11.5") // TODO soft depend

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}

tasks.shadowJar {
    // TODO do I need to include() ?
    relocate("com.google.code.gson", "com.kamikazejam.kamicommon.gson")
    relocate("org.json", "com.kamikazejam.kamicommon.json")
    relocate("org.apache.commons.text", "com.kamikazejam.kamicommon.text")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    // Unique dependencies for this project
    api(project(":spigot-nms:api"))
    api(project(":spigot-nms:v1_8_R3"))
    api(project(":spigot-nms:v1_16_R1"))
    api(project(":spigot-nms:v1_17_R1"))

    compileOnly(project.property("lowestSpigotDep") as String)
}

tasks.test {
    useJUnitPlatform()
}
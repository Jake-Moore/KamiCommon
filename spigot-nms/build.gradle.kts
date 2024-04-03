
dependencies {
    // Unique dependencies for this module
    api(project(":spigot-nms:api"))
    api(project(":spigot-nms:v1_8_R1"))
    api(project(":spigot-nms:v1_8_R2"))
    api(project(":spigot-nms:v1_8_R3"))
    api(project(":spigot-nms:v1_9_R1"))
    api(project(":spigot-nms:v1_9_R2"))
    api(project(":spigot-nms:v1_10_R1"))
    api(project(":spigot-nms:v1_11_R1"))
    api(project(":spigot-nms:v1_12_R1"))
    api(project(":spigot-nms:v1_13_R1"))
    api(project(":spigot-nms:v1_13_R2"))
    api(project(":spigot-nms:v1_14_R1"))
    api(project(":spigot-nms:v1_15_R1"))
    api(project(":spigot-nms:v1_16_R1"))
    api(project(":spigot-nms:v1_16_R2"))
    api(project(":spigot-nms:v1_16_R3"))
    api(project(":spigot-nms:v1_17_R1"))
    api(project(":spigot-nms:v1_18_R1"))
    api(project(":spigot-nms:v1_18_R2"))
    api(project(":spigot-nms:v1_19_R1"))
    api(project(":spigot-nms:v1_19_R2"))
    api(project(":spigot-nms:v1_19_R3"))
    api(project(":spigot-nms:v1_20_R1"))
    api(project(":spigot-nms:v1_20_R2"))
    api(project(":spigot-nms:v1_20_R3"))

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)

    compileOnly(project.property("lowestSpigotDep") as String)
}

tasks.test {
    useJUnitPlatform()
}
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17
plugins {
    // Unique plugins for this module
    id("com.github.johnrengelman.shadow")
}

dependencies {
    // Unique dependencies for this module
    api("org.apache.httpcomponents.client5:httpclient5:5.3.1")

    api(project(":generic-jar"))
    api(project(":spigot-nms"))
    api(project(":spigot-utils"))
    compileOnly(project.property("lowestSpigotDep") as String)

    // Spigot Libs (soft-depend)
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.2-beta-r3")
    compileOnly("net.citizensnpcs:citizens-main:2.0.33-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("io.lumine:Mythic-Dist:5.5.1")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.18-3")

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}

tasks.shadowJar {


}

tasks.test {
    useJUnitPlatform()
}
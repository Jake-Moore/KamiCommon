dependencies {
    // Unique dependencies for this project
    compileOnly("net.techcable.tacospigot:server:1.8.8-R0.2-SNAPSHOT")
    compileOnly(project(":spigot-nms:api"))
}

tasks.test {
    useJUnitPlatform()
}
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8
dependencies {
    // Unique dependencies for this module
    compileOnly("org.spigotmc:spigot-server:1.16.5-R0.1-SNAPSHOT")
    compileOnly(project(":spigot-nms:api"))
    compileOnly(project(":spigot-nms:v1_13_R1"))
}

tasks.test {
    useJUnitPlatform()
}
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8
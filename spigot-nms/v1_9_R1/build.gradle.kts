dependencies {
    // Unique dependencies for this module
    compileOnly("org.spigotmc:spigot-server:1.9.2-R0.1-SNAPSHOT")
    compileOnly(project(":spigot-nms:api"))
}

tasks.test {
    useJUnitPlatform()
}
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8
dependencies {
    // Unique dependencies for this project
    compileOnly("org.spigotmc:spigot-server:1.8.3-R0.1-SNAPSHOT")
    compileOnly(project(":spigot-nms:api"))
}

tasks.test {
    useJUnitPlatform()
}
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8
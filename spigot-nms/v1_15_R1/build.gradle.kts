dependencies {
    // Unique dependencies for this module
    compileOnly("org.spigotmc:spigot-server:1.15.2-R0.1-SNAPSHOT")
    compileOnly(files(project(":spigot-nms:api")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )
    compileOnly(project(":spigot-nms:v1_13_R1"))
}

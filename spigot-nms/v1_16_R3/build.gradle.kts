dependencies {
    // Unique dependencies for this module
    compileOnly("org.spigotmc:spigot-server:1.16.5-R0.1-SNAPSHOT")
    compileOnly(files(project(":spigot-nms:api")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )
    compileOnly(project(":spigot-nms:v1_13_R1"))
}

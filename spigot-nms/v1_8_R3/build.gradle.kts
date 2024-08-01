dependencies {
    // Unique dependencies for this module
    compileOnly("net.techcable.tacospigot:server:1.8.8-R0.2-REDUCED")
    compileOnly(files(project(":spigot-nms:api")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )
}

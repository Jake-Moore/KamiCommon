repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    // Unique dependencies for this module
    compileOnly("org.spigotmc:spigot-server:1.8-R0.1")
    compileOnly(files(project(":spigot-nms:api")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )

    // WorldEdit v7 / World Guard v7
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    compileOnly("com.sk89q.worldedit:fawe:2.4.5")
}

repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    // Unique dependencies for this module
    compileOnly("org.spigotmc:spigot-server:1.8-R0.1")
    compileOnly(project(":spigot-nms:api"))

    // WorldEdit v6 / World Guard v6
    compileOnly("com.sk89q:worldguard:6.1.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:bukkit:6.1.9")
}

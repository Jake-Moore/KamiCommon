plugins {
    // Unique plugins for this module
    id("io.papermc.paperweight.userdev")                                 // 1. add the Paperweight plugin
}

dependencies {
    // Unique dependencies for this module
    paperweight.paperDevBundle("1.19.3-R0.1-SNAPSHOT")           // 2. add the dev bundle (contains all apis)
    compileOnly(project(":spigot-nms:api"))
    compileOnly(project(":spigot-nms:v1_13_R1"))
    compileOnly(project(":spigot-nms:v1_14_R1"))
}


tasks {                                                                 // 3. configure tasks (like reObf automatically)
    assemble {
        dependsOn(reobfJar)
    }

    reobfJar {
        outputJar.set(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
    }
}
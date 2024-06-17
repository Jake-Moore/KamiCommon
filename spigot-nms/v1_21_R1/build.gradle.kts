plugins {
    // Unique plugins for this module
    id("io.papermc.paperweight.userdev")                                 // 1. add the Paperweight plugin
}

dependencies {
    // Unique dependencies for this module
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")                    // 2. add the dev bundle (contains all apis)
    // Confirmed working for 1.21

    compileOnly(files(project(":spigot-nms:api")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )
    compileOnly(project(":spigot-nms:v1_13_R1"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))           // 3. Need Java 21 for 1.20.5+
}

// Starting with 1.20.5 Paper we can choose not to reobf the jar, leaving it mojang mapped
//  we forfeit spigot compatability, but it will natively work on paper
paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
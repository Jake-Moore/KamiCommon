plugins {
    // Unique plugins for this module
    id("io.papermc.paperweight.userdev")                                 // 1. add the Paperweight plugin
}

dependencies {
    // Unique dependencies for this module
    paperweight.paperDevBundle("1.17.1-R0.1-SNAPSHOT")           // 2. add the dev bundle (contains all apis)
    compileOnly(project(":spigot-nms:api"))
    compileOnly(project(":spigot-nms:v1_13_R1"))
}

java {                                              // 3. provision Java 17
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {                                             // 4. configure tasks (like reObf automatically)
    assemble {
        dependsOn(reobfJar)
    }

    reobfJar {
        outputJar.set(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
    }
}

tasks.test {
    useJUnitPlatform()
}
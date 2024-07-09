plugins {
    id("io.github.goooler.shadow")
}

dependencies {
    // Unique dependencies for this module
    compileOnly(files(project(":spigot-nms:api")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )
    shadow(project(":spigot-nms:v1_8_R1"))
    shadow(project(":spigot-nms:v1_8_R2"))
    shadow(project(":spigot-nms:v1_8_R3"))
    shadow(project(":spigot-nms:v1_9_R1"))
    shadow(project(":spigot-nms:v1_9_R2"))
    shadow(project(":spigot-nms:v1_10_R1"))
    shadow(project(":spigot-nms:v1_11_R1"))
    shadow(project(":spigot-nms:v1_12_R1"))
    shadow(project(":spigot-nms:v1_13_R1"))
    shadow(project(":spigot-nms:v1_13_R2"))
    shadow(project(":spigot-nms:v1_14_R1"))
    shadow(project(":spigot-nms:v1_15_R1"))
    shadow(project(":spigot-nms:v1_16_R1"))
    shadow(project(":spigot-nms:v1_16_R2"))
    shadow(project(":spigot-nms:v1_16_R3"))
    // These are compileOnly so that we can include the reobfJar outputs
    compileOnly(project(":spigot-nms:v1_17_R1"))
    compileOnly(project(":spigot-nms:v1_18_R1"))
    compileOnly(project(":spigot-nms:v1_18_R2"))
    compileOnly(project(":spigot-nms:v1_19_R1"))
    compileOnly(project(":spigot-nms:v1_19_R2"))
    compileOnly(project(":spigot-nms:v1_19_R3"))
    compileOnly(project(":spigot-nms:v1_20_R1"))
    compileOnly(project(":spigot-nms:v1_20_R2"))
    compileOnly(project(":spigot-nms:v1_20_R3"))

    // Starting with 1_20_CB we can opt to not re-obf, so we can shadow again
    shadow(project(":spigot-nms:v1_20_CB"))
    shadow(project(":spigot-nms:v1_21_R1"))

    compileOnly(project.property("lowestSpigotDep") as String)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    build.get().dependsOn(shadowJar)
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        // Add the 1.17 to 1.20R3 reobf outputs
        from(project(":spigot-nms:v1_17_R1").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_18_R1").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_18_R2").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_19_R1").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_19_R2").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_19_R3").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_20_R1").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_20_R2").tasks.getByName("reobfJar").outputs)
        from(project(":spigot-nms:v1_20_R3").tasks.getByName("reobfJar").outputs)

        from(project(":spigot-nms:api").tasks.shadowJar.get().outputs)
    }
}

// ONLY REQUIRED IF: you are using Solution 2 with the modified dependency
tasks.register<Copy>("unpackShadow") {
    dependsOn(tasks.shadowJar)
    from(zipTree(layout.buildDirectory.dir("libs").map { it.file(tasks.shadowJar.get().archiveFileName) }))
    into(layout.buildDirectory.dir("unpacked-shadow"))
}
tasks.getByName("build").finalizedBy(tasks.getByName("unpackShadow"))
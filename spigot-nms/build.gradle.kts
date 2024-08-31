plugins {
    // Unique plugins for this module
}

dependencies {
    // Unique dependencies for this module
    api(project(":spigot-nms:api")); implementation(project(":spigot-nms:api"))

    implementation(project(":spigot-nms:v1_8_R1"))
    implementation(project(":spigot-nms:v1_8_R2"))
    implementation(project(":spigot-nms:v1_8_R3"))
    implementation(project(":spigot-nms:v1_9_R1"))
    implementation(project(":spigot-nms:v1_9_R2"))
    implementation(project(":spigot-nms:v1_10_R1"))
    implementation(project(":spigot-nms:v1_11_R1"))
    implementation(project(":spigot-nms:v1_12_R1"))
    implementation(project(":spigot-nms:v1_13_R1"))
    implementation(project(":spigot-nms:v1_13_R2"))
    implementation(project(":spigot-nms:v1_14_R1"))
    implementation(project(":spigot-nms:v1_15_R1"))
    implementation(project(":spigot-nms:v1_16_R1"))
    implementation(project(":spigot-nms:v1_16_R2"))
    implementation(project(":spigot-nms:v1_16_R3"))
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
    implementation(project(":spigot-nms:v1_20_CB"))
    implementation(project(":spigot-nms:v1_21_R1"))

    implementation(project(":spigot-nms:worlds6"))
    implementation(project(":spigot-nms:worlds7"))

    // So we have access to the Clipboard class
    compileOnly("com.sk89q.worldedit:bukkit:6.1.9")

    compileOnly(project.property("lowestSpigotDep") as String)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    build.get().dependsOn(shadowJar)
    shadowJar {
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
    }
}
plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

repositories {
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Add NMS library from KamiCommonNMS
    api("com.kamikazejam.kamicommon:spigot-nms:1.2.15")
    api(project(":standalone-utils")) // Also includes shared-utils

    api("com.google.code.gson:gson:2.13.1")
    api("org.apache.commons:commons-text:1.14.0") // primarily for LevenshteinDistance

    compileOnly(project.property("serverAPI") as String)

    // Spigot Libs (soft-depend)
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.19")
    // Combat Integrations
    compileOnly("net.minelink:CombatTagPlus:1.3.1")
    compileOnly("me.nochance:PvPManager:3.15.9")
    compileOnly("nl.marido.deluxecombat:DeluxeCombat:1.40.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

// Configure javadoc-publish-convention
configure<Javadoc_publish_convention_gradle.JavadocPublishExtension> {
    // spigot-utils includes shared-utils AND standalone-utils
    exportedProjects = listOf(
        ":spigot-utils",
        ":standalone-utils",
        ":shared-utils",
    )
    moduleName = "spigot-utils"
}

tasks.register("printServerAPI") {
    doFirst {
        println("Using Server API: ${project.property("serverAPI") as String}")
    }
}
tasks.compileJava.get().dependsOn(tasks.named("printServerAPI"))
plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

// A 1.8.8 server loads this in full. KamiPlugin lives here, so it is the module that decides
// whether 1.8.x support is real. See buildSrc/src/main/kotlin/module-floor-convention.gradle.kts.
// See buildSrc/src/main/kotlin/module-floor-convention.gradle.kts for what each setting does.
extra["moduleFloor"] = 8
apply(plugin = "module-floor-convention")


repositories {
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Add NMS library from KamiCommonNMS
    api("com.kamikazejam.kamicommon:spigot-nms:1.2.38")
    api(project(":standalone-utils")) // Also includes shared-utils

    api("com.google.code.gson:gson:2.14.0")
    api("org.apache.commons:commons-text:1.15.0") // primarily for LevenshteinDistance

    compileOnly(project.property("serverAPI") as String)

    // Spigot Libs (soft-depend)
    compileOnly("me.clip:placeholderapi:2.12.3")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.19")
    // Combat Integrations
    compileOnly("net.minelink:CombatTagPlus:1.3.1")
    compileOnly("me.nochance:PvPManager:3.15.9")
    compileOnly("nl.marido.deluxecombat:DeluxeCombat:1.40.5")

    // Testing Dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// The root build disables tests for every project. This module opts back in, matching
//  standalone-utils and shared-jar, because ColoredStringParser decides how EVERY configured
//  menu title, icon name, lore line and subsystem prefix is coloured. It tested for a
//  MiniMessage tag before it tested for a legacy colour code, so any v4 line that contained
//  both - "&7Usage: &f/kit <name>" - rendered its colour codes as literal text.
tasks.named<Test>("test") {
    enabled = true
    useJUnitPlatform()
    testLogging { events("passed", "failed") }
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
apply(from = "$rootDir/gradle/verify-sealed-hierarchies.gradle.kts")

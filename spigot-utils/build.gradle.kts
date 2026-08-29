plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

repositories {
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Add NMS library from KamiCommonNMS
    api("com.kamikazejam.kamicommon:spigot-nms:1.2.20")
    api(project(":standalone-utils")) // Also includes shared-utils

    api("com.google.code.gson:gson:2.13.2")
    api("org.apache.commons:commons-text:1.14.0") // primarily for LevenshteinDistance

    compileOnly(project.property("serverAPI") as String)

    // Spigot Libs (soft-depend)
    compileOnly("me.clip:placeholderapi:2.11.7")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.19")
    // Combat Integrations
    compileOnly("net.minelink:CombatTagPlus:1.3.1")
    compileOnly("me.nochance:PvPManager:3.15.9")
    compileOnly("nl.marido.deluxecombat:DeluxeCombat:1.40.5")
}

java {
    // Under MC_SERVER_NEWEST_API=true, `serverAPI` is paper-api 26.2, whose class files are
    //  major 69 and whose Gradle metadata declares org.gradle.jvm.version=25.
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    // Declare Java 21 explicitly. options.release controls what javac EMITS but does not feed
    //  org.gradle.jvm.version on the outgoing variants, which would otherwise default to the
    //  toolchain and publish metadata claiming Java 25 over major-65 bytecode.
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
configurations.named("compileClasspath").configure {
    // The resolver rejects that library for a Java 21 consumer BEFORE javac runs, so
    //  options.release alone cannot reach it.
    attributes {
        attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }
}
tasks.withType<JavaCompile>().configureEach {
    // The output must stay Java 21. A jar mixing class-file versions does not work: the JVM loads
    //  referenced classes during verification, so one major-69 class makes the providers that
    //  merely NAME it unloadable on every pre-26 server.
    options.release.set(21)
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
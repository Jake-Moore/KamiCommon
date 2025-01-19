@Suppress("PropertyName")
var VERSION = "4.0.0-beta.9"

plugins { // needed for the allprojects section to work
    id("java")
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.5" apply false
}

ext {
    // reduced is just a re-zipped version of the original, without some conflicting libraries
    //  gson, org.json, com.yaml.snakeyaml
    set("lowestSpigotDep", "net.techcable.tacospigot:server:1.8.8-R0.2-REDUCED")    // luxious nexus (public)
}

allprojects {
    group = "com.kamikazejam.kamicommon"
    version = VERSION
    description = "KamikazeJAM's common library for Spigot and Standalone projects."

    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    // Provision Java 17 all projects (Java 21 required for spigot-utils and spigot-jar)
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    repositories {
        mavenLocal()
        mavenCentral()
        // PaperMC & SpigotMC
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        // Luxious Nexus
        maven("https://repo.luxiouslabs.net/repository/maven-public/")
        // Spigot Plugin Repos
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://mvn.lumine.io/repository/maven-public/") {
            content {
                includeGroup("io.lumine")
                excludeGroup("org.jetbrains")
            }
        }
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        // Misc repos
        maven("https://jitpack.io")
        gradlePluginPortal()
    }

    dependencies {
        // Lombok
        compileOnly("org.projectlombok:lombok:1.18.36")
        annotationProcessor("org.projectlombok:lombok:1.18.36")
        testImplementation("org.projectlombok:lombok:1.18.36")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.36")

        // IntelliJ annotations
        compileOnly("org.jetbrains:annotations:26.0.1")
        testImplementation("org.jetbrains:annotations:26.0.1")
    }

    // We want UTF-8 for everything
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }
}

// Disable root project build
tasks.jar.get().enabled = false

// ---------------- Define Helper Methods ---------------- //

// This method adapts the version string to send the artifact to the snapshots repository if it does not
//  comply with the SemVer release versioning scheme.
extra["getPublishingVersion"] = {
    val version = rootProject.version.toString()
    if (!version.matches(Regex("^\\d+\\.\\d+\\.\\d+$"))) {
        // Only apply -SNAPSHOT if necessary
        if (!version.endsWith("-SNAPSHOT")) "$version-SNAPSHOT" else version
    } else {
        version
    }
}

// Allowed Version Formats:
//   1. A simple SemVer release version: X.Y.Z (e.g. 1.0.0, 2.1.3, 10.20.30)
//   2. A pre-release version: X.Y.Z-TYPE.NUMBER (e.g. 1.0.0-alpha.1, 1.0.0-beta.2, 1.0.0-rc.3)
//      'TYPE' can be 'alpha', 'beta', or 'rc'
//   3. A snapshot version: Any version suffixed with -SNAPSHOT (e.g. 1.0.0-SNAPSHOT, 1.0.0-alpha.1-SNAPSHOT)
// Publication Locations by Version Format:
//   - maven-releases: Formats 1 and 2 (releases and pre-releases)
//   - maven-snapshots: Format 3 (snapshots)
// Invalid Versions:
//   - Any version not matching one of the above formats will not be published. Publication will be skipped.
@Suppress("PropertyName")
var VERSION = "5.0.0-alpha.13" // -SNAPSHOT REQUIRED for dev builds to the snapshots repo

plugins { // needed for the allprojects section to work
    id("java")
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.0.2" apply false
}

ext {
    // reduced is just a re-zipped version of the original, without some conflicting libraries
    //  gson, org.json, com.yaml.snakeyaml
    set("lowestSpigotDep", "net.techcable.tacospigot:server:1.8.8-R0.2-REDUCED-KC")    // luxious nexus (public)
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
        compileOnly("org.projectlombok:lombok:1.18.38")
        annotationProcessor("org.projectlombok:lombok:1.18.38")
        testImplementation("org.projectlombok:lombok:1.18.38")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.38")

        // IntelliJ annotations
        compileOnly("org.jetbrains:annotations:26.0.2")
        testImplementation("org.jetbrains:annotations:26.0.2")
    }

    // We want UTF-8 for everything
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    // Disable tests, KamiCommon does not use a testing framework yet.
    tasks.named<Test>("test") {
        enabled = false
    }
}

// Disable root project build
tasks.jar.get().enabled = false

// -------------------------------------------------- //
//          Version Management for Publishing         //
// -------------------------------------------------- //
// Regex patterns for allowed formats
val semverRelease = Regex("""^\d+\.\d+\.\d+$""")
val semverPreRelease = Regex("""^\d+\.\d+\.\d+-(alpha|beta|rc)\.\d+$""")
val semverSnapshot = Regex("""^.+-SNAPSHOT$""")

// Function to resolve publishing version (nullable)
/**
 * @returns Pair<VersionString or null, isSnapshot:Boolean>
 */
val getPublishingVersion: () -> Pair<String, Boolean>? = {
    when {
        semverRelease.matches(VERSION) -> VERSION to false
        semverPreRelease.matches(VERSION) -> VERSION to false
        semverSnapshot.matches(VERSION) -> VERSION to true
        else -> null
    }
}

// Expose it to subprojects
rootProject.extra["getPublishingVersion"] = getPublishingVersion
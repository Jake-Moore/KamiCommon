import java.util.*

@Suppress("PropertyName")
var VERSION = "3.0.2.4"

plugins { // needed for the subprojects section to work
    id("java")
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.1" apply false
    id("io.github.goooler.shadow") version "8.1.7" apply false
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
}

tasks.register("refresh") {
    doLast {
        val process: Process
        if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("windows")) {
            // Use git bash's sh on Windows
            // process = ProcessBuilder("sh", "./refresh.sh").start()
            return@doLast
        }else {
            process = ProcessBuilder("./refresh.sh").start()
        }

        val inputStream = process.inputStream
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { println(it) }
        }

        process.waitFor()
        if (process.exitValue() != 0) {
            throw IllegalStateException("Failed to refresh: " + process.exitValue())
        }
    }
}
tasks.getByName("clean").finalizedBy(tasks.getByName("refresh"))

//idea.project.settings {
//    taskTriggers {
//        afterSync(tasks.getByName("refresh"))
//    }
//}

ext {
    set("projectName", rootProject.name)
    set("lombokDep", "org.projectlombok:lombok:1.18.32")

    // reduced is just a re-zipped version of the original, without some conflicting libraries
    //  gson, org.json, com.yaml.snakeyaml
    set("lowestSpigotDep", "net.techcable.tacospigot:server:1.8.8-R0.2-REDUCED")    // luxious nexus (public)
}

allprojects {
    group = "com.kamikazejam.kamicommon"
    version = VERSION
    description = "KamikazeJAM's common library for Spigot and Standalone projects."

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://nexus.luxiouslabs.net/public")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://mvn.lumine.io/repository/maven-public/")
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://jitpack.io")
        gradlePluginPortal()
    }

    // We want UTF-8 for everything
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    // Provision Java 17 all subprojects (new modules have version 21 configured)
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Disable root project build
tasks.jar.get().enabled = false

tasks {
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

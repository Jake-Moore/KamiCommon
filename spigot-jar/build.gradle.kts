import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
    id("com.gradleup.shadow")
}

repositories {
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Unique dependencies for this module
    implementation(project(":shared-jar"))
    implementation(project(":spigot-utils"))

    implementation("org.apache.httpcomponents.client5:httpclient5:5.5.1")
    implementation("org.apache.httpcomponents.core5:httpcore5:5.3.6")

    // Spigot Libraries
    compileOnly(project.property("serverAPI") as String)
}

tasks {
    publish.get().dependsOn(build.get())
    build.get().dependsOn(shadowJar)
    shadowJar.get().dependsOn(jar)

    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("KamiCommon")

        // From particlenativeapi
        exclude("LICENSE*", "META-INF/LICENSE*")
        exclude("License*", "META-INF/License*")

        // Versions differ and can break builds if old copies get included here
        exclude("org/intellij/lang/annotations/**")
        exclude("org/jetbrains/annotations/**")

        // KamiCommonNMS
        relocate("com.cryptomorin.xseries", "com.kamikazejam.kamicommon.xseries")
        relocate("com.github.fierioziy.particlenativeapi", "com.kamikazejam.kamicommon.particleapi")
        relocate("de.tr7zw.changeme.nbtapi", "com.kamikazejam.kamicommon.nbtapi")
        // shared-jar
        relocate("com.zaxxer.hikari", "com.kamikazejam.kamicommon.hikari")
        relocate("org.apache.commons.pool2", "com.kamikazejam.kamicommon.commons.pool2")
        relocate("com.mysql", "com.kamikazejam.kamicommon.mysql")
        relocate("com.rabbitmq", "com.kamikazejam.kamicommon.rabbitmq")
        relocate("org.slf4j", "com.kamikazejam.kamicommon.slf4j")
        relocate("io.netty", "com.kamikazejam.kamicommon.netty")
        relocate("reactor", "com.kamikazejam.kamicommon.reactor")
        relocate("org.reactivestreams", "com.kamikazejam.kamicommon.reactivestreams")
        relocate("io.lettuce.core", "com.kamikazejam.kamicommon.lettuce.core")
        // standalone-utils
        relocate("org.yaml.snakeyaml", "com.kamikazejam.kamicommon.snakeyaml")
        relocate("org.json", "com.kamikazejam.kamicommon.json")
        // standalone-jar
        relocate("com.google.gson", "com.kamikazejam.kamicommon.gson")
        relocate("com.google.errorprone", "com.kamikazejam.kamicommon.errorprone")
        // spigot-utils
        relocate("org.apache.commons.text", "com.kamikazejam.kamicommon.text")
        relocate("org.apache.commons.lang3", "com.kamikazejam.kamicommon.lang3")
        // spigot-jar
        relocate("org.apache.hc.client5", "com.kamikazejam.kamicommon.hc.client5")
        relocate("org.apache.hc.core5", "com.kamikazejam.kamicommon.hc.core5")
    }
    jar {
        // Starting with 1.20.5 Paper we can choose not to reobf the jar, leaving it mojang mapped
        //  we forfeit spigot compatability, but it will natively work on paper
        // The following manifest attribute notifies paper that this jar need not be deobfuscated
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang+yarn"
        }
        archiveBaseName.set("KamiCommon")
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to rootProject.name,
            "version" to rootProject.version,
            "description" to rootProject.description,
            "date" to DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
        filesMatching("**/version.json") {
            expand(props)
        }
    }
}

//gradle.projectsEvaluated {
//    tasks.getByName("publishShadowPublicationToMavenRepository").dependsOn(tasks.jar)
//}

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
    // standalone-utils includes only shared-utils
    exportedProjects = listOf(
        ":spigot-jar",
        ":spigot-utils",
        ":shared-jar",
        ":shared-utils",
        ":standalone-utils",
    )
    moduleName = "spigot-jar"
    usesShadow = true
}

tasks.register("printServerAPI") {
    doFirst {
        logger.info("[${project.name}] Using Server API: ${project.property("serverAPI") as String}")
    }
}
tasks.compileJava.get().dependsOn(tasks.named("printServerAPI"))
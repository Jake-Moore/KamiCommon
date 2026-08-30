plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

// HikariCP publishes NO Gradle module metadata, verified across every cached version: there are
    // no .module files under ~/.gradle/caches for com.zaxxer, and its Java 11 requirement appears
    // only as an OSGi Require-Capability in the manifest, which Gradle does not read. So a Java 8
    // consumer resolves it silently and there is NO resolution error to fall back on.
    // Database.requireJava11() is the whole control, which is why verifySealedHierarchies-style
    // enforcement matters here: nothing stops a second class reaching Hikari without the guard.
// See buildSrc/src/main/kotlin/module-floor-convention.gradle.kts for what each setting does.
extra["moduleFloor"] = 8
apply(plugin = "module-floor-convention")


// Dependency Version Configuration
val slf4jVersion = "2.0.18"
dependencies {
    api(project(":shared-utils"))

    // MySQL via HikariCP (2,725 KB)
    api("com.zaxxer:HikariCP:7.1.0")
    api("com.mysql:mysql-connector-j:9.7.0") { exclude("com.google.protobuf", "protobuf-java") }

    // RabbitMQ amqp-client (732 KB)
    api("com.rabbitmq:amqp-client:5.35.0")

    // SLF4J (39 KB) (needed for RabbitMQ)
    api("org.slf4j:slf4j-api:$slf4jVersion")
    api("org.slf4j:slf4j-simple:$slf4jVersion")

    // Lettuce Core (Redis) (6,246 KB)
    api("io.lettuce:lettuce-core:7.7.0.RELEASE")
}

// Configure javadoc-publish-convention
configure<Javadoc_publish_convention_gradle.JavadocPublishExtension> {
    // shared-jar includes only shared-utils
    exportedProjects = listOf(
        ":shared-jar",
        ":shared-utils",
    )
    moduleName = "shared-jar"
}
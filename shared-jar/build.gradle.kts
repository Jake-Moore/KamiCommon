plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

// Its own bytecode is Java 8. HikariCP declares 11 for itself, so a consumer
// that actually pulls the database stack still gets a resolution error naming Hikari, which is
// the right place for that constraint to live.
// See buildSrc/src/main/kotlin/module-floor-convention.gradle.kts for what each setting does.
extra["moduleFloor"] = 8
apply(plugin = "module-floor-convention")


// Dependency Version Configuration
val slf4jVersion = "2.0.17"
dependencies {
    api(project(":shared-utils"))

    // MySQL via HikariCP (2,725 KB)
    api("com.zaxxer:HikariCP:7.0.2")
    api("com.mysql:mysql-connector-j:9.5.0") { exclude("com.google.protobuf", "protobuf-java") }

    // RabbitMQ amqp-client (732 KB)
    api("com.rabbitmq:amqp-client:5.27.0")

    // SLF4J (39 KB) (needed for RabbitMQ)
    api("org.slf4j:slf4j-api:$slf4jVersion")
    api("org.slf4j:slf4j-simple:$slf4jVersion")

    // Lettuce Core (Redis) (6,246 KB)
    api("io.lettuce:lettuce-core:7.0.0.RELEASE")
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
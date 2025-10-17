plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

// Dependency Version Configuration
val slf4jVersion = "2.0.17"
dependencies {
    api(project(":shared-utils"))

    // MySQL via HikariCP (2,725 KB)
    api("com.zaxxer:HikariCP:7.0.2")
    api("com.mysql:mysql-connector-j:9.4.0") { exclude("com.google.protobuf", "protobuf-java") }

    // RabbitMQ amqp-client (732 KB)
    api("com.rabbitmq:amqp-client:5.27.0")

    // SLF4J (39 KB) (needed for RabbitMQ)
    api("org.slf4j:slf4j-api:$slf4jVersion")
    api("org.slf4j:slf4j-simple:$slf4jVersion")

    // Lettuce Core (Redis) (6,246 KB)
    api("io.lettuce:lettuce-core:6.8.1.RELEASE")
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
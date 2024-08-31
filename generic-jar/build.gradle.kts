plugins {
    // Unique plugins for this module
}

// Dependency Version Configuration
val slf4jVersion = "2.0.16"
val rabbitMQVersion = "com.rabbitmq:amqp-client:5.21.0"
val hikariVersion = "com.zaxxer:HikariCP:5.1.0"
val mysqlVersion = "com.mysql:mysql-connector-j:9.0.0"
val lettuceVersion = "io.lettuce:lettuce-core:6.4.0.RELEASE"
val jacksonVersion = "2.17.2"
val jacksonDatabindVersion = "com.fasterxml.jackson.core:jackson-databind:$jacksonVersion"
val jacksonCoreVersion = "com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion"

dependencies {
    api(project(":generic-utils")); shadow(project(":generic-utils"))

    // MySQL via HikariCP (2,725 KB)
    api(hikariVersion); shadow(hikariVersion)
    api(mysqlVersion) { exclude("com.google.protobuf", "protobuf-java") }
    shadow(mysqlVersion) { exclude("com.google.protobuf", "protobuf-java") }

    // RabbitMQ amqp-client (732 KB)
    api(rabbitMQVersion); shadow(rabbitMQVersion)

    // SLF4J (39 KB) (needed for RabbitMQ)
    api("org.slf4j:slf4j-api:$slf4jVersion"); shadow("org.slf4j:slf4j-api:$slf4jVersion")
    api("org.slf4j:slf4j-simple:$slf4jVersion"); shadow("org.slf4j:slf4j-simple:$slf4jVersion")

    // Lettuce Core (Redis) (6,246 KB)
    api(lettuceVersion); shadow(lettuceVersion)

    // For the redis system to deserialize messages (2,244 KB)
    api(jacksonDatabindVersion); shadow(jacksonDatabindVersion)
    api(jacksonCoreVersion); shadow(jacksonCoreVersion)

    // Tests
    testImplementation(rabbitMQVersion)
    testImplementation(lettuceVersion)
    testImplementation(jacksonDatabindVersion)
    testImplementation(jacksonCoreVersion)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = rootProject.group.toString()
            artifactId = project.name
            version = rootProject.version.toString()
            from(components["java"])
        }
    }

    repositories {
        maven {
            credentials {
                username = System.getenv("LUXIOUS_NEXUS_USER")
                password = System.getenv("LUXIOUS_NEXUS_PASS")
            }
            // Select URL based on version (if it's a snapshot or not)
            url = if (project.version.toString().endsWith("-SNAPSHOT")) {
                uri("https://repo.luxiouslabs.net/repository/maven-snapshots/")
            }else {
                uri("https://repo.luxiouslabs.net/repository/maven-releases/")
            }
        }
    }
}
plugins {
    // Unique plugins for this module
}

// Dependency Version Configuration
val slf4jVersion = "2.0.16"
val jacksonVersion = "2.18.2"
dependencies {
    api(project(":shared-utils"))

    // MySQL via HikariCP (2,725 KB)
    api("com.zaxxer:HikariCP:6.2.1")
    api("com.mysql:mysql-connector-j:9.1.0") { exclude("com.google.protobuf", "protobuf-java") }

    // RabbitMQ amqp-client (732 KB)
    api("com.rabbitmq:amqp-client:5.23.0")

    // SLF4J (39 KB) (needed for RabbitMQ)
    api("org.slf4j:slf4j-api:$slf4jVersion")
    api("org.slf4j:slf4j-simple:$slf4jVersion")

    // Lettuce Core (Redis) (6,246 KB)
    api("io.lettuce:lettuce-core:6.5.1.RELEASE")

    // For the redis system to deserialize messages (2,244 KB)
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
}

tasks {
    publish.get().dependsOn(build)
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
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
            // Only allow valid SemVer release versions for the releases repository
            url = if (project.version.toString().matches(Regex("^\\d+\\.\\d+\\.\\d+$"))) {
                uri("https://repo.luxiouslabs.net/repository/maven-releases/")
            } else {
                uri("https://repo.luxiouslabs.net/repository/maven-snapshots/")
            }
        }
    }
}
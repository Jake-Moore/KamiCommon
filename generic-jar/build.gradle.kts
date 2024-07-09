plugins {
    // Unique plugins for this module
    id("io.github.goooler.shadow")
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

val slf4jVersion = "2.0.13" // For RabbitMQ
dependencies {
    // MySQL via HikariCP (2,725 KB)
    shadow("com.zaxxer:HikariCP:5.1.0")
    shadow("com.mysql:mysql-connector-j:8.4.0") {
        exclude("com.google.protobuf", "protobuf-java")
    }

    // RabbitMQ amqp-client (732 KB)
    shadow("com.rabbitmq:amqp-client:5.21.0")
    testImplementation("com.rabbitmq:amqp-client:5.21.0")

    // SLF4J (39 KB)
    shadow("org.slf4j:slf4j-api:$slf4jVersion")
    shadow("org.slf4j:slf4j-simple:$slf4jVersion")

    // Lettuce Core (Redis) (6,246 KB)
    shadow("io.lettuce:lettuce-core:6.3.2.RELEASE")

    // For the redis system to deserialize messages (2,244 KB)
    shadow("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    shadow("com.fasterxml.jackson.core:jackson-annotations:2.17.1")

    // Tests
    testImplementation("io.lettuce:lettuce-core:6.3.2.RELEASE")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    testImplementation("com.fasterxml.jackson.core:jackson-annotations:2.17.1")
}

tasks {
    publish.get().dependsOn(build)
    build.get().dependsOn("shadowJar")
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        relocate("com.zaxxer.hikari", "com.kamikazejam.kamicommon.hikari")
        relocate("org.slf4j", "com.kamikazejam.kamicommon.slf4j") // part of the hikari jar
        relocate("org.apache.commons.pool2", "com.kamikazejam.kamicommon.commons.pool2")
        relocate("com.mysql", "com.kamikazejam.kamicommon.mysql")
        relocate("com.rabbitmq", "com.kamikazejam.kamicommon.rabbitmq")
        relocate("org.slf4j", "com.kamikazejam.kamicommon.slf4j")
        relocate("io.netty", "com.kamikazejam.kamicommon.netty")
        relocate("reactor", "com.kamikazejam.kamicommon.reactor")
        relocate("org.reactivestreams", "com.kamikazejam.kamicommon.reactivestreams")
        // don't relocate jackson
        relocate("io.lettuce.core", "com.kamikazejam.kamicommon.lettuce.core")
    }
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
            url = uri("https://nexus.luxiouslabs.net/public")
            credentials {
                username = System.getenv("LUXIOUS_NEXUS_USER")
                password = System.getenv("LUXIOUS_NEXUS_PASS")
            }
        }
    }
}

// ONLY REQUIRED IF: you are using Solution 2 with the modified dependency
tasks.register<Copy>("unpackShadow") {
    dependsOn(tasks.shadowJar)
    from(zipTree(layout.buildDirectory.dir("libs").map { it.file(tasks.shadowJar.get().archiveFileName) }))
    into(layout.buildDirectory.dir("unpacked-shadow"))
}
tasks.getByName("build").finalizedBy(tasks.getByName("unpackShadow"))
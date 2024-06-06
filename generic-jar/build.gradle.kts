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

val slf4jVersion = "1.7.36" // For RabbitMQ
dependencies {
    // Unique dependencies for this module
    shadow("com.zaxxer:HikariCP:5.1.0")
    shadow("com.mysql:mysql-connector-j:8.4.0") // comes with google.protobuf
    shadow("redis.clients:jedis:5.2.0-SNAPSHOT") {
        // jedis requires both of these, so any other -jar modules need to include them
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "org.json", module = "json")
    }

    // RabbitMQ amqp-client
    shadow("com.rabbitmq:amqp-client:5.21.0")
    compileOnly("org.jetbrains:annotations:24.1.0")

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    shadow("org.slf4j:slf4j-api:$slf4jVersion")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    shadow("org.slf4j:slf4j-simple:$slf4jVersion")

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}

tasks {
    publish.get().dependsOn(build)
    build.get().dependsOn("shadowJar")
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        relocate("com.zaxxer.hikari", "com.kamikazejam.kamicommon.hikari")
        relocate("org.slf4j", "com.kamikazejam.kamicommon.slf4j") // part of the hikari jar
        relocate("redis.clients.jedis", "com.kamikazejam.kamicommon.redis")
        relocate("org.apache.commons.pool2", "com.kamikazejam.kamicommon.commons.pool2")
        relocate("com.mysql", "com.kamikazejam.kamicommon.mysql")
        relocate("com.google.protobuf", "com.kamikazejam.kamicommon.google.protobuf")
        relocate("com.rabbitmq", "com.kamikazejam.kamicommon.rabbitmq")
        relocate("org.slf4j", "com.kamikazejam.kamicommon.slf4j")
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
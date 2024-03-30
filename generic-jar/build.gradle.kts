plugins {
    // Unique plugins for this project
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    // Unique dependencies for this project
    // api("com.zaxxer:HikariCP:5.1.0")

    // compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    //compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.test {
    useJUnitPlatform()
}
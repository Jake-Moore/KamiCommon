plugins {
    // Unique plugins for this module
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

var hikari = "com.zaxxer:HikariCP:5.1.0"
dependencies {
    // Unique dependencies for this module
    api(hikari); shadow(hikari)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        dependencies {
            include(dependency(hikari))
        }
        relocate("com.zaxxer.hikari", "com.kamikazejam.kamicommon.hikari")
    }
    test {
        useJUnitPlatform()
    }
}
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8
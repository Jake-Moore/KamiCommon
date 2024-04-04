plugins {
    // Unique plugins for this module
    id("com.github.johnrengelman.shadow")
}

var httpclient = "org.apache.httpcomponents.client5:httpclient5:5.3.1"
dependencies {
    // Unique dependencies for this module
    api(httpclient); shadow(httpclient)
    api(project(":generic-jar"));
    api(project(":spigot-utils"));

    compileOnly(project.property("lowestSpigotDep") as String)

    // Spigot Libs (soft-depend)
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.2-beta-r3")
    compileOnly("net.citizensnpcs:citizens-main:2.0.33-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("io.lumine:Mythic-Dist:5.5.1")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.18-3")

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        configurations = listOf(project.configurations.shadow.get())

        dependencies {
            include(dependency(httpclient))
        }
        relocate("org.apache.hc.client5", "com.kamikazejam.kamicommon.hc.client5")

        from(project(":generic-jar").tasks.shadowJar.get().outputs)
        from(project(":spigot-utils").tasks.shadowJar.get().outputs)

    }
    test {
        useJUnitPlatform()
    }
}
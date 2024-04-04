plugins {
    // Unique plugins for this module
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api(project(":generic-jar"))
    api(project(":standalone-utils"))
}
tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        from(project(":generic-jar").tasks.shadowJar.get().outputs)
        from(project(":standalone-utils").tasks.shadowJar.get().outputs)

        configurations = listOf(project.configurations.shadow.get())
    }
    test {
        useJUnitPlatform()
    }
}
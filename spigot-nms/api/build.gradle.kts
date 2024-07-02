plugins {
    id("io.github.goooler.shadow")
}

repositories {
    mavenCentral()
}

dependencies {
    shadow(files(project(":standalone-utils")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )
    shadow("com.github.cryptomorin:XSeries:11.2.0")

    compileOnly(project.property("lowestSpigotDep") as String)
}

tasks {
    build.get().dependsOn(shadowJar)
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        relocate("com.cryptomorin.xseries", "com.kamikazejam.kamicommon.xseries")
    }
}


// ONLY REQUIRED IF: you are using Solution 2 with the modified dependency
tasks.register<Copy>("unpackShadow") {
    dependsOn(tasks.shadowJar)
    from(zipTree(layout.buildDirectory.dir("libs").map { it.file(tasks.shadowJar.get().archiveFileName) }))
    into(layout.buildDirectory.dir("unpacked-shadow"))
}
tasks.getByName("build").finalizedBy(tasks.getByName("unpackShadow"))
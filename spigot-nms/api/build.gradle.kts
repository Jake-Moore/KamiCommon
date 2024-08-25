plugins {
    id("io.github.goooler.shadow")
}

repositories {
    mavenCentral()
}

dependencies {
    // Submodules are compile-only since they are included in the shadowJar task configuration: from(...)
    compileOnly(files(project(":standalone-utils")
        .dependencyProject.layout.buildDirectory.dir("unpacked-shadow"))
    )

    shadow("de.tr7zw:item-nbt-api:2.13.2")
    shadow("com.github.cryptomorin:XSeries:11.2.1")
    shadow("com.github.fierioziy.particlenativeapi:ParticleNativeAPI-core:4.3.0")

    compileOnly(project.property("lowestSpigotDep") as String)
}

tasks {
    build.get().dependsOn(shadowJar)
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())

        relocate("com.cryptomorin.xseries", "com.kamikazejam.kamicommon.xseries")
        relocate("com.github.fierioziy.particlenativeapi", "com.kamikazejam.kamicommon.particleapi")
        relocate("de.tr7zw.changeme.nbtapi", "com.kamikazejam.kamicommon.nbt.nbtapi")

        from(project(":standalone-utils").tasks.shadowJar.get().outputs)
    }
}


// ONLY REQUIRED IF: you are using Solution 2 with the modified dependency
tasks.register<Copy>("unpackShadow") {
    dependsOn(tasks.shadowJar)
    from(zipTree(layout.buildDirectory.dir("libs").map { it.file(tasks.shadowJar.get().archiveFileName) }))
    into(layout.buildDirectory.dir("unpacked-shadow"))
}
tasks.getByName("build").finalizedBy(tasks.getByName("unpackShadow"))
plugins {
    id("java")
    id("maven-publish")
}

// Define an extension to hold project-specific properties
open class JavadocPublishExtension {
    var exportedProjects: List<String>? = null
    var moduleName: String? = null
    var usesShadow: Boolean = false

    // "group:name" coordinates of external libraries that this module shades in whole. Their published
    // -sources jars are folded into the aggregate javadoc and sources jars, so an IDE can document the
    // shaded classes. A shaded library is invisible to dependency resolution, so without this an IDE has
    // no module to hang the library's own javadoc jar on and shows nothing for those classes.
    // The version is never written here. It is read from a declared dependency in one of the
    // exportedProjects, so it cannot drift away from the version actually being shaded.
    var shadedSources: List<String> = emptyList()

    // Full "group:name:version" coordinates needed only to resolve symbols while documenting
    // shadedSources. A library's compileOnly dependencies never appear in its published metadata, so
    // unlike the shadedSources version these cannot be derived and must be written out. Javadoc fails
    // loudly on an unresolved symbol, so a stale entry here shows up as a build failure, not silence.
    var shadedSourcesClasspath: List<String> = emptyList()
}

// Create the extension
val javadocPublish = extensions.create<JavadocPublishExtension>("luxiousPlugin")

// Javadoc module detection requires project evaluation (so api module is detected)
gradle.projectsEvaluated {
    // -------------------------------------------------- //
    //                      Javadocs                      //
    // -------------------------------------------------- //
    // Take api, core
    //   The version specific implementation modules don't have public API or javadocs
    //   They are excluded to avoid Javadoc errors due to NMS references that javadoc can't handle
    val exportedProjects = javadocPublish.exportedProjects?.map { project(it) }
        ?: throw GradleException("[tasks] exportedProjects must be set in the javadocPublish extension")
    val moduleName = javadocPublish.moduleName
        ?: throw GradleException("[tasks] moduleName must be set in the javadocPublish extension")
    val usesShadow = javadocPublish.usesShadow

    // -------------------------------------------------- //
    //          Sources for shaded-in libraries           //
    // -------------------------------------------------- //
    // A shaded library has no module of its own on the consumer side, so an IDE has nothing to attach the
    // library's published javadoc jar to and documents none of those classes. Fold the library's -sources
    // jar into ours instead. The version is read from a declaration in one of the exportedProjects rather
    // than written here, so the documentation cannot drift away from the bytes actually shaded.
    val shadedSourceCoordinates = javadocPublish.shadedSources.map { coordinate ->
        val parts = coordinate.split(":")
        if (parts.size != 2) {
            throw GradleException("[javadoc] shadedSources entries must be 'group:name', got '$coordinate'")
        }
        val (depGroup, depName) = parts

        val versions = exportedProjects
            .flatMap { proj -> proj.configurations.flatMap { conf -> conf.dependencies } }
            .filter { it.group == depGroup && it.name == depName }
            .mapNotNull { it.version }
            .toSet()
        when {
            versions.isEmpty() -> throw GradleException(
                "[javadoc] shadedSources names '$coordinate', but none of the exportedProjects " +
                        "${exportedProjects.map { it.path }} declares it. Either it is no longer shaded, in " +
                        "which case drop it from shadedSources, or it moved to a module outside " +
                        "exportedProjects, in which case its version can no longer be read here."
            )
            versions.size > 1 -> throw GradleException(
                "[javadoc] '$coordinate' is declared at more than one version ($versions), so there is no " +
                        "single version to document. Align the declarations first."
            )
        }
        "$depGroup:$depName:${versions.single()}:sources"
    }

    val shadedSourcesDir = layout.buildDirectory.dir("shadedSources")
    val extractShadedSources = if (shadedSourceCoordinates.isEmpty()) null else {
        val shadedSourceJars = configurations.create("shadedSourceJars") {
            isTransitive = false
            isCanBeConsumed = false
            isCanBeResolved = true
        }
        shadedSourceCoordinates.forEach { dependencies.add(shadedSourceJars.name, it) }

        configurations.create("shadedSourcesClasspath") {
            isCanBeConsumed = false
            isCanBeResolved = true
        }
        javadocPublish.shadedSourcesClasspath.forEach { dependencies.add("shadedSourcesClasspath", it) }

        tasks.register<Sync>("extractShadedSources") {
            group = "documentation"
            description = "Unpacks the -sources jars of shaded libraries so they can be documented with ours"
            from(provider { shadedSourceJars.files.map { zipTree(it) } })
            into(shadedSourcesDir)

            // An empty result means the sources jar was missing or held nothing, which would silently
            // produce documentation with a hole in it exactly where the shaded classes should be.
            doLast {
                val count = shadedSourcesDir.get().asFile.walkTopDown().count { it.extension == "java" }
                if (count == 0) {
                    throw GradleException(
                        "[javadoc] $shadedSourceCoordinates unpacked to no .java files. The sources jars " +
                                "resolved but are empty, so the shaded classes would ship undocumented."
                    )
                }
                logger.lifecycle("[javadoc] unpacked $count shaded source files from $shadedSourceCoordinates")
            }
        }
    }

    val aggregateJavadoc = tasks.register<Javadoc>("aggregateJavadoc") {
        val javaProjects = exportedProjects.filter { project ->
            project.plugins.hasPlugin("java")
        }

        // println("Generating Javadocs for projects (${javaProjects.size}): ${javaProjects.map { it.path }}")
        if (javaProjects.isEmpty()) {
            throw GradleException("No Java projects found in exportedProjects for Javadoc generation")
        }

        source(javaProjects.map { proj ->
            proj.extensions.getByType<SourceSetContainer>()["main"].allJava.matching {
                // Optional: Exclude classes that Javadoc can't handle, and that aren't needed in the docs
            }
        })
        extractShadedSources?.let {
            dependsOn(it)
            source(shadedSourcesDir.get().asFileTree.matching { include("**/*.java") })
        }
        classpath = files(javaProjects.map {
            it.extensions.getByType<SourceSetContainer>()["main"].compileClasspath
        })
        // Must come after the assignment above, which replaces the classpath rather than adding to it.
        if (extractShadedSources != null) {
            classpath += configurations["shadedSourcesClasspath"]
        }

        val aggregateJavadocDir = file("${layout.buildDirectory.get().asFile.absolutePath}/docs/aggregateJavadoc")
        setDestinationDir(aggregateJavadocDir)

        (options as StandardJavadocDocletOptions).apply {
            encoding = "UTF-8"
            charSet = "UTF-8"
            windowTitle = "KamiCommon"
            docTitle = "kamicommon:$moduleName ${rootProject.version} API"

            // External links
            links(
                "https://docs.oracle.com/en/java/javase/21/docs/api/",
                // Paper API javadocs site
                "https://jd.papermc.io/paper/",
                // Link back to KamiCommonNMS
                "https://docs.jake-moore.dev/KamiCommonNMS/latest/",
            )

            // Treat missing external links as warnings
            addBooleanOption("Xdoclint:none", true)
        }
    }

    // Create the Javadoc JAR task (provides rich javadocs in IDEs)
    val aggregateJavadocJar = tasks.register<Jar>("aggregateJavadocJar") {
        group = "documentation"
        description = "Assembles a JAR archive containing the combined Javadocs"

        archiveClassifier.set("javadoc")
        from(aggregateJavadoc.get().destinationDir)

        dependsOn(aggregateJavadoc)
    }

    // Create the combined sources JAR (contains .java files) (provides fallback sources in IDEs)
    val aggregateSourcesJar = tasks.register<Jar>("aggregateSourcesJar") {
        group = "build"
        description = "Assembles sources JAR for all modules"

        val javaProjects = exportedProjects.filter {
            it.plugins.hasPlugin("java")
        }

        from(javaProjects.map {
            it.extensions.getByType<SourceSetContainer>()["main"].allSource
        })
        extractShadedSources?.let {
            dependsOn(it)
            from(shadedSourcesDir)
        }
        archiveClassifier.set("sources")
    }

    // Automatically generate jars on build
    tasks.build.get().dependsOn(aggregateJavadocJar)
    tasks.build.get().dependsOn(aggregateSourcesJar)




    // -------------------------------------------------- //
    //                  publishing (java)                 //
    // -------------------------------------------------- //
    tasks.publish.get().dependsOn(tasks.build.get())
    tasks.publish.get().dependsOn(aggregateJavadocJar)
    tasks.publish.get().dependsOn(aggregateSourcesJar)

    @Suppress("UNCHECKED_CAST")
    val getPublishingVersion = rootProject.extra["getPublishingVersion"] as () -> Pair<String, Boolean>?

    publishing {
        val versionData = getPublishingVersion() ?: run {
            logger.warn("⚠️ Skipping publication: VERSION '${rootProject.version}' is not valid.")
            return@publishing
        }
        val resolvedVersion = versionData.first
        val isSnapshot = versionData.second

        publications {
            create<MavenPublication>("shadow") {
                groupId = rootProject.group.toString()
                artifactId = project.name
                version = resolvedVersion
                // Select the correct components based on the current build tooling
                if (usesShadow) {
                    from(components["shadow"])
                } else {
                    from(components["java"])
                }

                // Add both documentation artifacts
                artifact(tasks.named("aggregateJavadocJar")) // HTML documentation
                artifact(tasks.named("aggregateSourcesJar")) // Java source files
            }
        }

        repositories {
            maven {
                credentials {
                    username = System.getenv("LUXIOUS_NEXUS_USER")
                    password = System.getenv("LUXIOUS_NEXUS_PASS")
                }
                // getPublishingVersion will append "-SNAPSHOT" if the version is not a SemVer release version
                url = if (!isSnapshot) {
                    uri("https://repo.luxiouslabs.net/repository/maven-releases/")
                } else {
                    uri("https://repo.luxiouslabs.net/repository/maven-snapshots/")
                }
            }
        }
    }
}
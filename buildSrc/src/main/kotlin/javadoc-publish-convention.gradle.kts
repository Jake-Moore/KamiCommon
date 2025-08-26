plugins {
    id("java")
    id("maven-publish")
}

// Define an extension to hold project-specific properties
open class JavadocPublishExtension {
    var exportedProjects: List<String>? = null
    var moduleName: String? = null
    var usesShadow: Boolean = false
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
        classpath = files(javaProjects.map {
            it.extensions.getByType<SourceSetContainer>()["main"].compileClasspath
        })

        destinationDir = file("${layout.buildDirectory.get().asFile.absolutePath}/docs/aggregateJavadoc")

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
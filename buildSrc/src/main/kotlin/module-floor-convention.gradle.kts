plugins {
    id("java")
}

// Sets one module's Java floor: what its bytecode targets, and what it tells consumers.
//
// Applied as:
//     extra["moduleFloor"] = 8
//     plugins { id("module-floor-convention") }
//
// Four settings, each doing a different job, and none of them substitutes for another:
//
//   toolchain 25         javac must RUN on 25 to read class-file major 69, which is what
//                        paper-api 26.x ships. About reading, not emitting.
//   compileClasspath 25  the RESOLVER rejects paper-api 26.x for a consumer declaring less,
//                        before javac ever runs, so options.release cannot reach this.
//   options.release      what javac EMITS, and the only one of the four that decides whether a
//                        Java 8 server can load the class.
//   source/targetCompat  what the outgoing variant DECLARES to consumers, as
//                        org.gradle.jvm.version. On Gradle 9 options.release feeds this too, so
//                        these are belt and braces rather than the only source: removing both still
//                        produced jvm.version 8. Kept because they also set the value for tooling
//                        that reads the extension directly, and because being explicit here is what
//                        the assertion at the bottom of this file checks against.
val floor = (project.extra["moduleFloor"] as Number).toInt()

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}
configurations.named("compileClasspath").configure {
    attributes {
        attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }
}

// The shadow plugin derives org.gradle.jvm.version for its outgoing variant from the TOOLCHAIN, not
// from targetCompatibility, and neither options.release nor the floor above feeds it. So a module
// that emits Java 8 was publishing metadata that said 25, and NO Gradle consumer below 25 could
// resolve it. That is not hypothetical for this project: spigot-jar has shipped that way since
// alpha.37, which makes the "1.8.x servers can use this" claim untrue for every Gradle consumer.
//
// Only takes effect inside afterEvaluate, because the shadow plugin writes the attribute after us.
// The fat jar legitimately contains higher bytecode (relocated HikariCP at 11, and the NMS version
// modules up to 25), and that is fine: those are reached reflectively or behind a runtime guard, and
// are never loaded by a server that cannot read them. The variant has to describe what a consumer
// needs in order to LOAD the module, which is the floor.
plugins.withId("com.gradleup.shadow") {
    afterEvaluate {
        configurations.named("shadowRuntimeElements").configure {
            attributes {
                attribute(
                    org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
                    floor
                )
            }
        }
    }
}

// The floor governs SHIPPED bytecode. Tests run on the build JVM and are never loaded by a
// Minecraft server, so constraining them buys nothing and costs a lot. JUnit 6 requires Java 17,
// so a floor applied to the test source set makes the parser suite unresolvable.
// Set on compileJava ONLY, not on the java {} extension. The extension applies to every
// JavaCompile in the project including compileTestJava, so the floor was reaching the test source
// set that the comment below says is deliberately unconstrained. Tests compiled at -source 8 today
// only because none of them happens to use newer syntax; the first `var` in a test would have
// failed for a reason the convention explicitly disclaims.
tasks.named<JavaCompile>("compileJava") {
    options.release.set(floor)
    sourceCompatibility = JavaVersion.toVersion(floor).toString()
    targetCompatibility = JavaVersion.toVersion(floor).toString()
}
listOf("testCompileClasspath", "testRuntimeClasspath").forEach { name ->
    configurations.named(name).configure {
        attributes {
            attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
        }
    }
}


// Every module declares a floor. Only spigot-jar gets the full verifyFloor, because only it has a
// shaded jar to inspect. That left the other five with a declared floor and nothing checking that
// the DECLARATION survives to the published metadata.
//
// standalone-jar is the one that matters: it has no src/, so its classes never reach spigot-jar's
// fat jar and it is not covered incidentally the way the other four are. It is published and listed
// as a download in the release workflow. Deleting its two floor lines made it publish
// jvm.version 17, with nothing in the repo noticing.
//
// So assert it here, in the convention every module already applies. Cheap, and it scales to a new
// module for free.
val floorForCheck = floor
plugins.withId("maven-publish") {
    val verifyDeclaredFloor = tasks.register("verifyDeclaredFloor") {
        group = "verification"
        description = "Fails if this module's published metadata stops declaring its Java floor."

        val metadataTasks = tasks.matching { it.name.startsWith("generateMetadataFileFor") }
        dependsOn(metadataTasks)
        val publicationsDir = layout.buildDirectory.dir("publications")

        doLast {
            val dir = publicationsDir.get().asFile
            val modules = if (dir.isDirectory) {
                dir.walkTopDown().filter { it.name == "module.json" }.toList()
            } else {
                emptyList()
            }
            if (modules.isEmpty()) {
                // No publication for this VERSION, so nothing will be published either.
                logger.lifecycle("verifyDeclaredFloor: no publication for ${project.name}, nothing to check")
                return@doLast
            }
            val wrong = ArrayList<String>()
            var seen = 0
            for (module in modules) {
                Regex("\"org\\.gradle\\.jvm\\.version\"\\s*:\\s*(\\d+)")
                    .findAll(module.readText())
                    .map { it.groupValues[1].toInt() }
                    .forEach { declared ->
                        seen++
                        if (declared != floorForCheck) {
                            wrong.add("${module.parentFile.name}/${module.name} declares $declared")
                        }
                    }
            }
            if (seen == 0) {
                throw GradleException(
                    "found ${modules.size} module.json for ${project.name} but no org.gradle.jvm.version " +
                            "in any of them, so this check would pass whatever the metadata said."
                )
            }
            if (wrong.isNotEmpty()) {
                throw GradleException(
                    "${project.name} compiles to Java $floorForCheck but its published metadata says " +
                            "otherwise, so a Gradle consumer at the floor cannot resolve it:\n  " +
                            wrong.joinToString("\n  ")
                )
            }
            logger.lifecycle("verifyDeclaredFloor: ${project.name} declares $floorForCheck across $seen variant(s)")
        }
    }
    tasks.named("build") { dependsOn(verifyDeclaredFloor) }
    tasks.matching { it.name == "publish" }.configureEach { dependsOn(verifyDeclaredFloor) }
}

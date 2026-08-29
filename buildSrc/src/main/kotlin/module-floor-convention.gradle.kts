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
//   source/targetCompat  what the outgoing variant DECLARES. Gradle derives
//                        org.gradle.jvm.version from this; options.release does not feed it.
//                        Get it wrong and the bytecode is fine while nobody can resolve it.
val floor = (project.extra["moduleFloor"] as Number).toInt()

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    sourceCompatibility = JavaVersion.toVersion(floor)
    targetCompatibility = JavaVersion.toVersion(floor)
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
tasks.named<JavaCompile>("compileJava") { options.release.set(floor) }
listOf("testCompileClasspath", "testRuntimeClasspath").forEach { name ->
    configurations.named(name).configure {
        attributes {
            attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
        }
    }
}

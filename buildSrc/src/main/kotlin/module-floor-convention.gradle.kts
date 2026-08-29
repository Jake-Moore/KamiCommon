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

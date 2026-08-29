plugins {
    id("java")
}

// Applied by every module that compiles against paper-api 26.x.
// Three settings doing three different jobs: javac must RUN on 25 to read class-file major 69,
// the resolver must ASK for 25 because paper-api declares org.gradle.jvm.version=25, and the
// output must stay at 21 so this library keeps loading on pre-26 JVMs.
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
configurations.named("compileClasspath").configure {
    attributes {
        attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }
}
tasks.withType<JavaCompile>().configureEach { options.release.set(21) }

plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

// A 1.8.8 server loads this in full.
// See buildSrc/src/main/kotlin/module-floor-convention.gradle.kts for what each setting does.
extra["moduleFloor"] = 8
apply(plugin = "module-floor-convention")


repositories {
    mavenCentral()
}

var snakeYaml = "org.yaml:snakeyaml:2.7"
var json = "org.json:json:20250517"
dependencies {
    api(project(":shared-utils"))
    // Unique dependencies for this module
    api(snakeYaml)
    api(json)

    // Testing Dependencies
    testImplementation(snakeYaml)
    testImplementation(json)
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// The root build disables tests for every project. This module opts back in, because
//  NmsVersionParser decides which NMS implementation every server on every supported
//  version receives, and it had no test when it started throwing on Paper 26.x.
tasks.named<Test>("test") {
    enabled = true
    useJUnitPlatform()
    testLogging { events("passed", "failed") }
}

// Configure javadoc-publish-convention
configure<Javadoc_publish_convention_gradle.JavadocPublishExtension> {
    // standalone-utils includes only shared-utils
    exportedProjects = listOf(
        ":standalone-utils",
        ":shared-utils",
    )
    moduleName = "standalone-utils"
}
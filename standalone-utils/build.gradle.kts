plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

repositories {
    mavenCentral()
}

var snakeYaml = "org.yaml:snakeyaml:2.4"
var json = "org.json:json:20250517"
dependencies {
    api(project(":shared-utils"))
    // Unique dependencies for this module
    api(snakeYaml)
    api(json)

    // Testing Dependencies
    testImplementation(snakeYaml)
    testImplementation(json)
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
plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

dependencies {
    // Unique dependencies for this module
}

// Configure javadoc-publish-convention
configure<Javadoc_publish_convention_gradle.JavadocPublishExtension> {
    // shared-utils includes no other projects
    exportedProjects = listOf(
        ":shared-utils"
    )
    moduleName = "shared-utils"
}
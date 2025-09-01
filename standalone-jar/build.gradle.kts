plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

dependencies {
    // Both shared-jar and standalone-utils inherit from shared-utils
    // We should exclude one of them to avoid duplicate classes
    api(project(":shared-jar"))
    api(project(":standalone-utils")) {
        // Must exclude one copy of shared-utils, since both shared-jar and standalone-utils include it
        exclude(group = "com.kamikazejam.kamicommon", module = "shared-utils")
    }
}

// Configure javadoc-publish-convention
configure<Javadoc_publish_convention_gradle.JavadocPublishExtension> {
    // standalone-utils includes shared-jar AND standalone-utils
    exportedProjects = listOf(
        ":standalone-jar",
        ":standalone-utils",
        ":shared-jar",
        ":shared-utils",
    )
    moduleName = "standalone-jar"
}
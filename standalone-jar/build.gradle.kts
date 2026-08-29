plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

// An aggregator with no bytecode of its own.
// See buildSrc/src/main/kotlin/module-floor-convention.gradle.kts for what each setting does.
extra["moduleFloor"] = 8
apply(plugin = "module-floor-convention")


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
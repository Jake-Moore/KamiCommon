plugins {
    id("javadoc-publish-convention")
    // Unique plugins for this module
}

// A 1.8.8 server loads this in full.
// See buildSrc/src/main/kotlin/module-floor-convention.gradle.kts for what each setting does.
extra["moduleFloor"] = 8
apply(plugin = "module-floor-convention")


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
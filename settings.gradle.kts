plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
rootProject.name = "kamicommon"
include(":spigot-jar")
include(":spigot-utils")
include(":standalone-jar")
include(":standalone-utils")
include(":shared-jar")
include(":shared-utils")

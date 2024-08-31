plugins {
    // Unique plugins for this module
}

val nbtVersion = "de.tr7zw:item-nbt-api:2.13.2"
val xseriesVersion = "com.github.cryptomorin:XSeries:11.2.1"
val particlesVersion = "com.github.fierioziy.particlenativeapi:ParticleNativeAPI-core:4.3.0"
dependencies {
    api(project(":standalone-utils")); shadow(project(":standalone-utils"))

    api(nbtVersion); shadow(nbtVersion)
    api(xseriesVersion); shadow(xseriesVersion)
    api(particlesVersion); shadow(particlesVersion)

    compileOnly(project.property("lowestSpigotDep") as String)
}
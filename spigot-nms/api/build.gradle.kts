plugins {
    // Unique plugins for this module
}

val nbtVersion = "de.tr7zw:item-nbt-api:2.13.2"
val xseriesVersion = "com.github.cryptomorin:XSeries:11.2.1"
val particlesVersion = "com.github.fierioziy.particlenativeapi:ParticleNativeAPI-core:4.3.0"
dependencies {
    api(project(":standalone-utils")); implementation(project(":standalone-utils"))

    api(nbtVersion); implementation(nbtVersion)
    api(xseriesVersion); implementation(xseriesVersion)
    api(particlesVersion); implementation(particlesVersion)

    compileOnly(project.property("lowestSpigotDep") as String)
}
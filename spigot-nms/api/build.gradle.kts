plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":standalone-utils"))
    compileOnly(project.property("lowestSpigotDep") as String)
    api("com.github.cryptomorin:XSeries:9.8.1")

    // Lombok
    compileOnly(project.property("lombokDep") as String)
    annotationProcessor(project.property("lombokDep") as String)
    testAnnotationProcessor(project.property("lombokDep") as String)


    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8
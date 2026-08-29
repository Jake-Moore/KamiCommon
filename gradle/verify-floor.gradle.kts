import java.io.DataInputStream
import java.util.zip.ZipFile

// KamiCommon declares every module at Java 8 so a 1.8.x server can load it. Nothing checked that.
//
// The sibling project has four such checks. This one had none, which matters more here, because THIS
// is the repo with Renovate configured to auto-merge non-major dependency updates. A shaded library
// raising its own bytecode above Java 8 would merge itself, ship, and only surface as
// UnsupportedClassVersionError on somebody's 1.8.8 server.
//
// Three assertions:
//   A. Ceiling.    Every class in the published jar is within the floor of the area it belongs to.
//   B. Metadata.   The published variant declares the floor. The shadow plugin derives that
//                  attribute from the TOOLCHAIN, so it said 25 while the bytecode was 8, and no
//                  Gradle consumer below 25 could resolve the artifact at all.
//   C. Artifact.   The jar inspected here is the one consumers receive.

val BASE_FLOOR = 8

// Areas allowed above the base floor, and why each is safe. Anything NOT listed must meet the base
// floor: an unrecognised package is "must be Java 8", never "unknown, skip". A table that skips what
// it does not recognise cannot fail, and a new shaded dependency would exempt itself.
val areaCeilings = mapOf(
    // Relocated HikariCP. Java 11, reached only through Database, which calls requireJava11() before
    // touching it so a Java 8 server gets a sentence instead of a class-loader Error.
    "com/kamikazejam/kamicommon/hikari/" to 11,
    // The NMS version modules from spigot-nms. Each targets the JVM its own Minecraft version
    // required, up to 25 for 26.x, and :core resolves them by name so an old server never loads one
    // it cannot read. This is spigot-nms's own contract, enforced by verifyFloors over there.
    "com/kamikazejam/kamicommon/nms/" to 25,
)

fun majorFor(floor: Int) = floor + 44

/** Class-file major version. Header only, nothing is loaded. */
fun majorOf(bytes: ByteArray): Int = DataInputStream(bytes.inputStream()).use {
    require(it.readInt() == -0x35014542) { "not a class file" }
    it.readUnsignedShort()
    it.readUnsignedShort()
}

val verifyFloor = tasks.register("verifyFloor") {
    group = "verification"
    description = "Checks the published jar's bytecode and metadata against the declared Java floor."
    dependsOn(tasks.named("shadowJar"), tasks.named("generateMetadataFileForShadowPublication"))

    val jarFile = tasks.named<Jar>("shadowJar").flatMap { it.archiveFile }
    val classifier = tasks.named<Jar>("shadowJar").flatMap { it.archiveClassifier }
    val moduleFile = layout.buildDirectory.file("publications/shadow/module.json")
    val declaredFloor = (project.extra["moduleFloor"] as Number).toInt()

    doLast {
        // C. The jar checked below must be the one consumers receive. If shadowJar grows a
        // classifier, the shaded jar publishes as -all and the primary artifact becomes the thin jar.
        if (classifier.get().isNotEmpty()) {
            throw GradleException(
                "shadowJar has classifier '${classifier.get()}', so the shaded jar is not the primary " +
                        "published artifact and consumers would receive the thin one."
            )
        }

        var inspected = 0
        val violations = ArrayList<String>()
        val areaSeen = HashMap<String, Int>()

        ZipFile(jarFile.get().asFile).use { zip ->
            zip.entries().asSequence()
                .filter { it.name.endsWith(".class") }
                // Multi-release entries are governed by their own version directory. A JVM at the
                // base floor never reads them, so they are not a violation of it.
                .filterNot { it.name.startsWith("META-INF/versions/") }
                .forEach { entry ->
                    val major = majorOf(zip.getInputStream(entry).readBytes())
                    inspected++
                    val area = areaCeilings.keys.firstOrNull { entry.name.startsWith(it) }
                    val ceiling = if (area != null) areaCeilings.getValue(area) else BASE_FLOOR
                    if (area != null) areaSeen[area] = (areaSeen[area] ?: 0) + 1
                    if (major > majorFor(ceiling)) {
                        violations.add(
                            "${entry.name} is major $major (Java ${major - 44}) but its ceiling is Java $ceiling"
                        )
                    }
                }
        }

        // A walker that matches nothing passes forever.
        if (inspected < 5000) {
            throw GradleException("verifyFloor only inspected $inspected classes, far below what this jar contains")
        }
        // An area that stops matching silently drops its exemption AND stops being checked. Both
        // are listed because both are known to be present today.
        val emptyAreas = areaCeilings.keys - areaSeen.keys
        if (emptyAreas.isNotEmpty()) {
            throw GradleException(
                "these areas are exempted above the base floor but matched no classes, so either they " +
                        "were relocated somewhere else or the exemption is now dead:\n  " +
                        emptyAreas.joinToString("\n  ")
            )
        }
        if (violations.isNotEmpty()) {
            throw GradleException(
                "these classes exceed the Java floor their area is allowed, so a server at the floor " +
                        "cannot load them:\n  " + violations.take(20).joinToString("\n  ") +
                        "\nEither lower the bytecode, or if this is a new shaded dependency, decide " +
                        "deliberately whether it belongs behind a runtime guard like Database's."
            )
        }

        // B. Metadata. EVERY declaration, not the first.
        val module = moduleFile.get().asFile
        if (!module.exists()) { throw GradleException("expected Gradle module metadata at $module") }
        val declared = Regex("\"org\\.gradle\\.jvm\\.version\"\\s*:\\s*(\\d+)")
            .findAll(module.readText()).map { it.groupValues[1].toInt() }.toList()
        if (declared.isEmpty()) { throw GradleException("no org.gradle.jvm.version found in $module") }
        val wrong = declared.filter { it != declaredFloor }
        if (wrong.isNotEmpty()) {
            throw GradleException(
                "published metadata declares org.gradle.jvm.version=${wrong.joinToString()} across " +
                        "${declared.size} variant(s), expected $declaredFloor. The bytecode may be " +
                        "perfect, but no Gradle consumer below that can resolve this module, which is " +
                        "exactly how this artifact shipped unusable to Java 8 consumers before."
            )
        }

        logger.lifecycle(
            "verifyFloor: $inspected classes, none above their area's ceiling, metadata declares " +
                    "$declaredFloor, shaded jar is the primary artifact"
        )
    }
}
tasks.named("build") { dependsOn(verifyFloor) }
tasks.named("publish") { dependsOn(verifyFloor) }

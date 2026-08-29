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

// Every class allowed above the base floor, pinned by name in gradle/above-floor-inventory.txt.
//
// This replaced a two-entry prefix table. "com/kamikazejam/kamicommon/nms/" exempted 1401 classes so
// that 160 could sit above the floor, and the other 1241 included the always-loaded dispatch layer,
// nms/abstraction and nms/provider, which a 1.8.8 server loads unconditionally. A spigot-nms release
// raising one of those above Java 8 passed the check and died on the server. Demonstrated, not
// assumed: a Java 21 class dropped at nms/abstraction/ built green, and the byte-identical class at
// util/ failed.
//
// A name rule cannot replace the prefix either. The version markers are irregular:
// Teleporter1_21_9, Teleporter1_20_CB, WorldGuard7 and ModernVersionedComponent all belong above the
// floor and match no pattern that _LATEST or _1_17_R1 do.
val inventoryFile = rootProject.file("gradle/above-floor-inventory.txt")
val aboveFloor: Map<String, Int> = inventoryFile.readLines()
    .map { it.substringBefore('#').trim() }
    .filter { it.isNotEmpty() }
    .associate { line ->
        val name = line.substringBefore('=').trim()
        val version = line.substringAfter('=').trim().toIntOrNull()
            ?: throw GradleException("bad line in ${inventoryFile.name}: $line")
        name to version
    }
if (aboveFloor.size < 200) {
    throw GradleException(
        "${inventoryFile.name} parsed to only ${aboveFloor.size} entries. It should list every class " +
                "above Java 8 in the jar, and a table that parsed to almost nothing would fail every " +
                "one of them rather than checking anything."
    )
}

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
    dependsOn(tasks.named("shadowJar"))
    // The metadata task only exists when a publication does, and javadoc-publish-convention skips
    // creating one for a VERSION outside the three publishable formats. That is the normal case for
    // an ad-hoc local build, and depending on the task unconditionally turned a warn-and-continue
    // into UnknownTaskException on `gradlew build`.
    val metadataTask = tasks.matching { it.name == "generateMetadataFileForShadowPublication" }
    dependsOn(metadataTask)

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
        val present = HashSet<String>()

        ZipFile(jarFile.get().asFile).use { zip ->
            zip.entries().asSequence()
                .filter { it.name.endsWith(".class") }
                // Multi-release entries are governed by their own version directory. A JVM at the
                // base floor never reads them, so they are not a violation of it.
                .filterNot { it.name.startsWith("META-INF/versions/") }
                .forEach { entry ->
                    val major = majorOf(zip.getInputStream(entry).readBytes())
                    inspected++
                    val ceiling = aboveFloor[entry.name] ?: BASE_FLOOR
                    if (aboveFloor.containsKey(entry.name)) present.add(entry.name)
                    if (major > majorFor(ceiling)) {
                        violations.add(
                            "${entry.name} is major $major (Java ${major - 44}) but is allowed only Java $ceiling"
                        )
                    }
                }
        }

        // A walker that matches nothing passes forever.
        if (inspected < 5000) {
            throw GradleException("verifyFloor only inspected $inspected classes, far below what this jar contains")
        }
        // An entry for a class that is no longer in the jar is dead weight that could later
        // exempt something unrelated with the same name, so it fails too. The inventory is only
        // trustworthy if it stays exactly the set that needs it.
        val stale = aboveFloor.keys - present
        if (stale.isNotEmpty()) {
            throw GradleException(
                "${inventoryFile.name} lists ${stale.size} class(es) that are not in the jar, so the " +
                        "inventory no longer describes what ships:\n  " +
                        stale.sorted().take(20).joinToString("\n  ") +
                        "\nRegenerate it and read the diff."
            )
        }
        if (violations.isNotEmpty()) {
            throw GradleException(
                "these classes exceed the Java floor their area is allowed, so a server at the floor " +
                        "cannot load them:\n  " + violations.take(20).joinToString("\n  ") +
                        "\nIf this is a new shaded dependency, decide deliberately whether it belongs " +
                        "behind a runtime guard like Database's. If it is a version-specific NMS class, " +
                        "add it to ${inventoryFile.name}. If it is neither, it is a regression: an " +
                        "always-loaded class cannot exceed Java 8."
            )
        }

        // B. Metadata. EVERY declaration, not the first.
        val module = moduleFile.get().asFile
        if (!module.exists()) {
            // No publication was created, so there is no metadata to check and nothing will be
            // published either. The bytecode assertions above still ran, which is the part that
            // matters for a local build. `publish` cannot reach here: it needs the publication.
            logger.lifecycle(
                "verifyFloor: $inspected classes, ${aboveFloor.size} pinned above the floor and all " +
                        "present. No publication for this VERSION, so the metadata check was skipped."
            )
            return@doLast
        }
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
            "verifyFloor: $inspected classes, ${aboveFloor.size} pinned above the floor and all present, " +
                    "metadata declares " +
                    "$declaredFloor, shaded jar is the primary artifact"
        )
    }
}
tasks.named("build") { dependsOn(verifyFloor) }
tasks.named("publish") { dependsOn(verifyFloor) }

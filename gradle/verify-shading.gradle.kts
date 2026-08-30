import java.util.zip.ZipFile

// KamiCommon ships as one uber jar that a server loads next to other plugins, so every third-party
// class in it must be relocated. Nothing checked that, and two things had already broken:
//
//   1. Twenty classes shipped unrelocated. The rule covers io.lettuce.core, and lettuce 7 added the
//      sibling packages io.lettuce.authx and redis.clients.authentication, which no rule matched.
//      A relocate list is a blacklist: it can only cover what somebody remembered to name.
//   2. Not one META-INF/services file was rewritten, because the build never called
//      mergeServiceFiles(). All seven pointed at classes that do not exist in the jar. The relocated
//      slf4j therefore found no provider and logged nothing at all, and the only reason MySQL still
//      worked is that Database.detectDataSourceClassName() loads the driver by name instead of
//      through ServiceLoader. A broken registration is silent: ServiceLoader skips what it cannot
//      find, so this survived every build, every test and every server in the matrix.
//
// Two assertions, both allow-list shaped, because a blacklist only catches the leak you predicted:
//   A. Namespace.  Every class in the jar is under com/kamikazejam/kamicommon/. No exceptions.
//   B. Services.   Every implementation named in META-INF/services resolves to a class in the jar.
//                  This catches an unrewritten file name too: if the file is still called
//                  io.lettuce.core.json.JsonParser then its contents are unrewritten as well, and
//                  the class they name is gone.
//   C. Logger key. simplelogger.properties configures the relocated slf4j-simple, and shadow rewrites
//                  the property-name constants inside the class but not this file. A key written at
//                  the wrong prefix is read by nobody and changes nothing, so the prefix in the file
//                  is checked against the one actually compiled into SimpleLoggerConfiguration.

val OWN_NAMESPACE = "com/kamikazejam/kamicommon/"

val verifyShading = tasks.register("verifyShading") {
    group = "verification"
    description = "Checks that every third-party class is relocated and every service registration resolves."
    dependsOn(tasks.named("shadowJar"))

    val jarFile = tasks.named<Jar>("shadowJar").flatMap { it.archiveFile }

    doLast {
        var inspected = 0
        var serviceFiles = 0
        var serviceEntries = 0
        val unrelocated = ArrayList<String>()
        val danglingServices = ArrayList<String>()
        val classNames = HashSet<String>()
        val services = LinkedHashMap<String, List<String>>()

        ZipFile(jarFile.get().asFile).use { zip ->
            val entries = zip.entries().asSequence().toList()

            entries.filter { it.name.endsWith(".class") }.forEach { entry ->
                // A multi-release class is governed by the same relocation rules; strip the prefix
                // and judge the path underneath it.
                val path = entry.name.replace(Regex("^META-INF/versions/\\d+/"), "")
                inspected++
                classNames.add(path.removeSuffix(".class").replace('/', '.'))
                if (!path.startsWith(OWN_NAMESPACE)) {
                    unrelocated.add(entry.name)
                }
            }

            entries.filter { it.name.contains("META-INF/services/") && !it.isDirectory }.forEach { entry ->
                serviceFiles++
                val impls = zip.getInputStream(entry).bufferedReader().readLines()
                    .map { it.substringBefore('#').trim() }
                    .filter { it.isNotEmpty() }
                services[entry.name] = impls
            }
        }

        // A walker that matches nothing passes forever. Both counts are floors this jar clears by a
        // wide margin, so they fail on a broken scan rather than on a normal change.
        if (inspected < 5000) {
            throw GradleException("verifyShading only inspected $inspected classes, far below what this jar contains")
        }
        if (serviceFiles < 5) {
            throw GradleException(
                "verifyShading found only $serviceFiles META-INF/services files. The shaded " +
                        "dependencies contribute more than that, so the scan did not read the jar."
            )
        }

        services.forEach { (file, impls) ->
            serviceEntries += impls.size
            impls.filterNot { classNames.contains(it) }.forEach { impl ->
                danglingServices.add("$file -> $impl")
            }
        }
        if (serviceEntries == 0) {
            throw GradleException("verifyShading read $serviceFiles service files but no entries out of them")
        }

        if (unrelocated.isNotEmpty()) {
            throw GradleException(
                "these classes ship at their real names, so they collide with any other plugin " +
                        "carrying the same library:\n  " + unrelocated.take(20).joinToString("\n  ") +
                        "\nAdd a relocate() rule for the package. Naming the library's main package " +
                        "is not enough: a release can add a sibling package that no existing rule matches."
            )
        }
        if (danglingServices.isNotEmpty()) {
            throw GradleException(
                "these service registrations name classes that are not in the jar, so ServiceLoader " +
                        "silently finds nothing:\n  " + danglingServices.take(20).joinToString("\n  ") +
                        "\nThis is what an unrewritten META-INF/services looks like after relocation. " +
                        "mergeServiceFiles() in the shadowJar block rewrites both the file names and " +
                        "their contents."
            )
        }

        // C. The logger property prefix in the resource must be the one the relocated class reads.
        val configName = "simplelogger.properties"
        val configClass = "${OWN_NAMESPACE}slf4j/simple/SimpleLoggerConfiguration.class"
        ZipFile(jarFile.get().asFile).use { zip ->
            val classEntry = zip.getEntry(configClass)
            if (classEntry != null) {
                val configEntry = zip.getEntry(configName)
                    ?: throw GradleException(
                        "the relocated slf4j-simple is in the jar but $configName is not, so it logs at " +
                                "its INFO default and prints library chatter to System.err on every server."
                    )
                // The prefix is a string constant in the class, so read it back rather than trusting
                // that the relocate rule and the hand-written key still agree.
                val classText = zip.getInputStream(classEntry).readBytes().toString(Charsets.ISO_8859_1)
                val prefix = Regex("[\\w.]+\\.simpleLogger\\.defaultLogLevel").find(classText)?.value
                    ?: throw GradleException(
                        "no simpleLogger.defaultLogLevel constant found in $configClass, so the property " +
                                "name this check compares against could not be established"
                    )
                val keys = zip.getInputStream(configEntry).bufferedReader().readLines()
                    .map { it.substringBefore('#').trim() }
                    .filter { it.contains('=') }
                    .map { it.substringBefore('=').trim() }
                if (!keys.contains(prefix)) {
                    throw GradleException(
                        "$configName sets ${keys.joinToString()} but the relocated logger reads '$prefix'. " +
                                "The relocation prefix moved and the file did not, so this configuration " +
                                "is inert and the logger silently falls back to INFO."
                    )
                }
                logger.lifecycle("verifyShading: $configName sets '$prefix', which is the key the relocated logger reads")
            }
        }

        logger.lifecycle(
            "verifyShading: $inspected classes all under $OWN_NAMESPACE, $serviceEntries service " +
                    "entries across $serviceFiles files all resolve"
        )
    }
}
tasks.named("build") { dependsOn(verifyShading) }
tasks.named("publish") { dependsOn(verifyShading) }

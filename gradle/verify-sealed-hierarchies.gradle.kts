import java.io.DataInputStream

// Six hierarchies lost `sealed` when this module dropped to Java 8, because sealed types are Java 17
// and have no Java 8 spelling. Nine places in the source say the closed set is "still enforced within
// the library by the verifySealedHierarchies build task".
//
// That task did not exist. The claim was written alongside the un-sealing and never implemented, so
// the compensating control for losing a compiler guarantee was a sentence in a comment. Worse, the
// runtime messages point an operator at it: ItemBuilder throws "Patch is a closed hierarchy enforced
// by the verifySealedHierarchies build task; add a branch here."
//
// This is that task. It reads the compiled classes rather than the source, so it sees the real
// hierarchy including transitive subclasses and anything generated, and it walks the full closure
// rather than direct implementors only: a class extending SimpleMenu is an AbstractMenu too.
//
// @ApiStatus.NonExtendable still carries the message to consumers. It is an IDE hint and cannot fail
// a build, which is why the "within the library" wording matters and why this only covers our own
// compiled output.

val permitted = mapOf(
    "com/kamikazejam/kamicommon/item/patch/Patch" to setOf(
        "com/kamikazejam/kamicommon/item/patch/PatchAdd",
        "com/kamikazejam/kamicommon/item/patch/PatchRemove",
    ),
    "com/kamikazejam/kamicommon/item/IBuilder" to setOf(
        "com/kamikazejam/kamicommon/item/ItemBuilder",
    ),
    "com/kamikazejam/kamicommon/menu/AbstractMenu" to setOf(
        "com/kamikazejam/kamicommon/menu/SimpleMenu",
        "com/kamikazejam/kamicommon/menu/PaginatedMenu",
        "com/kamikazejam/kamicommon/menu/OneClickMenu",
    ),
    "com/kamikazejam/kamicommon/menu/AbstractMenuBuilder" to setOf(
        "com/kamikazejam/kamicommon/menu/SimpleMenu\$Builder",
        "com/kamikazejam/kamicommon/menu/PaginatedMenu\$Builder",
        "com/kamikazejam/kamicommon/menu/OneClickMenu\$Builder",
    ),
    "com/kamikazejam/kamicommon/menu/api/icons/interfaces/modifier/MenuIconModifier" to setOf(
        "com/kamikazejam/kamicommon/menu/api/icons/interfaces/modifier/StaticIconModifier",
        "com/kamikazejam/kamicommon/menu/api/icons/interfaces/modifier/StatefulIconModifier",
    ),
    "com/kamikazejam/kamicommon/menu/api/struct/size/MenuSize" to setOf(
        "com/kamikazejam/kamicommon/menu/api/struct/size/MenuSizeRows",
        "com/kamikazejam/kamicommon/menu/api/struct/size/MenuSizeType",
    ),
)

/** (thisClass, directParents). Constant pool only, nothing is loaded. */
fun readHierarchy(bytes: ByteArray): Pair<String, List<String>> {
    DataInputStream(bytes.inputStream()).use { input ->
        require(input.readInt() == -0x35014542) { "not a class file" }
        input.readUnsignedShort()
        input.readUnsignedShort()
        val count = input.readUnsignedShort()
        val utf8 = HashMap<Int, String>()
        val classes = HashMap<Int, Int>()
        var i = 1
        while (i < count) {
            when (val tag = input.readUnsignedByte()) {
                1 -> utf8[i] = input.readUTF()
                7 -> classes[i] = input.readUnsignedShort()
                8, 16, 19, 20 -> input.skipBytes(2)
                15 -> input.skipBytes(3)
                3, 4, 9, 10, 11, 12, 17, 18 -> input.skipBytes(4)
                5, 6 -> { input.skipBytes(8); i++ }
                else -> throw GradleException("unknown constant pool tag $tag")
            }
            i++
        }
        input.skipBytes(2)
        fun nameAt(idx: Int): String? = classes[idx]?.let { utf8[it] }
        val self = nameAt(input.readUnsignedShort()) ?: throw GradleException("could not read this_class")
        val superName = nameAt(input.readUnsignedShort())
        val interfaceCount = input.readUnsignedShort()
        val parents = ArrayList<String>()
        if (superName != null) parents.add(superName)
        repeat(interfaceCount) { nameAt(input.readUnsignedShort())?.let { parents.add(it) } }
        return self to parents
    }
}

val verifySealedHierarchies = tasks.register("verifySealedHierarchies") {
    group = "verification"
    description = "Fails if a formerly-sealed hierarchy gains an implementation inside this library."
    dependsOn(tasks.named("classes"))

    val classesDir = layout.buildDirectory.dir("classes/java/main")

    doLast {
        val dir = classesDir.get().asFile
        if (!dir.isDirectory) { throw GradleException("no compiled output at $dir") }

        val parentsOf = HashMap<String, List<String>>()
        dir.walkTopDown().filter { it.extension == "class" }.forEach {
            val (self, parents) = readHierarchy(it.readBytes())
            parentsOf[self] = parents
        }
        // A walker that matches nothing would report every hierarchy as empty, which reads as
        // "no rogue implementations" unless the roots are asserted present below.
        if (parentsOf.size < 200) {
            throw GradleException("only read $parentsOf.size classes from $dir, far below this module")
        }

        val problems = ArrayList<String>()
        for ((root, allowed) in permitted) {
            if (root !in parentsOf) {
                problems.add("root $root was not found in the compiled output, so nothing was checked for it")
                continue
            }
            // Full closure, not direct implementors: a subclass of SimpleMenu is an AbstractMenu.
            val descendants = parentsOf.keys.filter { candidate ->
                if (candidate == root) return@filter false
                val seen = HashSet<String>()
                val stack = ArrayDeque(parentsOf[candidate] ?: emptyList())
                var found = false
                while (stack.isNotEmpty() && !found) {
                    val next = stack.removeFirst()
                    if (!seen.add(next)) continue
                    if (next == root) { found = true; break }
                    parentsOf[next]?.forEach { stack.addLast(it) }
                }
                found
            }.toSet()

            (descendants - allowed).sorted().forEach {
                problems.add("$root has an implementation that is not on its permitted list: $it")
            }
            (allowed - descendants).sorted().forEach {
                problems.add("$root lists $it as permitted, but it does not implement it any more")
            }
        }

        if (problems.isNotEmpty()) {
            throw GradleException(
                "these hierarchies lost `sealed` when this module dropped to Java 8, and the closed set " +
                        "is enforced here instead:\n  " + problems.joinToString("\n  ") +
                        "\nEither add the implementation to the permitted list in " +
                        "gradle/verify-sealed-hierarchies.gradle.kts AND add a branch everywhere the " +
                        "hierarchy is dispatched on, or do not add it."
            )
        }
        logger.lifecycle(
            "verifySealedHierarchies: ${permitted.size} closed hierarchies, " +
                    "${permitted.values.sumOf { it.size }} permitted implementations, no others found"
        )
    }
}
tasks.named("build") { dependsOn(verifySealedHierarchies) }

# 🚀 KamiCommon V5 🚀

---

### **Status: Stable** ✅

`release/v5` is the current line, and **5.0.0** is its first official release. The API it ships is the
one to build against.

**Please Note:**
*   The API is **stable**. Breaking changes are reserved for a future major version.
*   New features and fixes land here.
*   Every server from **1.8.8 to 26.2** is supported from a single artifact, and every module targets
    **Java 8**.
*   Full documentation is in the [wiki](https://github.com/Jake-Moore/KamiCommon/wiki).

Coming from `v4`, or from a `v5` prerelease? Start with the
[Migration Guide](https://github.com/Jake-Moore/KamiCommon/wiki/v5-Migration-Guide).
---

### ⚠️ `release/v4` Branch: End of Life (EOL)

The `release/v4` branch is **end-of-life** and **no longer supported**.  
No new features, bug fixes, or security patches will be provided.

`v5` covers everything `v4` did and is now the stable line, so there is no longer a reason to stay on
`v4`. Existing `v4` builds continue to resolve, but nothing further will be published for them.
---

&nbsp;
> ### Releases
> <a href="https://github.com/Jake-Moore/KamiCommon/releases/latest"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/5dfd7c9bb8b81ae5867c81e9a77ee821/raw/kc-release-latest.json" /></a>
> <a href="https://github.com/Jake-Moore/KamiCommon/releases"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/5dfd7c9bb8b81ae5867c81e9a77ee821/raw/kc-prerelease-latest.json" /></a>
> 
> The GitHub releases may be different from the spigot release

> ### API Compatibility
> <a href="https://github.com/Jake-Moore/KamiCommon/"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/5dfd7c9bb8b81ae5867c81e9a77ee821/raw/kc-release-compatibility.json" /></a>
> <a href="https://github.com/Jake-Moore/KamiCommon/"> <img alt="Latest Pre-Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/5dfd7c9bb8b81ae5867c81e9a77ee821/raw/kc-prerelease-compatibility.json" /></a>
>
> The latest API version may not match the latest Minecraft Version.  
> (This is okay, as updates are not always required for every Minecraft version.)

**DEVELOPMENT ROADMAP**: https://github.com/users/Jake-Moore/projects/3

# KamiCommon
**SEE [STRUCTURE.md](./STRUCTURE.md) FOR GRADLE MODULE BREAKDOWN**

- A common library originally intended for Spigot plugin development, expanded for standalone use too.
- The spigot portions of this library aim to support all versions (since 1.8.x) via its sister project [KamiCommonNMS](https://github.com/Jake-Moore/KamiCommonNMS)
  - View the NMS disclaimers here: [NMS Disclaimers](https://github.com/Jake-Moore/KamiCommonNMS?tab=readme-ov-file#disclaimers)

## JavaDoc
- https://docs.jake-moore.dev/KamiCommon/

## Modules
There are 6 common modules, 5 of which can be safely shaded
- The 5 modules that can be shaded are:
  - `shared-utils`, `shared-jar`, `standalone-utils`, `standalone-jar`, `spigot-utils`
- The last module, `spigot-jar`, compiles the spigot plugin and is only intended to be used as an api
  - When using this module, remember to modify the `plugin.yml` to include `KamiCommon` in the `depend:` list

## Java version

**KamiCommon runs on Java 8.** Every module compiles to class-file major 52, so it loads on any
server from 1.8.x upward. That is not a claim about what is *bundled*, it is what the bytecode
targets, and it is checked on every build rather than assumed.

There is one exception.

| what you use | Java it needs | why |
|---|---|---|
| everything else | **8** | matches the oldest server version this library supports |
| `com.kamikazejam.kamicommon.database` | **11** | HikariCP, the connection pool, is Java 11 |

Nothing else in the jar is above Java 8. HikariCP is the only bundled library that is, and `Database`
is the only class that touches it, so the split is confined to that one package. Call it on an older
JVM and you get an `IllegalStateException` saying so, rather than an `UnsupportedClassVersionError`
naming a relocated class you cannot search for.

### For the NMS side

`spigot-nms` ships implementations for every supported Minecraft version, and each targets the JVM
its own version required: Java 8 through 1.16.5, 16 for 1.17, 17 through 1.20.4, 21 from 1.20.5, and
25 for 26.x. They are loaded by name at runtime, so a 1.8.8 server never loads the class built for
26.x, and the jar carries all of them without any one raising the floor for the rest. `/kc
nmsproviders` prints which implementation your server selected for each capability.

### If you are contributing

`shared-utils`, `standalone-utils`, `shared-jar`, `spigot-utils`, `spigot-jar` and `standalone-jar`
are compiled with
`--release 8`. The compiler will tell you, but so that it is not a surprise: no records, no `var`, no
sealed types, no switch expressions or pattern matching, no `List.of`, `Set.of` or `Stream.toList`.
`Jdk8.repeat` and `Jdk8.strip` stand in for the `String` methods added in Java 11.

That constraint is the price of the 1.8.x support in the line above. It is deliberate, and the build
enforces it rather than relying on review.

## Using KamiCommon
### Repository Information
Add the following Repository to your build file.
#### Maven [pom.xml]:
```xml
<repository>
  <id>luxious-public</id>
  <name>Luxious Repository</name>
  <url>https://repo.luxiouslabs.net/repository/maven-public/</url>
</repository>
```
#### Gradle (kotlin) [build.gradle.kts]:
```kotlin
maven {
    name = "luxiousPublic"
    url = uri("https://repo.luxiouslabs.net/repository/maven-public/")
}
```
#### Gradle (groovy) [build.gradle]:
```groovy
maven {
  name "luxiousPublic"
  url "https://repo.luxiouslabs.net/repository/maven-public/"
}
```

### Dependency Information
- **SEE [STRUCTURE.md](./STRUCTURE.md) FOR GRADLE MODULE BREAKDOWN**

Add the following dependency to your build file.  
Replace `{VERSION}` with the version listed at the top of this page.  
Replace `{MODULE}` with the module you want to use (spigot-jar, standalone-jar, etc.)

#### Maven Dependency [pom.xml]
```xml
<dependency>
  <groupId>com.kamikazejam.kamicommon</groupId>
  <artifactId>{MODULE}</artifactId>
  <version>{VERSION}</version>
  <scope>provided</scope> <!-- set to `compile` if shading a util or standalone jar -->
</dependency>
```

#### Gradle Dependency (groovy) [build.gradle]
```groovy
implementation "com.kamikazejam.kamicommon:{MODULE}:{VERSION}"
```

#### Gradle Dependency (kotlin) [build.gradle.kts]
```kotlin
implementation("com.kamikazejam.kamicommon:{MODULE}:{VERSION}")
```

## Features
See the [wiki](https://github.com/Jake-Moore/KamiCommon/wiki)

# 🚀 V5 Branch: Active Development 🚧

---

### **Status: Mostly Stable, Active Development** 🛠️

Welcome to the `release/v5` branch! This is where the **next major version** of the project is currently being built.

**Please Note:**
*   This branch is under **active development**.
*   It is considered **mostly stable** and safe for production use.
*   **Breaking changes may still occur**, but only if strictly necessary.
*   New features and improvements will continue to land here.
*   Once the API is deemed fully stable, prerelease versions will end and the first official release will be **v5.0.0**.
---

### ⚠️ `release/v4` Branch: End of Life (EOL)

The `release/v4` branch is now **end-of-life** and **no longer supported**.  
No new features, bug fixes, or security patches will be provided.

However:
*   `v4` offers a **more stable API surface** for users who do not wish to migrate
    yet or prefer to wait for `v5` stability with version 5.0.0+
*   If you need long-term stability and do not require new features, you may
    continue using `v4` at your own discretion.
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

`api`, `shared-utils`, `standalone-utils`, `spigot-utils` and `spigot-jar` are compiled with
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

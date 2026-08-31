# 🕰️ V4 Branch: Historical Archive 🕰️

---

### 🚧 **Status: End of Life (EOL)** 🚧

This `release/v4` branch now serves purely as a **historical and archival snapshot** of the project's version 4 codebase.

**`v5` is now stable, and `5.0.0` is released.** Active development and maintenance for `v4` ceased
when the first `v5` alpha was published, and all development is on `v5`.

**⚠️ Please be advised: No support, bug fixes, or new features will be provided for this branch.**

`v5` covers everything `v4` did, supports Minecraft `1.8.8` through `26.2` from a single artifact, and
targets Java 8. Existing `v4` builds continue to resolve from the repository, but nothing further will
be published for them.

---

**Upgrading?** The [v4 to v5 Migration Guide](https://github.com/Jake-Moore/KamiCommon/wiki/v5-Migration-Guide) lists every rename, removal and replacement.  
**For the current version, see the `release/v5` branch or the [Releases page](https://github.com/Jake-Moore/KamiCommon/releases/latest).**

&nbsp;
> <a href="https://github.com/Jake-Moore/KamiCommon/releases/latest"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/5dfd7c9bb8b81ae5867c81e9a77ee821/raw/test.json" /></a>
> 
> The GitHub release may be different from the spigot release
> 
**DEVELOPMENT ROADMAP**: https://github.com/users/Jake-Moore/projects/3

# KamiCommon
**SEE [STRUCTURE.md](./STRUCTURE.md) FOR GRADLE MODULE BREAKDOWN**

- A common library originally intended for Spigot plugin development, expanded for standalone use too.
- The spigot portions of this library aim to support all versions (since 1.8.x) via its sister project [KamiCommonNMS](https://github.com/Jake-Moore/KamiCommonNMS)
  - View the NMS disclaimers here: [NMS Disclaimers](https://github.com/Jake-Moore/KamiCommonNMS?tab=readme-ov-file#disclaimers)

## Modules
There are 6 common modules, 5 of which can be safely shaded
- The 5 modules that can be shaded are:
  - `shared-utils`, `shared-jar`, `standalone-utils`, `standalone-jar`, `spigot-utils`
- The last module, `spigot-jar`, compiles the spigot plugin and is only intended to be used as an api
  - When using this module, remember to modify the `plugin.yml` to include `KamiCommon` in the `depend:` list

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

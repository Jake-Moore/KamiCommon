&nbsp;
> <a href="https://github.com/Jake-Moore/KamiCommon/releases/latest"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/5dfd7c9bb8b81ae5867c81e9a77ee821/raw/test.json" /></a>
> 
> The GitHub release may be different from the spigot release

**SEE [STRUCTURE.md](./STRUCTURE.md) FOR GRADLE MODULE BREAKDOWN**

## ⚠️ v4 Announcement ⚠️
In the coming weeks/months I will be dedicating the majority of work for this project towards creating a v4 release.  
V4 will be a complete rewrite of several sections of this project, and will make no attempt at backwards compatibility.  
I will provide basic bug fix support for the v3 branch, but no new features will be added.  

**Soon I will be posting a roadmap for v4, including all the planned features and changes**.
- **TLDR**: v4 aims to rewrite GUIs, stick to semver practices, improve commands, improve usability of the `spigot-utils` module, add global configuration, and much more.


# KamiCommon
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
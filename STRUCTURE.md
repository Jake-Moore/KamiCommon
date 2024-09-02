&nbsp;

**SEE [README.md](./README.md) FOR DEPENDENCY INFORMATION**  
**SEE [WIKI](https://github.com/Jake-Moore/KamiCommon/wiki) FOR FEATURE DOCUMENTATION**

# Module Structure
All 6 modules are published as repository artifacts for use, but only 5 are intended for shading into your project.
- The 5 modules that can be shaded are:
  - `shared-utils`, `shared-jar`, `standalone-utils`, `standalone-jar`, `spigot-utils`
- The last module, `spigot-jar`, compiles the spigot-jar which should not be shaded.

**NMS & Cross Version Compatibility**
- NMS (`net.minecraft.server`) support has been extracted into a sister project ([KamiCommonNMS](https://github.com/Jake-Moore/KamiCommonNMS))
  - This module is included in `spigot-utils` & `spigot-jar`

## Module Hierarchy
![ScreenShot](/docs/screenshots/structure.png)

## Spigot Development
For developers writing spigot plugins.

### [spigot-jar](./spigot-jar)
- The primary module responsible for compiling the spigot plugin jar.
- This module uses relocation for all dependencies (including transitives) to avoid conflicts on the server.

This module inherits: [spigot-utils](#spigot-utils), [shared-jar](#shared-jar)

It also adds the following dependencies:
- `org.apache.httpcomponents.client5:httpclient5`
- `org.apache.httpcomponents.core5:httpcore5`

(⭐) Should **NOT** be shaded ❌
- <span style="text-decoration:underline;">should be added to the server as a plugin, and to projects as an API only</span>


### [spigot-utils](./spigot-utils)
- A developer jar file containing *some* of the spigot APIs from KamiCommon

This module inherits: [shared-utils](#shared-utils), [standalone-utils](#standalone-utils), and [KamiCommonNMS](https://github.com/Jake-Moore/KamiCommonNMS)

It also adds the following dependencies:
- `com.google.code.gson:gson`
- `org.apache.commons:commons-text`

(⭐) **SHOULD** be shaded ✅



## Standalone Development
For developers compiling outside the spigot server environment.

### [standalone-jar](./standalone-jar)
- A standalone jar file containing utilities that do not utilize the spigot-api

This module inherits: [shared-jar](#shared-jar), [standalone-utils](#standalone-utils)

It also adds the following dependencies:
- `com.google.code.gson:gson`

(⭐) **SHOULD** be shaded ✅


### [standalone-utils](./standalone-utils)
- A jar file containing standalone utilities that do not require shaded dependencies

This module inherits: [shared-utils](#shared-utils)

It also adds the following dependencies:
- `org.yaml:snakeyaml`
- `org.json:json`

(⭐) **SHOULD** be shaded ✅
- jar does not function as a spigot plugin
- meant to be integrated (shaded) into your project
- While code is also present in the spigot-jar, relocations are not applied (do not assume it will work with KamiCommon the plugin)



## Shared Development
For developers who want some generic KamiCommon libraries in their project.

### [shared-jar](./shared-jar)
- A jar file containing generic utility classes (with their shaded dependencies)
- Included (transitive dependency) in both `spigot-jar` and `standalone-jar`

This module inherits: [shared-utils](#shared-utils)

It also adds the following dependencies:
- `com.zaxxer:HikariCP`
- `com.mysql:mysql-connector-j`
- `com.rabbitmq:amqp-client`
- `org.slf4j:slf4j-api`
- `org.slf4j:slf4j-simple`
- `io.lettuce:lettuce-core`
- `com.fasterxml.jackson.core:jackson-databind`
- `com.fasterxml.jackson.core:jackson-annotations`

(⭐) **SHOULD** be shaded ✅


### [shared-utils](./shared-utils)
- A jar file containing shared generic utility classes (classes with no dependencies)

This module inherits: none

It also adds the following dependencies:
- none

(⭐) **SHOULD** be shaded ✅


## Warning
- Only the `spigot-jar` module relocates dependencies. This means that developing against `spigot-utils` will develop against non-relocated dependencies.  
- It is highly unlikely that a jar developed with `spigot-utils` without shading will work on a server, regardless if the KamiCommon plugin is installed.
- <span style="text-decoration:underline;">If you intended to use a module other than `spigot-jar`, you need to shade it and its dependencies into your project.</span>
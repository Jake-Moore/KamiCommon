# Module Structure
## Spigot Development
### [spigot-jar](https://github.com/Jake-Moore/KamiCommon/tree/master/spigot-jar)
- The primary module responsible for the spigot plugin jar.
- Contains ALL of
  - [spigot-utils](#spigot-utils)
    - Which contains [standalone-utils](#standalone-utils)
    - Which contains [spigot-nms](#spigot-nms)
  - [generic-jar](#generic-jar)
- (⭐) Should **NOT** be shaded ❌
  - <span style="text-decoration:underline;">should be added to the server as a plugin</span>

### [spigot-utils](https://github.com/Jake-Moore/KamiCommon/tree/master/spigot-utils)
- A **developer** jar file containing *some* of the standalone spigot APIs in KamiCommon
- Contains ALL:
  - [spigot-nms](#spigot-nms)
  - [standalone-utils](#standalone-utils)
- Does not contain the shaded utilities the spigot-jar has
- Meant for developers who want some of the small/frequently used classes, without loading all of the shaded utilities
- (⭐) **CAN** be shaded
  - classes in this module may use the spigot-api, but do not require a plugin to back them

### [spigot-nms](https://github.com/Jake-Moore/KamiCommon/tree/master/spigot-nms)
- A parent module responsible for nms utilities & implementations.
- Wrapper classes (available in [spigot-jar](#spigot-jar)) should be used instead!
- (⭐) Should **NOT** be shaded ❌
    - <span style="text-decoration:underline;">Should not be used at all</span>, there are nms wrapper classes (like NmsManager) in [spigot-jar](#spigot-jar) that should be used instead


## Standalone Development
### [standalone-jar](https://github.com/Jake-Moore/KamiCommon/tree/master/standalone-jar)
- A standalone jar file containing utilities that do not utilize the spigot-api
- Contains ALL of
  - [standalone-utils](#standalone-utils)
  - [generic-jar](#generic-jar)
- Meant for developers who want to use the config system or other utilities in a non-spigot environment
- (⭐) **SHOULD** be shaded ✅
  - jar does not function as a spigot plugin
  - meant to be integrated (shaded) into your project

### [standalone-utils](https://github.com/Jake-Moore/KamiCommon/tree/master/standalone-utils)
- A jar file containing standalone utilities that do not require shaded dependencies
- This is a **smaller** and **less feature complete** version of the full [standalone-jar](#standalone-jar)
- (⭐) **CAN** be shaded
  - Developers should only use this if they are NOT using [standalone-jar](#standalone-jar)
  - These classes do not require any backing, they are safe to shade

## Generic Development
### [generic-jar](https://github.com/Jake-Moore/KamiCommon/tree/master/generic-jar)
- A jar file containing generic utility classes (with their shaded dependencies)
- Placed in its own module so it can be included in both other -jar modules
- (⭐) **CAN** be shaded
  - Also present in either [spigot-jar](#spigot-jar) or [standalone-jar](#standalone-jar)

## TLDR
- two -jar modules contain the full set of utilities for their respective environment
  - [spigot-jar](#spigot-jar) meant to run as a spigot plugin
  - [standalone-jar](#standalone-jar) meant to be shaded into any kind of project
- generic-jar module contains a set of utilities that can be used in either environment
  - it is included in both the [spigot-jar](#spigot-jar) and [standalone-jar](#standalone-jar) modules
  - It can be used on its own if desired
- both -utils modules contain a minimized set of utilities
  - They avoid large utilities that require shaded dependencies (those are left in the -jar module)
  - you can use them if you shade them
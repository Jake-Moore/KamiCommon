**SEE [README.md](./README.md) FOR API DOCUMENTATION**

# Module Structure
- Modules Available as Dependencies: [spigot-jar](#spigot-jar), [spigot-utils](#spigot-utils), [standalone-jar](#standalone-jar), [standalone-utils](#standalone-utils), [generic-jar](#generic-jar)

## Spigot Development
### [spigot-jar](./spigot-jar)
- The primary module responsible for the spigot plugin jar.
- Contains ALL of
  - [spigot-utils](#spigot-utils)
    - Which contains [standalone-utils](#standalone-utils)
    - Which contains [spigot-nms](#spigot-nms)
  - [generic-jar](#generic-jar)
- 📄 Shaded Utilities
  - com.google.code.gson:gson, org.apache.commons:commons-text, [de.tr7zw:item-nbt-api](https://github.com/tr7zw/Item-NBT-API) [via [spigot-utils](#spigot-utils)]
  - org.json:json, org.yaml:snakeyaml [via [standalone-utils](#standalone-utils)]
  - [com.github.cryptomorin:XSeries](https://github.com/CryptoMorin/XSeries) [via [spigot-nms](#spigot-nms)]
  - com.zaxxer:HikariCP, com.mysql:mysql-connector-j, redis.clients:jedis, com.rabbitmq:amqp-client [via [generic-jar](#generic-jar)]
- (⭐) Should **NOT** be shaded ❌
  - <span style="text-decoration:underline;">should be added to the server as a plugin</span>

### [spigot-utils](./spigot-utils)
- A **developer** jar file containing *some* of the standalone spigot APIs in KamiCommon
- Contains ALL:
  - [spigot-nms](#spigot-nms)
  - [standalone-utils](#standalone-utils)
- Does not contain the shaded utilities the spigot-jar has
- Meant for developers who want some of the small/frequently used classes, without loading all of the shaded utilities
- 📄 Shaded Utilities
  - org.json:json
  - com.google.code.gson:gson
  - org.apache.commons:commons-text
  - [de.tr7zw:item-nbt-api](https://github.com/tr7zw/Item-NBT-API)
  - [com.github.cryptomorin:XSeries](https://github.com/CryptoMorin/XSeries) [via [spigot-nms](#spigot-nms)]
  - org.json:json, org.yaml:snakeyaml [via [standalone-utils](#standalone-utils)]
- (⭐) **CAN** be shaded
  - classes in this module may use the spigot-api, but do not require a plugin to back them

### [spigot-nms](./spigot-nms)
- A parent module responsible for nms utilities & implementations.
- Wrapper classes (available in [spigot-jar](#spigot-jar)) should be used instead!
- 📄 Shaded Utilities
  - [com.github.cryptomorin:XSeries](https://github.com/CryptoMorin/XSeries)
  - org.json:json, org.yaml:snakeyaml [via [standalone-utils](#standalone-utils)]
- ❌ Not Available
    - This module is not available on its own
    - The quickest way to use this module is via [spigot-utils](#spigot-utils)

## Standalone Development
### [standalone-jar](./standalone-jar)
- A standalone jar file containing utilities that do not utilize the spigot-api
- Contains ALL of
  - [standalone-utils](#standalone-utils)
  - [generic-jar](#generic-jar)
- Meant for developers who want to use the config system or other utilities in a non-spigot environment
- 📄 Shaded Utilities
  - org.json:json
  - com.google.code.gson:gson
  - org.json:json, org.yaml:snakeyaml [via [standalone-utils](#standalone-utils)]
  - com.zaxxer:HikariCP, com.mysql:mysql-connector-j, redis.clients:jedis [via [generic-jar](#generic-jar)]
- (⭐) **SHOULD** be shaded ✅
  - jar does not function as a spigot plugin
  - meant to be integrated (shaded) into your project

### [standalone-utils](./standalone-utils)
- A jar file containing standalone utilities that do not require shaded dependencies
- This is a **smaller** and **less feature complete** version of the full [standalone-jar](#standalone-jar)
- 📄 Shaded Utilities
  - org.yaml:snakeyaml
  - org.json:json
- (⭐) **CAN** be shaded
  - Developers should only use this if they are NOT using [standalone-jar](#standalone-jar)
  - These classes do not require any backing, they are safe to shade

## Generic Development
### [generic-jar](./generic-jar)
- A jar file containing generic utility classes (with their shaded dependencies)
- Placed in its own module so it can be included in both other -jar modules
- 📄 Shaded Utilities
  - com.zaxxer:HikariCP
  - com.mysql:mysql-connector-j
  - redis.clients:jedis
  - com.rabbitmq:amqp-client
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
&nbsp;
> <a href="https://github.com/Jake-Moore/KamiCommon/releases/latest"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/5dfd7c9bb8b81ae5867c81e9a77ee821/raw/test.json" /></a>
> 
> The GitHub release may be different from the spigot release

**SEE [STRUCTURE.md](./STRUCTURE.md) FOR GRADLE MODULE BREAKDOWN**

# KamiCommon
- A common library originally intended for Spigot plugin development, expanded for standalone use too.
- The spigot portions of this library aim to support all versions (since 1.8.x), contact me if anything does not.
  - **Note:** it is assumed that anyone running 1.17+ will be using **Paper**, not plain Spigot
  - **Note:** Must use at least Java 11 (even for older versions like 1.8)
  - Contact Info (Discord Username): `kamikazejam`

## Using the Common
- Before you can use KamiCommon, you have to import it into your project as a dependency.  
- If you are developing an application outside of spigot (what I call 'standalone' here), you can use the `maven-shade-plugin` and the `compile` scope for the dependency.

### Plugin YML!!!
Before you continue, if you're working on a spigot plugin (and not shading) make sure to add `KamiCommon` to your plugin.yml in the `depend:` list

### Repository Information
Add the following Repository to your build file.
#### Maven [pom.xml]:
```xml
<repository>
  <id>luxious-public</id>
  <name>Luxious Repository</name>
  <url>https://nexus.luxiouslabs.net/public</url>
</repository>
```
#### Gradle (kotlin) [build.gradle.kts]:
```kotlin
maven {
    name = "luxiousPublic"
    url = uri("https://nexus.luxiouslabs.net/public")
}
```
#### Gradle (groovy) [build.gradle]:
```groovy
maven {
  name "luxiousPublic"
  url "https://nexus.luxiouslabs.net/public"
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

&nbsp;
&nbsp;

## Features
- Easier inventory management with click callbacks `(spigot-jar)`
``` java
String title = "test";
int rows = 3, slot = 8;
KamiMenu menu = new KamiMenu(title, rows);
menu.addMenuClick(itemstack, (plr, click) -> {
    //code on click
}, slot);
menu.openMenu(player);
```
- ItemBuilders for easier item manipulation `(spigot-utils)`
``` java
XMaterial material = XMaterial.CHEST;
short damage = (short) 0;
int amount = 64;

// Various ways to create an ItemBuilder:
ItemBuilder builder = new ItemBuilder(material);
builder = new ItemBuilder(material, (short) damage);
builder = new ItemBuilder(material, (short) damage);
builder = new ItemBuilder(material, amount, (short) damage);
//builder...
builder.toItemStack();

// IAItemBuilder is an extension of ItemBuilder
//   which adds support for namespacedids as the type
builder = new IAItemBuilder("namespace:id");
```
- **Auto Update** for plugins `(spigot-jar)`
   - This feature requires that each plugin repository using auto update have a configured GitHub action to publish a release for each version
   - Probably best to contact me if you're interested in using this feature with your own plugin
- **Commands Library** for subcommand management (forked from the MassiveCore commands system) `(spigot-utils)`
  - WIP Documentation (see MassiveCore development guide for now)
  - Remember to call `SpigotUtilProvider.setPlugin`
- **Config Management** `(spigot-utils)` & `(standalone-utils)`
  - Java Classes (They work on their own, and you can extend if additional features are needed.)
    - KamiConfig `(spigot-utils)`
    - StandaloneConfig `(standalone-utils)`
  - Features
    - Support to save and get ItemStacks (uses spigot config serialization)
    - Fully integrated comments support (loads comments from defaults file, and preserves unique user-generated comments)
    - Native support for creating custom configs and loading defaults from resources
```java
// Example with KamiConfig 
// StandaloneConfig works the same, but doesn't require a plugin, 
//   and fetches the defaults stream from the jar
public class Config extends KamiConfig {
    public Config(JavaPlugin plugin, File file) {
        // When addDefaults is true, KamiConfig will look for
        //   a resource file with the same name as file, and load defaults
        super(plugin, file, true);

        // You can also add defaults manually
        addDefaults();
        save();
    }

    private void addDefaults() {
        // Add defaults manually
        addDefault("some.key", "some-value");
    }
}
```
- **Utility Classes (shaded & relocated)**
  - org.yaml.snakeyaml `(standalone-utils)` [~335 KB]
  - org.json `(spigot-utils)` [~78 KB]
  - redis.clients.jedis `(generic-jar)` [< 888 KB]
  - org.apache.httpcomponents.client5 `(spigot-jar)` [~861 KB]
  - com.google.code.gson `(spigot-utils)` [~284 KB]
  - com.zaxxer.hikari `(generic-jar)` [~162 KB]
  - com.mysql:mysql-connector-j `(generic-jar)` [~2.5 MB]
  - [NBT-API](https://github.com/tr7zw/Item-NBT-API) `(spigot-utils)` [~158 KB]
  - [XSeries](https://github.com/CryptoMorin/XSeries) `(spigot-nms)` [~405 KB]
  - ❗ Note: review [STRUCTURE.md](./STRUCTURE.md) for the module hierarchy
    - A lot of these libraries are also available in other modules (via shading)
- **Utility Classes (other)**
  - DiscordWebhook (for sending simple webhooks) `(standalone-jar)`
  - StringUtil (spigot-independant translation from & to §, and other useful string / string-list methods) `(standalone-utils)`
  - StringUtilP (spigot and PlaceholderAPI-dependant expansion of StringUtil. Includes .p(...) methods for parsing PAPI placeholders) `(spigot-utils)`
- **Version Command** `(spigot-jar)`
   - Java Class: `KamiCommandVersion` 
     - Written using internal commands structure (similar to MassiveCore commands)
   - When you write your core plugin command, you will use `addChild(new KamiCommandVersion())` in its constructor to add this subcommand.
     - Optionally: Use the internal method `VersionControl.sendDetails(getPlugin(), sender);` to trigger version details on your own. 
   - Requires a `version.json` inside your resources folder with the following keys (See below for a sample examples)
&nbsp;
&nbsp;
### Version Control Example: Using **Maven**
`version.json` - new file in your resources folder
```json
{
  "name": "${project.artifactId}",
  "version": "${project.version}",
  "date": "${maven.build.timestamp}"
}
```
`pom.xml` - edit to include resource filtering (will replace details in `version.json` when compiled)
```xml
<!-- Required for version.json (maven-resources-plugin) -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-resources-plugin</artifactId>
    <version>3.3.1</version>
    <configuration>
        <encoding>UTF-8</encoding>
    </configuration>
</plugin>
```
&nbsp;
&nbsp;
### Version Control Example: Using **Gradle**
`version.json` - new file in your resources folder
```json
{
  "name": "${name}",
  "version": "${version}",
  "date": "${date}"
}
```
`build.gradle` - edit to include resource filtering (mimicking maven's plugin)
```Groovy
processResources {
    // Process version file
    // Use DateTimeFormatter ISO_INSTANT (KamiCommon requires this format for the version json)
    filesMatching("**/version.json") {
        expand([name: this.description, version: version, date: DateTimeFormatter.ISO_INSTANT.format(Instant.now())])
    }
}
```

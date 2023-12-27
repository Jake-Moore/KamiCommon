&nbsp;
> <a href="https://github.com/Jake-Moore/KamiCommon/releases/latest"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/5dfd7c9bb8b81ae5867c81e9a77ee821/raw/test.json" /></a>
> 
> The GitHub release may be different from the spigot release

# KamiCommon

- A common library for my (KamikazeJAM) plugins.
- This library aims to support all spigot versions (since 1.8.x), contact me if anything does not.
-  Contact Info (Discord): KamikazeJAM (kamikazejam)


## Using the Common
Before you can use KamiCommon, you have to import it into your project.  
If you are developing an application outside of spigot, you can shade the jar using maven-shade-plugin and by removing the provided scope from the dependency.

### Plugin YML!!!
Before you continue, if you're working on a spigot plugin make sure to add `KamiCommon` to your plugin.yml in the `depend:` list

### Import with Maven
Add the following Repository to your pom.xml
```xml
<repository>
  <id>luxious-public</id>
  <name>Luxious Repository</name>
  <url>https://nexus.luxiouslabs.net/public</url>
</repository>
```
Then add the following dependency  
Replace `{VERSION}` with the version listed at the top of this page.
```xml
<dependency>
  <groupId>com.kamikazejam</groupId>
  <artifactId>kamicommon</artifactId>
  <version>{VERSION}</version>
  <scope>provided</scope>
</dependency>
```
&nbsp;
### Import with Gradle
```kotlin
maven {
    name = "luxiousPublic"
    url = uri("https://nexus.luxiouslabs.net/public")
}
```
Then add the following dependency  
Replace `{VERSION}` with the version listed at the top of this page.
```kotlin
compileOnly 'com.kamikazejam:kamicommon:{VERSION}'
```

&nbsp;
&nbsp;

## Features
- Easier inventory management with click callbacks
``` java
String title = "test";
int rows = 3, slot = 8;
KamiMenu menu = new KamiMenu(title, rows);
menu.addMenuClick(itemstack, (plr, click) -> {
    //code on click
}, slot);
menu.openMenu(player);
```
- ItemBuilders for easier item manipulation
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
- **Auto Update** for plugins
   - This feature requires that each plugin repository using auto update have a configured GitHub action to publish a release for each version
   - Probably best to contact me if you're interested in using this feature with your own plugin
- **Commands Library** for subcommand management (forked from the MassiveCore commands system)
  - WIP Documentation (see MassiveCore development guide for now)
- **Config Management**
  - Java Classes (They work on their own, and you can extend if additional features are needed.)
    - KamiConfig (for spigot plugin configs)
    - StandaloneConfig (for yaml configs outside of spigot)
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
- **Utility Classes (shaded)**
  - org.yaml.snakeyaml (latest)
  - org.json
  - redis.clients.jedis
  - org.apache.httpcomponents
  - [NBT-API](https://github.com/tr7zw/Item-NBT-API)
  - [XSeries](https://github.com/CryptoMorin/XSeries)
- **Utility Classes (other)**
  - DiscordWebhook (for sending simple webhooks)
  - StringUtil (spigot-independant translation from & to §, and other useful string / string-list methods)
  - StringUtilP (spigot and PlaceholderAPI-dependant expansion of StringUtil. Includes .p(...) methods for parsing PAPI placeholders)
- **Version Command**
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

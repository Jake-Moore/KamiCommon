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
- **Commands Library** for subcommand management (I would personally recommend the MassiveCore command system over this, this is very simplistic by comparison)
  - **1.** Create your sub command classes that extend `KamiSubCommand`
    - Override the required methods
    - You can optionally override `performTabComplete` to supply tab completions
    - Make sure to create a "none" sub command, which will act as the help when no sub command is found
  - **2.** Create a sub commands container class that extends `KamiSubCommands`
    - Override `getSubCommands()` and return a list of your sub command instances
    - It is advised to store the "NoneCommand" in a local varaible so it can be supplied to both `getSubCommands()` and `getNoneSubCommand()`
  - **3.** Create a command class that extends `KamiCommand`
    - Use `super(new YourSubCommands())`
    - Override the required methods
    - Do NOT override `onCommand`, this will break all sub commands
    - Optionally override `onTabComplete` to supply tab completions (sub commands)
      - you can access your sub commands with `getKamiSubCommands()`
- **Config Management**
  - Files (create classes extending these, and then create an instance to use)
    - KamiConfig (for plugin configs)
    - StandaloneConfig (for yaml configs outside of spigot)
  - Features
    - Support to save and get ItemStacks (uses spigot config serialization)
    - Full integrated comments support (loads comments from defaults file, and preserves user-generated comments)
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

        // Casts to ItemStack will act normally if you use KamiConfig and provide a plugin object
        // This config system supports environments without spigot classes like ItemStack, so at a base ConfigurationSection level
        //   it cannot return ItemStack or standalone environments would not work, this is the compromise
        ItemStack stack = (ItemStack) getItemStack("key");
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
- **Yaml Management** - YamlHandler and YamlHandlerStandalone (for using .yml files outside of spigot)
  - Utilized internally by AbstractConfig
``` java
YamlHandler yaml = new YamlHandler(File configFile, String fileName);
config = yaml.loadConfig(boolean addDefaults)
yaml.save()
config.save()
config.get(key)
config.put(key, value)
```
- Version command
   - If you are using a KamiCommand implementation, the `KamiSubCommands` constructor takes a boolean for version command
     - When set to true, a sample Version subcommand will be added, and reads from the following file:
   - Requires a `version.json` inside your resources folder with the following keys (See below for a sample maven example, Tip: enable resource filtering)
```json
{
  "name": "${project.artifactId}",
  "version": "${project.version}",
  "date": "${maven.build.timestamp}"
}
```

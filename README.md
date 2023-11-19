> <a href="https://github.com/Jake-Moore/KamiCommon/releases/latest"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/5dfd7c9bb8b81ae5867c81e9a77ee821/raw/test.json" /></a>
> 
> The GitHub release may be different from the spigot release

# KamiCommon

- A common library for my (KamikazeJAM) plugins. (This library should support all spigot versions, let me know if anything does not.)
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
compileOnly 'com.kamikazejam:kamicommon:2.0.0.2'
```

&nbsp;
&nbsp;

## Notable features include:
- Easier inventory management for click events
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
- Auto update for plugins
   - This feature requires that each plugin repository using auto update have a configured GitHub action to publish a release for each version
   - Probably best to contact me if you're interested in using this feature with your own plugin
- Commands library for subcommand management
  - No longer receiving updates, I'm now primarily using MassiveCore commands
- Config Management
  - Files
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
    }

    private void addDefaults() {
        // Add defaults manually
        addDefault("some.key", "some-value");
    }
}
```
- A few utilities
   - All of XSeries, DiscordWebhook utility, StringUtil, and StringUtilP (StringUtil with Placeholder methods)
- YamlHandler and YamlHandlerStandalone (for using .yml files outside of spigot)
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
   - If using KamiCommand implementation, you can specify if you'd like an additional version subcommand
   - Requires a `version.json` inside your resources folder with the following
```json
{
  "name": "${project.artifactId}",
  "version": "${project.version}",
  "date": "${maven.build.timestamp}"
}
```
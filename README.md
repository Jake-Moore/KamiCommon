# KamiCommon

- A common library for my (KamikazeJAM_YT) plugins. (This library should support all spigot versions, let me know if anything does not.)
-  Contact Info (Discord): KamikazeJAM_YT#3713

&nbsp;

## Using the Common
- Either download it from spigot, or you can use the maven repository with your own PAT.
- GitHub packages with Maven requires that all users have a PAT (personal access token) configured, in order to download packages
- If you do not know how to create one up please refer to: https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token
- With your PAT, you must add it to maven, please refer to: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry ("Authenticating with a personal access token")
- The first step towards using the maven dependency is configuring the repository
```xml
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/jake-moore/kamicommon</url>
</repository>
```
- From there you can use the maven dependency for it
```xml
<!-- KamiCommon -->
<dependency>
    <groupId>com.kamikazejamplugins</groupId>
    <artifactId>kamicommon</artifactId>
    <version>1.7.0.2</version>
    <scope>provided</scope>
</dependency>
```

&nbsp;
&nbsp;

## Notable features include:
- Easier inventory management for click events
``` java
String title = "test";
int rows = 3, slot = 8;
KamiMenu<Player> menu = new KamiMenu<>(title, rows);
menu.addMenuClick(itemstack, clickInfo -> {
    //code on click
}, slot);
menu.openMenu(player);
```
- ItemBuilder for easier item manipulation
``` java
XMaterial material = XMaterial.CHEST;
short damage = (short) 0;
int amount = 64;

//Various ways to create an ItemBuilder:
ItemBuilder builder = new ItemBuilder(material);
builder = new ItemBuilder(material, (short) damage);
builder = new ItemBuilder(material, (short) damage);
builder = new ItemBuilder(material, amount, (short) damage);
//builder...
builder.toItemStack();
```
- Auto update for plugins (hard coded for my use only, but feel free to edit the repo and PAT to use it on your own)
   - This feature requires that each plugin repository using auto update have a configured GitHub action to publish a release for each version
   - Probably best to contact me if you're interested in using this feature with your own plugin
- Commands library for easier subcommand management
   - Contact me on discord for more info on this
- A few bulky methods simplified for Configuration management (ConfigManager.java)
   - Create empty configs, save configs, reload configs 
- A few utilities
   - All of XSeries, DiscordWebhook utility, StringUtil.t() as an alias of ChatColor...
- A YamlHandler (for using .yml files outside of spigot, standalone compatible)
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
   - Requires a version.json inside your resources folder with the following
```json
{
  "name": "${project.artifactId}",
  "version": "${project.version}",
  "date": "${maven.build.timestamp}"
}
```

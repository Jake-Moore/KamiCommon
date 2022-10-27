# KamiCommon

A common library for my (KamikazeJAM_YT) plugins. (Plugins made primarily for 1.8.9, no guarantees or support for other versions)
Contact Info (Discord): KamikazeJAM_YT#3713

Noteable features include:
- Easier inventory management for click events
```java
String title = "test";
int rows = 3, slot = 8;
KamiMenu<Player> menu = new KamiMenu<>(title, rows);
menu.addMenuClick(itemstack, clickInfo -> {
    //code on click
}, slot);
```
- ItemBuilder for easier item manipulation
```java
Material material = Material.CHEST;
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
   - This feature requires that each plugin repository using auto update have a configured github action to publish a release for each version
- Commands library for easier subcommand management
   - Contact me on discord for more info on this
- A few bulky methods simplified for Configuration management (ConfigManager.java)
   - Create empty configs, save configs, reload configs 
- A few utilities
   - ActionBar utility, DiscordWebhook utility, StringUtil.t() as an alias of ChatColor...
- A YamlHandler (for using .yml files outside of spigot, standalone compatible)
```java
YamlHandler yaml = new YamlHandler(File configFile, String fileName);
config = yaml.loadConfig(boolean addDefaults)
yaml.save()
config.save()
config.get(key)
config.put(key, value)
```
- Version commad
   - If using KamiCommand implementation, you can specify if you'd like an additional version subcommand
   - Requires a version.json inside your resources folder with the following
```json
{
  "name": "${project.artifactId}",
  "version": "${project.version}",
  "date": "${maven.build.timestamp}"
}
```

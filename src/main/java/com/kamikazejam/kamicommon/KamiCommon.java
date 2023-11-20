package com.kamikazejam.kamicommon;

import com.kamikazejam.kamicommon.gui.MenuManager;
import com.kamikazejam.kamicommon.gui.MenuTask;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

@SuppressWarnings("unused")
public class KamiCommon extends JavaPlugin implements Listener {
    private static KamiCommon plugin;

    @Override
    public void onEnable(){
        long start = System.currentTimeMillis();
        getLogger().info("KamiCommon enabling...");

        plugin = this;
        plugin.getServer().getPluginManager().registerEvents(new MenuManager(), plugin);
        getServer().getPluginManager().registerEvents(this, this);

        // Schedule menu task to run every 1 second
        Bukkit.getScheduler().runTaskTimer(this, new MenuTask(), 0L, 20L);

        if (isWineSpigot()) {
            getLogger().info("WineSpigot (1.8.8) detected!");
        }

        // Create Yaml Loader
        getLogger().info("Creating Yaml Loader");
        KamiCommon.getYaml();

        getLogger().info("KamiCommon enabled in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Plugin) plugin);
        Bukkit.getLogger().info("KamiCommon disabled");
    }

    public static JavaPlugin get() {
        return plugin;
    }


    private static Boolean isWineSpigot = null;
    public static boolean isWineSpigot() {
        if (isWineSpigot == null) {
            return isWineSpigot = Bukkit.getServer().getName().equals("WineSpigot");
        }
        return isWineSpigot;
    }

    private static Yaml yaml = null;
    public static @NotNull Yaml getYaml() {
        if (yaml == null) {
            // Configure LoaderOptions
            LoaderOptions loaderOptions = new LoaderOptions();
            loaderOptions.setProcessComments(true);

            // Configure DumperOptions
            DumperOptions dumperOptions = getDumperOptions();

            // Create a Yaml object with our loading and dumping options
            yaml = (new Yaml(new Constructor(loaderOptions), new Representer(dumperOptions), dumperOptions, loaderOptions));
        }
        return yaml;
    }

    @NotNull
    private static DumperOptions getDumperOptions() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setIndent(2);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setAllowUnicode(true);
        dumperOptions.setProcessComments(true);
        dumperOptions.setPrettyFlow(false); // When Disabled, [] will be used for empty lists instead of [\n]  (Keep Disabled)
        dumperOptions.setSplitLines(false); // When Enabled, string lines might be split into multiple lines   (Keep Disabled)
        return dumperOptions;
    }
}

package com.kamikazejam.kamicommon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kamikazejam.kamicommon.command.type.RegistryType;
import com.kamikazejam.kamicommon.gui.MenuManager;
import com.kamikazejam.kamicommon.gui.MenuTask;
import com.kamikazejam.kamicommon.util.adapter.*;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.collections.KamiMap;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.collections.KamiTreeSet;
import com.kamikazejam.kamicommon.util.id.IdUtil;
import com.kamikazejam.kamicommon.util.mson.Mson;
import com.kamikazejam.kamicommon.util.mson.MsonEvent;
import com.kamikazejam.kamicommon.yaml.standalone.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Modifier;

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
        YamlUtil.getYaml();

        // Setup IdUtil
        IdUtil.setup();

        // Setup RegistryType (Types for Commands)
        RegistryType.registerAll();

        getLogger().info("KamiCommon enabled in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Plugin) plugin);
        Bukkit.getLogger().info("KamiCommon disabled");

        // Save IdUtil
        IdUtil.saveCachefileDatas();
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


    private static Boolean hasItemsAdder = null;
    public static boolean hasItemsAdder() {
        if (hasItemsAdder == null) {
            return hasItemsAdder = Bukkit.getPluginManager().getPlugin("ItemsAdder") != null;
        }
        return hasItemsAdder;
    }



    public static final Gson gson = getKamiCommonGsonBuilder().create();
    public static GsonBuilder getKamiCommonGsonBuilder() {
        // Create
        GsonBuilder ret = new GsonBuilder();

        // Basic Behavior
        ret.setPrettyPrinting();
        ret.disableHtmlEscaping();
        ret.excludeFieldsWithModifiers(Modifier.TRANSIENT);
        ret.excludeFieldsWithModifiers(Modifier.STATIC);

        // Massive Containers
        ret.registerTypeAdapter(KamiList.class, AdapterMassiveList.get());
        ret.registerTypeAdapter(KamiMap.class, AdapterMassiveMap.get());
        ret.registerTypeAdapter(KamiSet.class, AdapterMassiveSet.get());
        ret.registerTypeAdapter(KamiTreeSet.class, AdapterMassiveTreeSet.get());

        // Mson
        ret.registerTypeAdapter(Mson.class, AdapterMson.get());
        ret.registerTypeAdapter(MsonEvent.class, AdapterMsonEvent.get());

        // Return
        return ret;
    }
}

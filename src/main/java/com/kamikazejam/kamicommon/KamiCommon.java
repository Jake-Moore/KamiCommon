package com.kamikazejam.kamicommon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kamikazejam.kamicommon.command.KamiCommonCommandRegistration;
import com.kamikazejam.kamicommon.command.type.RegistryType;
import com.kamikazejam.kamicommon.gui.MenuManager;
import com.kamikazejam.kamicommon.gui.MenuTask;
import com.kamikazejam.kamicommon.util.adapter.*;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.collections.KamiMap;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.collections.KamiTreeSet;
import com.kamikazejam.kamicommon.util.engine.EngineScheduledTeleport;
import com.kamikazejam.kamicommon.util.engine.EngineTeleportMixinCause;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import com.kamikazejam.kamicommon.util.mixin.*;
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
public class KamiCommon extends KamiPlugin implements Listener {
    private static KamiCommon plugin;

    @Override
    public void onEnableInner(){
        long start = System.currentTimeMillis();
        getLogger().info("KamiCommon enabling...");

        plugin = this;
        plugin.getServer().getPluginManager().registerEvents(new MenuManager(), plugin);
        getServer().getPluginManager().registerEvents(this, this);

        // Activate Actives
        EngineScheduledTeleport.get().setActive(this);
        EngineTeleportMixinCause.get().setActive(this);
        MixinPlayed.get().setActive(this);
        MixinDisplayName.get().setActive(this);
        MixinTeleport.get().setActive(this);
        MixinSenderPs.get().setActive(this);
        MixinWorld.get().setActive(this);

        // Schedule menu task to run every 1 second
        Bukkit.getScheduler().runTaskTimer(this, new MenuTask(), 0L, 20L);

        if (isWineSpigot()) {
            getLogger().info("WineSpigot (1.8.8) detected!");
        }

        // Create Yaml Loader
        getLogger().info("Creating Yaml Loader");
        YamlUtil.getYaml();

        // Setup IdUtil
        IdUtilLocal.setup();

        // Setup RegistryType (Types for Commands)
        RegistryType.registerAll();

        // Setup Commands
        new KamiCommonCommandRegistration(this);

        getLogger().info("KamiCommon enabled in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisableInner() {
        // Deactivate Actives
        EngineScheduledTeleport.get().setActive(null);
        EngineTeleportMixinCause.get().setActive(null);
        MixinPlayed.get().setActive(null);
        MixinDisplayName.get().setActive(null);
        MixinTeleport.get().setActive(null);
        MixinSenderPs.get().setActive(null);
        MixinWorld.get().setActive(null);

        // Unregister all listeners
        HandlerList.unregisterAll((Plugin) plugin);

        // Save IdUtil
        IdUtilLocal.saveCachefileDatas();

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



    public static final Gson gson = getKamiCommonGsonBuilder().create();
    public static GsonBuilder getKamiCommonGsonBuilder() {
        // Create
        GsonBuilder ret = new GsonBuilder();

        // Basic Behavior
        ret.setPrettyPrinting();
        ret.disableHtmlEscaping();
        ret.excludeFieldsWithModifiers(Modifier.TRANSIENT);
        ret.excludeFieldsWithModifiers(Modifier.STATIC);

        // KamiCommon Containers
        ret.registerTypeAdapter(KamiList.class, AdapterKamiList.get());
        ret.registerTypeAdapter(KamiMap.class, AdapterKamiMap.get());
        ret.registerTypeAdapter(KamiSet.class, AdapterKamiSet.get());
        ret.registerTypeAdapter(KamiTreeSet.class, AdapterKamiTreeSet.get());

        // Mson
        ret.registerTypeAdapter(Mson.class, AdapterMson.get());
        ret.registerTypeAdapter(MsonEvent.class, AdapterMsonEvent.get());

        // Return
        return ret;
    }
}

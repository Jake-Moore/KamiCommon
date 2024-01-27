package com.kamikazejam.kamicommon;

import com.google.gson.JsonObject;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.KamiCommonCommandRegistration;
import com.kamikazejam.kamicommon.configuration.config.KamiConfig;
import com.kamikazejam.kamicommon.modules.Module;
import com.kamikazejam.kamicommon.modules.ModuleManager;
import com.kamikazejam.kamicommon.modules.integration.CitizensIntegration;
import com.kamikazejam.kamicommon.modules.integration.ItemsAdderIntegration;
import com.kamikazejam.kamicommon.modules.integration.MythicMobsIntegration;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.interfaces.Disableable;
import com.kamikazejam.kamicommon.util.interfaces.Named;
import com.kamikazejam.kamicommon.util.mson.MsonMessenger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public abstract class KamiPlugin extends JavaPlugin implements Listener, Named {
    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    @Getter
    private long enableTime;
    private String logPrefixColored = null;
    private String logPrefixPlain = null;
    @Getter ModuleManager moduleManager;
    @Getter KamiConfig modulesConfig;


    // -------------------------------------------- //
    // ENABLE
    // -------------------------------------------- //

    @Override
    public void onLoad() {
        this.onLoadPre();
        this.onLoadInner();
        this.onLoadPost();
    }

    public void onLoadPre() {
        String[] version = this.getDescription().getVersion().split(" ");
        this.logPrefixColored = Txt.parse("<teal>[<aqua>%s %s<teal>] <i>", this.getDescription().getName(), version[version.length - 1]);
        this.logPrefixPlain = ChatColor.stripColor(this.logPrefixColored);
    }
    public void onLoadInner() {}
    public void onLoadPost() {}


    @Override
    public final void onEnable() {
        if (!this.onEnablePre()) return;
        this.onEnableInner();
        this.onEnablePost();
    }

    public boolean onEnablePre() {
        this.enableTime = System.currentTimeMillis();
        log("=== ENABLE START ===");

        // Create the Modules Config
        this.modulesConfig = new KamiConfig(this, new File(getDataFolder(), "modules.yml"), false);

        // Create the Module Manager
        this.moduleManager = new ModuleManager(this);

        // Listener
        Bukkit.getPluginManager().registerEvents(this, this);

        return true;
    }

    public abstract void onEnableInner();

    public void onEnablePost() {
        // Register module integrations
        if (hasItemsAdder()) {
            new ItemsAdderIntegration(this);
        }
        if (hasCitizens()) {
            new CitizensIntegration(this);
        }
        if (hasMythicMobs()) {
            new MythicMobsIntegration(this);
        }

        long ms = System.currentTimeMillis() - this.enableTime;
        log(Txt.parse("=== ENABLE <g>COMPLETE <i>(Took <h>" + ms + "ms<i>) ==="));
    }

    @Override
    public final void onDisable() {
        // Call Their Disable Method
        onDisableInner();

        // Cleanup any commands that were forgotten about
        // Loop through KamiCommand.getAllInstances() without causing a ConcurrentModificationException
        for (KamiCommand command : new ArrayList<>(KamiCommand.getAllInstances())) {
            command.unregisterCommand();
        }
        try {
            KamiCommonCommandRegistration.updateRegistrations();
        }catch (Throwable ignored) {}

        // Cleanup Listeners, Tasks, and Disableables
        unregisterListener(listeners.toArray(new Listener[0]));
        unregisterTask(taskList.toArray(new BukkitTask[0]));
        unregisterDisableable(disableables.toArray(new Disableable[0]));

        // Cleanup Modules
        if (moduleManager != null) {
            moduleManager.unregister();
        }

        onDisablePost();
        log("Disabled");
    }

    /**
     * Called First in JavaPlugin.onDisable()
     */
    public abstract void onDisableInner();

    /**
     * Called after both onDisableInner and listeners, tasks, and disableables are unregistered<p>
     * Also called after modules and commands are unregistered<p>
     * You can use this method to cleanup databases or anything else that should come after module shutdowns
     */
    public void onDisablePost() {}

    /**
     * Can override if configs are stored in a subpackage of the jar
     */
    public String getModuleYmlPath() {
        return null;
    }

    // -------------------------------------------- //
    // LOGGING
    // -------------------------------------------- //
    public void log(Object... msg) {
        log(Level.INFO, msg);
    }
    public void log(Level level, Object... msg) {
        String imploded = Txt.implode(msg, " ");
        if (level == Level.INFO) {
            MsonMessenger.get().messageOne(Bukkit.getConsoleSender(), this.logPrefixColored + imploded);
        } else {
            Logger.getLogger("Minecraft").log(level, this.logPrefixPlain + imploded);
        }
    }



    // -------------------------------------------- //
    // LISTENER REGISTRATION
    // -------------------------------------------- //
    private final List<Listener> listeners = new ArrayList<>();
    public void registerListener(Listener... listeners) {
        List<Listener> list = Arrays.asList(listeners);
        this.listeners.addAll(list);
        list.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }
    public void unregisterListener(Listener... listeners) {
        List<Listener> list = Arrays.asList(listeners);
        this.listeners.removeAll(list);
        list.forEach(HandlerList::unregisterAll);
    }

    // -------------------------------------------- //
    // DISABLEABLE REGISTRATION
    // -------------------------------------------- //
    private final List<Disableable> disableables = new ArrayList<>();
    public void registerDisableable(Disableable... disableables) {
        this.disableables.addAll(Arrays.asList(disableables));
    }
    public void unregisterDisableable(Disableable... disableables) {
        for (Disableable disableable : disableables) {
            disableable.onDisable();
        }
        this.disableables.removeAll(Arrays.asList(disableables));
    }

    // -------------------------------------------- //
    // TASK REGISTRATION
    // -------------------------------------------- //
    private final List<BukkitTask> taskList = new ArrayList<>();
    public void registerTask(BukkitTask... tasks) {
        this.taskList.addAll(Arrays.asList(tasks));
    }
    public void unregisterTask(BukkitTask... tasks) {
        for (BukkitTask bukkitTask : tasks) {
            Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
        }
        this.taskList.removeAll(Arrays.asList(tasks));
    }



    // -------------------------------------------- //
    // MODULE MANAGEMENT
    // -------------------------------------------- //
    public <T extends Module> void registerModule(Class<T> clazz) {
        Constructor<T> s;
        try {
            s = clazz.getDeclaredConstructor();
            s.setAccessible(true);
            T instance = s.newInstance();
            registerModule(instance);
        } catch (Throwable t) {
            Bukkit.getLogger().severe("Failed to initialize the module: " + clazz.getName());
            t.printStackTrace();
        }
    }
    public void registerModule(Module... modules) {
        for (Module module : modules) {
            getModuleManager().registerModule(module);
        }
    }
    /**
     * @deprecated Use singleton pattern on your modules instead of this!!
     */
    @Deprecated
    public <T extends Module> T getModule(Class<T> clazz) {
        return moduleManager.get(clazz);
    }



    // -------------------------------------------- //
    // STATIC INTEGRATION DETECTION
    // -------------------------------------------- //
    private static Boolean hasItemsAdder = null;
    public static boolean hasItemsAdder() {
        if (hasItemsAdder == null) {
            return hasItemsAdder = Bukkit.getPluginManager().getPlugin("ItemsAdder") != null;
        }
        return hasItemsAdder;
    }

    private static Boolean hasCitizens = null;
    public static boolean hasCitizens() {
        if (hasCitizens == null) {
            return hasCitizens = Bukkit.getPluginManager().getPlugin("Citizens") != null;
        }
        return hasCitizens;
    }

    private static Boolean hasMythicMobs = null;
    public static boolean hasMythicMobs() {
        if (hasMythicMobs == null) {
            return hasMythicMobs = Bukkit.getPluginManager().getPlugin("MythicMobs") != null;
        }
        return hasMythicMobs;
    }

    public interface ErrorPropertiesCallback {
        void onFailure(String pluginName, String minVer);
    }

    // Used in combination with a properties.json file in another plugin's resources
    //   which passes a JsonObject and a json key, along with a target plugin name for version validation
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean verifyPluginVersion(JsonObject o, String key, String pluginName, @Nullable ErrorPropertiesCallback callback) {
        // Fetch properties.json version
        String minVer = o.get(key).getAsString();
        if (minVer == null || minVer.isEmpty()) {
            getLogger().severe("Could not find " + pluginName + " version in properties.json");
            return false;
        }
        return verifyPluginVersion(minVer, pluginName, callback);
    }

    public boolean verifyPluginVersion(String minVer, String pluginName, @Nullable ErrorPropertiesCallback callback) {
        int minVerInt = verToInt(minVer);

        // Fetch target plugin version
        JavaPlugin pl = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        if (pl == null) {
            getLogger().severe("Could not load " + pluginName + " dependency! (Plugin Not Found!)");
            return false;
        }
        int ver = verToInt(pl.getDescription().getVersion());

        // Compare versions
        if (ver < minVerInt) {
            getLogger().severe(pluginName + " version is too old! (" + minVer + " or higher required)");
            if (callback != null) { callback.onFailure(pluginName, minVer); }
            return false;
        }
        getLogger().info(pluginName + " version " + pl.getDescription().getVersion() + " found, met requirement >= " + minVer);
        return true;
    }

    public int verToInt(String ver) {
        // Grab everything before the first dash, if there is one (1.6.1-pr1 -> 1.6.1) (1.6.2-SNAPSHOT -> 1.6.2)
        ver = ver.split("-")[0];
        // Remove everything that isn't a number (1.6.1 -> 161
        return Integer.parseInt(ver.replaceAll("[^\\d]", ""));
    }
}

package com.kamikazejam.kamicommon;

import com.google.gson.JsonObject;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.KamiCommonCommandRegistration;
import com.kamikazejam.kamicommon.configuration.spigot.ConfigObserver;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.modules.Module;
import com.kamikazejam.kamicommon.modules.ModuleManager;
import com.kamikazejam.kamicommon.modules.integration.CitizensIntegration;
import com.kamikazejam.kamicommon.modules.integration.ItemsAdderIntegration;
import com.kamikazejam.kamicommon.modules.integration.MythicMobsIntegration;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.interfaces.Disableable;
import com.kamikazejam.kamicommon.util.interfaces.Named;
import com.kamikazejam.kamicommon.util.log.LoggerService;
import com.kamikazejam.kamicommon.util.log.PluginLogger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings({"unused", "UnusedReturnValue", "DuplicatedCode"})
public abstract class KamiPlugin extends JavaPlugin implements Listener, Named, CoreMethods, ConfigObserver {
    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    @Getter
    private long enableTime;
    private String logPrefixColored = null;
    @Getter ModuleManager moduleManager;
    private KamiConfigExt modulesConfig = null;
    private @Nullable KamiConfigExt config = null;
    @Getter
    private LoggerService colorLogger;
    // CoreMethods Fields
    private final List<Listener> listenerList = new ArrayList<>();
    private final List<BukkitTask> taskList = new ArrayList<>();
    private final List<KamiCommand> commandList = new ArrayList<>();
    private final List<Disableable> disableableList = new ArrayList<>();

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
        this.logPrefixColored = StringUtil.t(String.format("&3[&b%s %s&3] &e", this.getDescription().getName(), version[version.length - 1]));
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
        this.colorLogger = new PluginLogger(this);
        this.colorLogger.logToConsole(this.logPrefixColored + "=== ENABLE START ===", Level.INFO);

        // Create the Module Manager
        this.moduleManager = new ModuleManager(this);

        // Load the Config
        if (isAutoLoadKamiConfig()) {
            this.getKamiConfig();
        }

        // Listener
        Bukkit.getPluginManager().registerEvents(this, this);

        return true;
    }

    public abstract void onEnableInner();

    /**
     * Should we automatically load the KamiConfig on enable?
     */
    public boolean isAutoLoadKamiConfig() {
        return true;
    }

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
        this.colorLogger.logToConsole(this.logPrefixColored + "=== ENABLE &aCOMPLETE &e(Took &d" + ms + "ms&e) ===", Level.INFO);
    }

    public @NotNull KamiConfigExt getKamiConfig() {
        if (config == null) {
            this.config = new KamiConfigExt(this, new File(getDataFolder(), "config.yml"), true);
            this.config.registerObserver(this);
        }
        return config;
    }
    public void reloadKamiConfig() {
        getKamiConfig().reload();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        reloadKamiConfig();
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        getKamiConfig().save();
    }

    @Override
    public final void onDisable() {
        // Call Their Disable Method
        onDisableInner();

        // Cleanup Listeners, Tasks, and Disableables
        unregisterListeners();
        unregisterTasks();
        unregisterDisableables();
        unregisterCommands();

        // Cleanup Modules
        if (moduleManager != null) {
            moduleManager.unregister();
        }

        onDisablePost();

        // Delete Config
        if (config != null) {
            config.unregisterObserver(this);
            config = null;
        }

        // Log Shutdown
        this.colorLogger.logToConsole(this.logPrefixColored + "Disabled", Level.INFO);
    }

    /**
     * Called First in JavaPlugin.onDisable()
     */
    public abstract void onDisableInner();

    /**
     * Called after both onDisableInner and listeners, tasks, and disableables are unregistered<br>
     * Also called after modules and commands are unregistered<br>
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
    // LISTENER REGISTRATION
    // -------------------------------------------- //
    /**
     * Registers one or more listeners for this plugin.<br>
     * The listeners will be automatically unregistered when the plugin is disabled. <br>
     * Previously registered listeners will not be registered again. <br>
     * @param listeners The listeners to register
     * @return The number of listeners that were registered from this call
     */
    @Override
    public final int registerListeners(Listener... listeners) {
        int count = 0;
        for (Listener listener : listeners) {
            if (listener == null) { continue; }
            Bukkit.getPluginManager().registerEvents(listener, this);
            if (listenerList.contains(listener)) { continue; }
            listenerList.add(listener);
            count++;
        }
        return count;
    }

    /**
     * Unregisters one or more listeners from this plugin.<br>
     * @param listeners The listeners to unregister
     * @return The number of listeners that were unregistered from this call
     */
    @Override
    public final int unregisterListeners(Listener... listeners) {
        int count = 0;
        for (Listener listener : listeners) {
            if (listener == null) { continue; }
            if (listenerList.remove(listener)) {
                HandlerList.unregisterAll(listener);
                count++;
            }
        }
        return count;
    }

    /**
     * Unregisters ALL listeners from this plugin.
     * @return The number of listeners that were unregistered from this call
     */
    @Override
    public final int unregisterListeners() {
        return unregisterListeners(listenerList.toArray(new Listener[0]));
    }

    // -------------------------------------------- //
    // DISABLEABLE REGISTRATION
    // -------------------------------------------- //
    /**
     * Registers one or more disableable objects for this plugin.<br>
     * The disableables will be automatically disabled when the plugin is disabled. <br>
     * Previously registered disableables will not be registered again. <br>
     * @param disableables The disableable objects to register
     * @return The number of disableables that were registered from this call
     */
    @Override
    public final int registerDisableables(Disableable... disableables) {
        int count = 0;
        for (Disableable disableable : disableables) {
            if (disableable == null) { continue; }
            if (disableableList.contains(disableable)) { continue; }
            disableableList.add(disableable);
            count++;
        }
        return count;
    }

    /**
     * Unregisters one or more disableable objects from this plugin.
     * @param disableables The disableable objects to unregister
     * @return The number of disableables that were unregistered from this call
     */
    @Override
    public final int unregisterDisableables(Disableable... disableables) {
        int count = 0;
        for (Disableable disableable : disableables) {
            if (disableable == null) { continue; }
            if (disableableList.remove(disableable)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Unregisters ALL disableable objects from this plugin.
     * @return The number of disableables that were unregistered from this call
     */
    @Override
    public final int unregisterDisableables() {
        return unregisterDisableables(disableableList.toArray(new Disableable[0]));
    }

    // -------------------------------------------- //
    // TASK REGISTRATION
    // -------------------------------------------- //

    /**
     * Registers one or more tasks for this plugin.<br>
     * The tasks will be automatically cancelled when the plugin is disabled. <br>
     * Previously registered tasks will not be registered again. <br>
     * @param tasks The tasks to register
     * @return The number of tasks that were registered from this call
     */
    @Override
    public final int registerTasks(BukkitTask... tasks) {
        int count = 0;
        for (BukkitTask task : tasks) {
            if (task == null) { continue; }
            if (taskList.contains(task)) { continue; }
            taskList.add(task);
            count++;
        }
        return count;
    }

    /**
     * Unregisters one or more tasks from this plugin.
     * @param tasks The tasks to unregister
     * @return The number of tasks that were unregistered from this call
     */
    @Override
    public final int unregisterTasks(BukkitTask... tasks) {
        int count = 0;
        for (BukkitTask task : tasks) {
            if (task == null) { continue; }
            if (taskList.remove(task)) {
                Bukkit.getScheduler().cancelTask(task.getTaskId());
                count++;
            }
        }
        return count;
    }

    /**
     * Unregisters ALL tasks from this plugin.
     * @return The number of tasks that were unregistered from this call
     */
    @Override
    public final int unregisterTasks() {
        return unregisterTasks(taskList.toArray(new BukkitTask[0]));
    }

    // -------------------------------------------- //
    // COMMAND REGISTRATION
    // -------------------------------------------- //
    /**
     * Registers the provided commands with this plugin. <br>
     * Previously registered commands will not be registered again. <br>
     * @param commands The commands to register
     * @return The number of commands that were registered with this plugin
     */
    @Override
    public final int registerCommands(KamiCommand... commands) {
        // Register the provided commands, skipping any that are already registered with this plugin
        int count = 0;
        for (KamiCommand command : commands) {
            if (command == null) { continue; }
            if (commandList.contains(command)) { continue; }
            command.registerCommand(this);
            commandList.add(command);
            count++;
        }

        // Ensure a call to update the registrations is queued (if we had commands)
        if (count > 0) {
            KamiCommonCommandRegistration.updateRegistrations();
        }
        return count;
    }

    /**
     * Unregisters the specified commands, if this plugin has registered them prior. <br>
     * ! If a KamiCommand is passed here that was not registered by this plugin, it will not be unregistered ! <br>
     * @return The number of commands that were unregistered from this plugin
     */
    @Override
    public final int unregisterCommands(KamiCommand... commands) {
        // Unregister the commands IFF they were registered by this plugin
        int count = 0;
        for (KamiCommand command : commands) {
            if (command == null) { continue; }
            if (commandList.remove(command)) {
                command.unregisterCommand();
                count++;
            }
        }

        // Update the command registrations map, so that bukkit gets rid of them
        KamiCommonCommandRegistration.updateRegistrations();
        return count;
    }

    /**
     * Unregisters ALL commands that this plugin has registered. <br>
     * @return The number of commands that were unregistered
     */
    @Override
    public final int unregisterCommands() {
        return unregisterCommands(commandList.toArray(new KamiCommand[0])); // Will remove from commandList
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
        // Fetch target plugin version
        JavaPlugin pl = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        if (pl == null) {
            getLogger().severe("Could not load " + pluginName + " dependency! (Plugin Not Found!)");
            return false;
        }

        // Compare versions
        if (!compareVersions(minVer, pl.getDescription().getVersion())) {
            getLogger().severe(pluginName + " version is too old! (" + minVer + " or higher required)");
            if (callback != null) { callback.onFailure(pluginName, minVer); }
            return false;
        }
        getLogger().info(pluginName + " version " + pl.getDescription().getVersion() + " found, met requirement >= " + minVer);
        return true;
    }

    /**
     * Requires ver format to be int.int.int... (ints separated by periods)
     * @return If currentVer satisfies minVer
     */
    public boolean compareVersions(String minVer, String currentVer) {
        // Cut off any suffixes (e.g. "-SNAPSHOT")
        minVer = minVer.split("-")[0];
        currentVer = currentVer.split("-")[0];

        // Use major, minor, and patch version logic to compare
        String[] minParts = minVer.split("\\.");
        String[] curParts = currentVer.split("\\."); // May be of different length

        // Compare versions in order of significance
        for (int i = 0; i < minParts.length; i++) {
            int min = Integer.parseInt(minParts[i]);
            int cur = i < curParts.length ? Integer.parseInt(curParts[i]) : 0;
            if (cur > min) { return true; }
            if (cur < min) { return false; }
        }

        // If we have reached this point, the versions were equal
        return true;
    }

    public @NotNull KamiConfigExt getModulesConfig() {
        // Create on-demand, since creating the KamiConfig will create the file too
        if (modulesConfig == null) {
            // Create the Modules Config
            this.modulesConfig = new KamiConfigExt(this, new File(getDataFolder(), "modules.yml"), false);
        }
        return modulesConfig;
    }


    // -------------------------------------------- //
    // MISCELLANEOUS
    // -------------------------------------------- //
    /**
     * Registers a {@link ConfigObserver} to a {@link KamiConfig} instance.
     * @return true IFF the observer was registered as a result of this call, false if the observer was already registered to the config.
     */
    public final boolean registerConfigObserver(@NotNull ConfigObserver observer, @NotNull KamiConfig config) {
        return config.registerObserver(observer);
    }

    /**
     * Optional Override (with listening behavior)
     */
    @Override
    public void onConfigLoaded(@NotNull KamiConfig config) { }
}

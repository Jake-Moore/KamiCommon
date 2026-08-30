package com.kamikazejam.kamicommon;

import com.google.gson.JsonObject;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.KamiCommonCommandRegistration;
import com.kamikazejam.kamicommon.configuration.observe.ConfigObserver;
import com.kamikazejam.kamicommon.configuration.observe.ObservableConfig;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.log.ComponentLogger;
import com.kamikazejam.kamicommon.subsystem.feature.Feature;
import com.kamikazejam.kamicommon.subsystem.feature.FeatureManager;
import com.kamikazejam.kamicommon.subsystem.module.Module;
import com.kamikazejam.kamicommon.subsystem.module.ModuleManager;
import com.kamikazejam.kamicommon.util.interfaces.Disableable;
import com.kamikazejam.kamicommon.util.interfaces.Named;
import com.kamikazejam.kamicommon.util.log.LegacyColorsLogger;
import com.kamikazejam.kamicommon.util.log.LoggerService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings({"unused", "UnusedReturnValue", "DuplicatedCode"})
public abstract class KamiPlugin extends JavaPlugin implements Listener, Named, CoreMethods, ObservableConfig<KamiConfig> {
    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    @Getter
    private long enableTime;
    private String logPrefixMini = null;
    @Getter ModuleManager moduleManager;
    @Getter FeatureManager featureManager;
    private KamiConfigExt modulesConfig = null;
    private KamiConfigExt featuresConfig = null;
    private @Nullable KamiConfigExt config = null;

    /**
     * @deprecated Use {@link #getColorComponentLogger()} instead, which supports the same legacy string methods, but also component methods.
     */
    @Deprecated @Getter
    private LoggerService colorLogger;

    /**
     * An improved color-supporting logger, which supports both legacy color codes and component-based messages.
     */
    @Getter
    private ComponentLogger colorComponentLogger;
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
        this.logPrefixMini = String.format(
                "<dark_aqua>[<aqua>%s %s<dark_aqua>] <yellow>",
                this.getDescription().getName(),
                version[version.length - 1]
        );
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
        this.colorLogger = new LegacyColorsLogger(this);
        this.colorComponentLogger = new ComponentLogger(this);
        this.colorComponentLogger.logToConsole(
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage(this.logPrefixMini + "=== ENABLE START ==="),
                Level.INFO
        );

        // Create the Subsystem Managers
        this.moduleManager = new ModuleManager(this);
        this.featureManager = new FeatureManager(this);

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
        long ms = System.currentTimeMillis() - this.enableTime;
        this.colorComponentLogger.logToConsole(
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage(
                        this.logPrefixMini + "=== ENABLE <green>COMPLETE <yellow>(Took <light_purple>" + ms + "ms<yellow>) ==="
                ),
                Level.INFO
        );
    }

    @NotNull
    public final KamiConfigExt getKamiConfig() {
        if (config == null) {
            this.config = createKamiConfig();
        }
        return config;
    }
    public void reloadKamiConfig() {
        getKamiConfig().reload();
    }

    @OverrideOnly
    public @NotNull KamiConfigExt createKamiConfig() {
        // Using the Ext config with defaults enabled. It looks for a 'config.yml' resource file.
        return new KamiConfigExt(this, new File(getDataFolder(), "config.yml"));
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
        // Cleanup Features
        if (featureManager != null) {
            featureManager.unregister();
        }

        onDisablePost();

        // Delete Config
        if (config != null) {
            config.unregisterConfigObservers();
            config = null;
        }

        // Log Shutdown
        this.colorComponentLogger.logToConsole(
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage(this.logPrefixMini + "Disabled"),
                Level.INFO
        );
    }

    /**
     * Called First in JavaPlugin.onDisable()
     */
    public abstract void onDisableInner();

    /**
     * Called after both onDisableInner and listeners, tasks, and disableables are unregistered<br>
     * Also called after modules, features, and commands are unregistered<br>
     * You can use this method to clean up databases or anything else that should come after module/features shutdowns
     */
    public void onDisablePost() {}

    /**
     * Can override if module configs are stored in a subpackage of the jar
     */
    public String getModuleYmlPath() {
        return null;
    }

    /**
     * Can override if feature configs are stored in a subpackage of the jar
     */
    public String getFeatureYmlPath() {
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
    public void registerModule(Module... modules) {
        for (Module module : modules) {
            if (module == null) { continue; }
            getModuleManager().registerModule(module);
        }
    }

    // -------------------------------------------- //
    // FEATURE MANAGEMENT
    // -------------------------------------------- //
    public void registerFeature(Feature... features) {
        for (Feature feature : features) {
            if (feature == null) { continue; }
            getFeatureManager().registerFeature(feature);
        }
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

    @NotNull
    public final KamiConfigExt getModulesConfig() {
        // Create on-demand, since creating the KamiConfig will create the file too
        if (modulesConfig == null) {
            this.modulesConfig = createModulesConfig();
        }
        return modulesConfig;
    }

    @OverrideOnly
    public @NotNull KamiConfigExt createModulesConfig() {
        // Create the Modules Config (expects defaults in a 'modules.yml' resource file)
        return new KamiConfigExt(this, new File(getDataFolder(), "modules.yml"));
    }

    @NotNull
    public final KamiConfigExt getFeaturesConfig() {
        // Create on-demand, since creating the KamiConfig will create the file too
        if (featuresConfig == null) {
            this.featuresConfig = createFeaturesConfig();
        }
        return featuresConfig;
    }

    @OverrideOnly
    public @NotNull KamiConfigExt createFeaturesConfig() {
        // Create the Features Config (expects defaults in a 'features.yml' resource file)
        return new KamiConfigExt(this, new File(getDataFolder(), "features.yml"), null);
    }

    // -------------------------------------------- //
    // ObservableConfig
    // -------------------------------------------- //
    /**
     * Registers an observer with the default KamiPlugin config (if not already registered)<br>
     * Refer to the {@link ConfigObserver} docs for information on its lifecycle events.
     * @return If the observer was successfully registered from this call (false if already registered)
     */
    @Override
    public boolean registerConfigObserver(@NotNull ConfigObserver<KamiConfig> observer) {
        return this.getKamiConfig().registerConfigObserver(observer);
    }

    /**
     * Unregisters an observer from this plugin's default KamiConfig
     */
    @Override
    public void unregisterConfigObserver(@NotNull ConfigObserver<KamiConfig> observer) {
        this.getKamiConfig().unregisterConfigObserver(observer);
    }

    /**
     * Unregisters ALL observers from this plugin's default KamiConfig.<br>
     * Intended for shutdown logic, but can be used at any time.
     */
    @Override
    public void unregisterConfigObservers() {
        this.getKamiConfig().unregisterConfigObservers();
    }

    /**
     * Reload the default KamiConfig for this plugin, notifying all registered observers of the change.<br>
     * <br>
     * Equivalent to {@link #reloadKamiConfig()}
     */
    @Override
    public void reloadObservableConfig() {
        this.reloadKamiConfig();
    }
}

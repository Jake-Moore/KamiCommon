package com.kamikazejam.kamicommon.modules;

import com.kamikazejam.kamicommon.CoreMethods;
import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.KamiCommonCommandRegistration;
import com.kamikazejam.kamicommon.configuration.spigot.ConfigObserver;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.util.MessageBuilder;
import com.kamikazejam.kamicommon.util.interfaces.Disableable;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "DuplicatedCode"})
public abstract class Module implements CoreMethods, ConfigObserver {
    // CoreMethods Fields
    private final List<Listener> listenerList = new ArrayList<>();
    private final List<BukkitTask> taskList = new ArrayList<>();
    private final List<KamiCommand> commandList = new ArrayList<>();
    private final List<Disableable> disableableList = new ArrayList<>();
    private final List<ConfigObserver> moduleConfigObservers = new ArrayList<>();

    @Getter private boolean successfullyEnabled = false;
    @Getter private boolean enabled = false;

    /**
     * @return The KamiPlugin that this module is registered to
     */
    public abstract KamiPlugin getPlugin();

    /**
     * This method is called every time the module is loaded. <br>
     * This is called before both {@link #onEnable()}. <br>
     * It is also called on {@link Module#reloadConfig()} (when the module is reloaded). <br>
     * It is NOT called for {@link ModuleConfig#reload()} (when the backing config is reloaded). <br>
     * You should put logic here that depends on values in the config. For easy reloading.
     */
    public abstract void onConfigLoaded(@NotNull ModuleConfig config);

    @Override
    @ApiStatus.Internal
    public final void onConfigLoaded(@NotNull KamiConfig config) {
        // Call the module's onConfigLoaded method, since our config should always be a ModuleConfig
        onConfigLoaded((ModuleConfig) config);
        // Call all observers of this config
        moduleConfigObservers.forEach(observer -> observer.onConfigLoaded(config));
    }

    /**
     * This method is called at {@link Module} initialization. <br>
     * This is called after {@link #onConfigLoaded(ModuleConfig)}. <br>
     * You should handle your enable logic here, including registering commands/listeners/tasks/disableables. <br>
     * <br>
     * Registration Methods: {@link #registerCommands}, {@link #registerListeners}, {@link #registerTasks}, {@link #registerDisableables}
     */
    public abstract void onEnable();

    /**
     * This method is called when a module is shutting down (server shut down most likely, but it could be manually called) <br>
     * You should handle your disable logic here, not including any unregistration of commands/listeners/tasks/disableables. (That is handled automatically) <br>
     */
    public abstract void onDisable();

    /**
     * @return Whether this module is enabled by default (generally always true, except in specific situations) <br>
     */
    @SuppressWarnings("SameReturnValue")
    public abstract boolean isEnabledByDefault();


    // -------------------------------------------- //
    // COMMAND METHODS
    // -------------------------------------------- //
    /**
     * Registers the provided commands with this module. <br>
     * Previously registered commands will not be registered again. <br>
     * @param commands The commands to register
     * @return The number of commands that were registered with this module
     */
    @Override
    public final int registerCommands(KamiCommand... commands) {
        KamiPlugin plugin = getPlugin();

        // Register the provided commands, skipping any that are already registered with this module
        int count = 0;
        for (KamiCommand command : commands) {
            if (command == null) { continue; }
            if (commandList.contains(command)) { continue; }
            command.registerCommand(plugin);
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
     * Unregisters the specified commands, if this module has registered them prior. <br>
     * ! If a KamiCommand is passed here that was not registered by this module, it will not be unregistered ! <br>
     * @return The number of commands that were unregistered from this module
     */
    @Override
    public final int unregisterCommands(KamiCommand... commands) {
        // Unregister the commands IFF they were registered by this module
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
     * Unregisters ALL commands that this module has registered. <br>
     * @return The number of commands that were unregistered
     */
    @Override
    public final int unregisterCommands() {
        return unregisterCommands(commandList.toArray(new KamiCommand[0])); // Will remove from commandList
    }


    // -------------------------------------------- //
    // INTEGRATIONS
    // -------------------------------------------- //
    /**
     * This method is called when/if ItemsAdder loads/reloads. <br>
     * It is always called after {@link #onEnable()} <br>
     * <br>
     * This may be called several times (if ItemsAdder is reloaded)
     */
    public void onItemsAdderLoaded() {}

    /**
     * This method is called when/if MythicMobs loads/reloads. <br>
     * It is always called after {@link #onEnable()} <br>
     * <br>
     * This may be called several times (if MythicMobs is reloaded)
     */
    public void onMythicMobsLoaded() {}

    /**
     * This method is called when/if Citizens loads/reloads. <br>
     * It is always called after {@link #onEnable()} <br>
     * <br>
     * This may be called several times (if Citizens is reloaded)
     */
    public void onCitizensLoaded() {}



    // -------------------------------------------- //
    // GENERAL METHODS
    // -------------------------------------------- //
    /**
     * @return The name of this module
     */
    public abstract String getName();

    /**
     * @return The default logging prefix for this module (saved under the KamiPlugin modulesConfig modulePrefix)
     */
    public abstract @NotNull String defaultPrefix();


    // -------------------------------------------- //
    // MODULE CONFIG
    // -------------------------------------------- //
    private ModuleConfig moduleConfig = null;
    @Override
    public final void reloadConfig() {
        moduleConfig.reload(); // Automatically saves
    }

    @Override
    public void saveConfig() {
        moduleConfig.save();
    }

    // Optionally Overrideable If the Config Name is different
    public String getConfigName() {
        return getName() + "Module.yml";
    }

    @NotNull
    public ModuleConfig getConfig() {
        if (this.moduleConfig == null) {
            this.initializeConfig(createConfig());
        }
        return Objects.requireNonNull(this.moduleConfig);
    }

    // Stores the given ModuleConfig and initializes it
    private void initializeConfig(@NotNull ModuleConfig config) {
        this.moduleConfig = config;
        this.moduleConfig.registerObserver(this);
    }

    // Creates a ModuleConfig from the provided path
    @NotNull
    private ModuleConfig createConfig() {
        final ModuleConfig moduleConfig;
        @Nullable String moduleYmlPath = getPlugin().getModuleYmlPath();
        if (moduleYmlPath == null) {
            moduleConfig = new ModuleConfig(this, getConfigName());
        }else {
            if (!moduleYmlPath.endsWith("/")) { moduleYmlPath += "/"; }
            moduleConfig = new ModuleConfig(this, moduleYmlPath + getConfigName());
        }
        return Objects.requireNonNull(moduleConfig);
    }

    @Override
    public @NotNull KamiConfigExt getKamiConfig() {
        return getConfig();
    }

    public final boolean isEnabledInConfig() {
        // Create a throw-away config so that we don't start any initialization logic
        // if this module does not intend to be enabled
        ModuleConfig config = createConfig();
        boolean enabled = config.getBoolean("enabled", isEnabledByDefault());
        // If the module is enabled in the config, begin initialization
        if (enabled) {
            initializeConfig(config);
        }
        return enabled;
    }



    // -------------------------------------------- //
    // ENABLE/DISABLE HANDLING
    // -------------------------------------------- //
    public final void handleEnable() {
        onEnable();
        info("Module enabled");
        successfullyEnabled = true;
        enabled = true;
    }

    public final void handleDisable() {
        onDisable();
        onDisableLater();
        info("Module disabled");
        enabled = false;
        if (moduleConfig != null) {
            moduleConfig.unregisterObserver(this);
            moduleConfig = null;
        }
    }

    public final void onDisableLater() {
        // Unregister Listeners
        int listeners = unregisterListeners();
        getPlugin().unregisterListeners(listenerList.toArray(new Listener[0]));
        listenerList.clear();
        if (listeners > 0) {
            getPlugin().getLogger().info("[" + getName() + "] Unregistered " + listeners + " listeners.");
        }

        // Unregister Commands
        int c = this.unregisterCommands();
        if (c > 0) {
            getPlugin().getLogger().info("[" + getName() + "] Unregistered " + c + " commands.");
        }

        // Unregister Tasks
        int tasks = taskList.size();
        getPlugin().unregisterTasks(taskList.toArray(new BukkitTask[0]));
        taskList.clear();
        if (tasks > 0) {
            getPlugin().getLogger().info("[" + getName() + "] Unregistered " + tasks + " tasks.");
        }

        // Unregister Disableables
        int disableables = disableableList.size();
        getPlugin().unregisterDisableables(disableableList.toArray(new Disableable[0]));
        disableableList.clear();
        if (disableables > 0) {
            getPlugin().getLogger().info("[" + getName() + "] Disabled " + disableables + " disableables.");
        }
    }


    // -------------------------------------------- //
    // Listener Methods
    // -------------------------------------------- //
    /**
     * Registers one or more listeners for this module.<br>
     * The listeners will be automatically unregistered when the module is disabled. <br>
     * Previously registered listeners will not be registered again. <br>
     * @param listeners The listeners to register
     * @return The number of listeners that were registered from this call
     */
    @Override
    public final int registerListeners(Listener... listeners) {
        int count = 0;
        for (Listener listener : listeners) {
            if (listener == null) { continue; }
            if (listenerList.contains(listener)) { continue; }
            getPlugin().registerListeners(listener);
            listenerList.add(listener);
            count++;
        }
        return count;
    }

    /**
     * Unregisters one or more listeners from this module.<br>
     * @param listeners The listeners to unregister
     * @return The number of listeners that were unregistered from this call
     */
    @Override
    public final int unregisterListeners(Listener... listeners) {
        int count = 0;
        for (Listener listener : listeners) {
            if (listener == null) { continue; }
            if (listenerList.remove(listener)) {
                getPlugin().unregisterListeners(listener);
                count++;
            }
        }
        return count;
    }

    /**
     * Unregisters ALL listeners from this module.
     * @return The number of listeners that were unregistered from this call
     */
    @Override
    public final int unregisterListeners() {
        return unregisterListeners(listenerList.toArray(new Listener[0]));
    }

    // -------------------------------------------- //
    // Task Methods
    // -------------------------------------------- //
    /**
     * Registers one or more tasks for this module.<br>
     * The tasks will be automatically cancelled when the module is disabled. <br>
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
            getPlugin().registerTasks(task);
            taskList.add(task);
            count++;
        }
        return count;
    }

    /**
     * Unregisters one or more tasks from this module.
     * @param tasks The tasks to unregister
     * @return The number of tasks that were unregistered from this call
     */
    @Override
    public final int unregisterTasks(BukkitTask... tasks) {
        int count = 0;
        for (BukkitTask task : tasks) {
            if (task == null) { continue; }
            if (taskList.remove(task)) {
                getPlugin().unregisterTasks(task);
                count++;
            }
        }
        return count;
    }

    /**
     * Unregisters ALL tasks from this module.
     * @return The number of tasks that were unregistered from this call
     */
    @Override
    public final int unregisterTasks() {
        return unregisterTasks(taskList.toArray(new BukkitTask[0]));
    }

    // -------------------------------------------- //
    // Disableable Methods
    // -------------------------------------------- //
    /**
     * Registers one or more disableable objects for this module.<br>
     * The disableables will be automatically disabled when the module is disabled. <br>
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
            getPlugin().registerDisableables(disableable);
            disableableList.add(disableable);
            count++;
        }
        return count;
    }

    /**
     * Unregisters one or more disableable objects from this module.
     * @param disableables The disableable objects to unregister
     * @return The number of disableables that were unregistered from this call
     */
    @Override
    public final int unregisterDisableables(Disableable... disableables) {
        int count = 0;
        for (Disableable disableable : disableables) {
            if (disableable == null) { continue; }
            if (disableableList.remove(disableable)) {
                getPlugin().unregisterDisableables(disableable);
                count++;
            }
        }
        return count;
    }

    /**
     * Unregisters ALL disableable objects from this module.
     * @return The number of disableables that were unregistered from this call
     */
    @Override
    public final int unregisterDisableables() {
        return unregisterDisableables(disableableList.toArray(new Disableable[0]));
    }

    // -------------------------------------------- //
    // LOGGING
    // -------------------------------------------- //
    public void info(String msg) { log(msg); }
    public void log(String msg) {
        getPlugin().getLogger().info("[" + getName() + "] " + msg);
    }

    public void warning(String msg) { warn(msg); }
    public void warn(String msg) {
        getPlugin().getLogger().warning("[" + getName() + "] " + msg);
    }

    public void warnWithTrace(String msg) {
        msg = "[" + getName() + "] " + msg;
        getPlugin().getLogger().warning(msg);
        try {
            throw new Exception(msg);
        }catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void severe(String msg) { error(msg); }
    public void error(String msg) {
        getPlugin().getLogger().severe("[" + getName() + "] " + msg);
    }
    public void errorWithTrace(String msg) {
        msg = "[" + getName() + "] " + msg;
        getPlugin().getLogger().severe(msg);
        try {
            throw new Exception(msg);
        }catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * @return The prefix for this module, as defined in the module config
     */
    public final @NotNull String getPrefix() {
        KamiConfigExt c = getPlugin().getModulesConfig();
        String prefix = c.getString("modules." + getName() + ".modulePrefix", null);
        if (prefix != null) { return prefix; }

        String def = defaultPrefix();
        c.setString("modules." + getName() + ".modulePrefix", def);
        c.save();
        return def;
    }

    /**
     * Builds a {@link MessageBuilder} using this Module's config and the provided key <br>
     * It will also automatically replace any {prefix} placeholders in the message with this module's prefix
     * @param key The key to get the message from the config
     * @return The MessageBuilder (see above)
     */
    public MessageBuilder buildMessage(String key) {
        return MessageBuilder.of(getConfig(), key)
                .replace("{prefix}", getPrefix())
                .replace("%prefix%", getPrefix());
    }

    /**
     * Builds a MessageBuilder using this Module's config and the provided key <br>
     * It will also automatically replace any {prefix} placeholders in the message with this module's prefix
     * @param key The key to get the message from the config
     * @return The MessageBuilder (see above)
     */
    public MessageBuilder getMessage(String key) {
        return buildMessage(key);
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
     * Registers a {@link ConfigObserver} to this {@link Module} instance to receive reloads automatically from {@link #onConfigLoaded(ModuleConfig)}
     */
    public final void registerConfigObserver(@NotNull ConfigObserver observer) {
        this.moduleConfigObservers.add(observer);
    }
}

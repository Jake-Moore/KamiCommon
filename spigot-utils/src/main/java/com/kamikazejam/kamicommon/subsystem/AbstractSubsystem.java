package com.kamikazejam.kamicommon.subsystem;

import com.kamikazejam.kamicommon.CoreMethods;
import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.KamiCommonCommandRegistration;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.configuration.spigot.observe.ConfigObserver;
import com.kamikazejam.kamicommon.configuration.spigot.observe.ObservableConfig;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.log.ComponentLogger;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.text.MiniMessageBuilder;
import com.kamikazejam.kamicommon.util.MessageBuilder;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.interfaces.Disableable;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public abstract class AbstractSubsystem<C extends SubsystemConfig<S>, S extends AbstractSubsystem<C, S>> implements CoreMethods, ObservableConfig {
    @Getter private boolean successfullyEnabled = false;
    @Getter private boolean enabled = false;
    @Getter private ComponentLogger logger;

    // CoreMethods Fields
    private final List<Listener> listenerList = new ArrayList<>();
    private final List<BukkitTask> taskList = new ArrayList<>();
    private final List<KamiCommand> commandList = new ArrayList<>();
    private final List<Disableable> disableableList = new ArrayList<>();

    // Hooks
    private final List<ObservableConfig> configHooks = new ArrayList<>();

    /**
     * @return The KamiPlugin that this subsystem is registered to
     */
    public abstract @NotNull KamiPlugin getPlugin();

    /**
     * This method is called at {@link AbstractSubsystem} initialization. <br>
     * You should handle your enable logic here, including: configs/commands/listeners/tasks/disableables. <br>
     * <br>
     * Registration Methods: {@link #registerCommands}, {@link #registerListeners}, {@link #registerTasks}, {@link #registerDisableables}
     */
    public abstract void onEnable();

    /**
     * This method is called when a subsystem is shutting down (server shut down most likely, but it could be manually called) <br>
     * You should handle your disable logic here, not including any unregistration of commands/listeners/tasks/disableables. (That is handled automatically) <br>
     */
    public abstract void onDisable();

    // -------------------------------------------- //
    // COMMAND METHODS
    // -------------------------------------------- //
    /**
     * Registers the provided commands with this subsystem. <br>
     * Previously registered commands will not be registered again. <br>
     * @param commands The commands to register
     * @return The number of commands that were registered with this subsystem
     */
    @Override
    public final int registerCommands(KamiCommand... commands) {
        KamiPlugin plugin = getPlugin();

        // Register the provided commands, skipping any that are already registered with this subsystem
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
     * Unregisters the specified commands, if this subsystem has registered them prior. <br>
     * ! If a KamiCommand is passed here that was not registered by this subsystem, it will not be unregistered ! <br>
     * @return The number of commands that were unregistered from this subsystem
     */
    @Override
    public final int unregisterCommands(KamiCommand... commands) {
        // Unregister the commands IFF they were registered by this subsystem
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
     * Unregisters ALL commands that this subsystem has registered. <br>
     * @return The number of commands that were unregistered
     */
    @Override
    public final int unregisterCommands() {
        return unregisterCommands(commandList.toArray(new KamiCommand[0])); // Will remove from commandList
    }

    // -------------------------------------------- //
    // GENERAL METHODS
    // -------------------------------------------- //
    /**
     * @return The name of this subsystem
     */
    public abstract String getName();

    /**
     * @return The default logging prefix for this subsystem
     */
    public abstract @NotNull VersionedComponent defaultPrefix();

    // -------------------------------------------- //
    // SUBSYSTEM CONFIG
    // -------------------------------------------- //
    @NotNull
    public abstract File getConfigFileDestination();

    private @Nullable C subsystemConfig = null;

    /**
     * Reload the subsystem config from its yaml file on disk.<br>
     *
     * Will also call any {@link ObservableConfig} hooks registered via {@link #registerReloadHook(ObservableConfig)}
     */
    @Override
    public final void reloadConfig() {
        // Reload Primary Config
        C config = Preconditions.checkNotNull(subsystemConfig, "SubsystemConfig is null! Cannot reload config!");
        config.reload(); // Automatically saves
        // Call hook reloads
        for (ObservableConfig hook : configHooks) {
            hook.reloadObservableConfig();
        }
    }

    @Override
    public void saveConfig() {
        C config = Preconditions.checkNotNull(subsystemConfig, "SubsystemConfig is null! Cannot save config!");
        config.save();
    }

    public abstract @NotNull String getConfigResourcePath();

    @NotNull
    public C getConfig() {
        if (this.subsystemConfig == null) {
            this.initializeConfig(createConfig());
        }
        return Objects.requireNonNull(this.subsystemConfig);
    }

    // Stores the given SubsystemConfig and initializes it
    @Internal
    public void initializeConfig(@NotNull C config) {
        this.subsystemConfig = config;
    }

    @NotNull
    protected abstract C createConfig();

    @Override
    public @NotNull KamiConfigExt getKamiConfig() {
        return getConfig();
    }

    /**
     * Adds a hook to call this observable's {@link #reloadObservableConfig()} method whenever this subsystem's config is reloaded.<br>
     * <br>
     * This means that when this subsystem gets reloaded, this config will also be reloaded.
     */
    public final void registerReloadHook(@NotNull ObservableConfig config) {
        Preconditions.checkNotNull(config, "Cannot register a null config hook!");
        // caller's responsibility to not register the same hook multiple times
        configHooks.add(config);
    }

    // -------------------------------------------- //
    // ENABLE/DISABLE HANDLING
    // -------------------------------------------- //
    public final void handleEnable() {
        // Create this subsystem's logger with an extra prefix of this subsystem's name
        this.logger = new ComponentLogger(getPlugin()).setMessagePrefix(
                NmsAPI.getVersionedComponentSerializer().fromPlainText("[" + getName() + "] ")
        );
        onEnable();
        this.logger.info(NmsAPI.getVersionedComponentSerializer().fromPlainText("Successfully enabled!"));
        successfullyEnabled = true;
        enabled = true;
    }

    public final void handleDisable() {
        onDisable();
        onDisableLater();
        this.logger.info(NmsAPI.getVersionedComponentSerializer().fromPlainText("Successfully disabled!"));
        enabled = false;
        // Clear config
        if (subsystemConfig != null) {
            subsystemConfig.unregisterConfigObservers();
            subsystemConfig = null;
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
     * Registers one or more listeners for this subsystem.<br>
     * The listeners will be automatically unregistered when the subsystem is disabled. <br>
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
     * Unregisters one or more listeners from this subsystem.<br>
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
     * Unregisters ALL listeners from this subsystem.
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
     * Registers one or more tasks for this subsystem.<br>
     * The tasks will be automatically cancelled when the subsystem is disabled. <br>
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
     * Unregisters one or more tasks from this subsystem.
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
     * Unregisters ALL tasks from this subsystem.
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
     * Registers one or more disableable objects for this subsystem.<br>
     * The disableables will be automatically disabled when the subsystem is disabled. <br>
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
     * Unregisters one or more disableable objects from this subsystem.
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
     * Unregisters ALL disableable objects from this subsystem.
     * @return The number of disableables that were unregistered from this call
     */
    @Override
    public final int unregisterDisableables() {
        return unregisterDisableables(disableableList.toArray(new Disableable[0]));
    }

    /**
     * @return The prefix for this subsystem, as defined in the subsystem config
     */
    public abstract @NotNull VersionedComponent getPrefix();

    /**
     * Builds a {@link MessageBuilder} using this Subsystems' config and the provided key <br>
     * It will also automatically replace any {prefix} placeholders in the message with this subsystem's prefix
     * @param key The key to get the message from the config
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #buildMiniMessage(String)}
     * @return The MessageBuilder (see above)
     */
    @Deprecated
    public @NotNull MessageBuilder buildMessage(@NotNull String key) {
        Preconditions.checkNotNull(key, "Message key cannot be null!");
        String sectionedPrefix = getPrefix().serializeLegacySection();
        return MessageBuilder.of(getConfig(), key)
                .replace("{prefix}", sectionedPrefix)
                .replace("%prefix%", sectionedPrefix);
    }

    /**
     * Builds a {@link MessageBuilder} using this Subsystems' config and the provided key <br>
     * It will also automatically replace any {prefix} placeholders in the message with this subsystem's prefix
     * @param key The key to get the message from the config
     * @deprecated As of 5.0.0-alpha.26, replaced by {@link #buildMiniMessage(String)}
     * @return The MessageBuilder (see above)
     */
    @Deprecated
    public MessageBuilder getMessage(String key) {
        return buildMessage(key);
    }

    /**
     * Builds a {@link MiniMessageBuilder} using this Subsystems' config and the provided key <br>
     * It will also automatically replace any {prefix} placeholders in the message with this subsystem's prefix
     * @param key The key to get the message from the config
     * @return The MiniMessageBuilder (see above)
     */
    public @NotNull MiniMessageBuilder buildMiniMessage(@NotNull String key) {
        Preconditions.checkNotNull(key, "Message key cannot be null!");
        return MiniMessageBuilder.fromMiniMessage(getConfig(), key)
                .replace("{prefix}", getPrefix())
                .replace("%prefix%", getPrefix());
    }

    /**
     * Builds a {@link MiniMessageBuilder} using this Subsystems' config and the provided key <br>
     * It will also automatically replace any {prefix} placeholders in the message with this subsystem's prefix
     * @param key The key to get the message from the config
     * @return The MiniMessageBuilder (see above)
     */
    @Deprecated
    public MiniMessageBuilder getMiniMessage(String key) {
        return buildMiniMessage(key);
    }


    // -------------------------------------------- //
    // ObservableConfig
    // -------------------------------------------- //
    @Override
    public boolean registerConfigObserver(@NotNull ConfigObserver observer) {
        return getConfig().registerConfigObserver(observer);
    }

    @Override
    public void unregisterConfigObserver(@NotNull ConfigObserver observer) {
        getConfig().unregisterConfigObserver(observer);
    }

    @Override
    public void unregisterConfigObservers() {
        getConfig().unregisterConfigObservers();
    }

    /**
     * Reloads the backing config for this observable, notifying all registered observers of the change.<br>
     * <br>
     * Equivalent to calling {@link #reloadConfig()} on this subsystem.<br>
     */
    @Override
    public void reloadObservableConfig() {
        reloadConfig();
    }

    // -------------------------------------------- //
    // SUPPLEMENTAL CONFIG
    // -------------------------------------------- //
    /**
     * Placeholder for your own implementation in order to support supplemental configuration files.<br>
     * This method should return an InputStream to the supplemental config resource.<br><br>
     * By default, this method throws an {@link UnsupportedOperationException}.
     * @param fileName The YAML file name of the resource to load. Includes ONLY the name, not the path.
     * @throws UnsupportedOperationException Always, unless overridden with new behavior
     */
    @UnknownNullability
    public InputStream getSupplementalConfigResource(@NotNull String fileName) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}

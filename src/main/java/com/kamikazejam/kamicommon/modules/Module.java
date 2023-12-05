package com.kamikazejam.kamicommon.modules;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.KamiCommonCommandRegistration;
import com.kamikazejam.kamicommon.util.MessageBuilder;
import com.kamikazejam.kamicommon.util.interfaces.Disableable;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public abstract class Module {

    @Getter private boolean successfullyEnabled = false;

    private final List<Listener> listenerList = new ArrayList<>();
    private final List<BukkitTask> taskList = new ArrayList<>();
    private final List<KamiCommand> commandList = new ArrayList<>();
    private final List<Disableable> disableableList = new ArrayList<>();

    @Getter private boolean enabled = false;

    /**
     * @return The KamiPlugin that this module is registered to
     */
    public abstract KamiPlugin getPlugin();

    /**
     * This method is called after the config is loaded/setup and before {@link #onEnable()}. <p>
     * It is also called in {@link ModuleConfig#reload()} (When the config is reloaded). <p>
     * You should put logic here that depends on values in the config. For easy reloading.
     */
    public abstract void onConfigLoaded();

    @SuppressWarnings("SameReturnValue")
    public abstract boolean isEnabledByDefault();

    /**
     * This method is called once when the module loads. <p>
     * It is called after {@link #onConfigLoaded()} <p>
     * You should register commands, listeners, and tasks in this method.
     */
    public abstract void onEnable();

    /**
     * @return All KamiCommand objects that this module has. This Method is called ONCE when enabling the module.
     */
    public abstract List<KamiCommand> getCommands();

    final void registerCommands() {
        commandList.clear();

        List<KamiCommand> commands = getCommands();
        if (commands == null) { return; }
        commands.forEach(KamiCommand::registerCommand);
        commandList.addAll(commands);
    }

    /**
     * This method is called when/if ItemsAdder loads/reloads. <p>
     * It is always called after {@link #onEnable()} <p>
     * <p>
     * This may be called several times (if ItemsAdder is reloaded)
     */
    public void onItemsAdderLoaded() {}

    /**
     * This method is called when/if MythicMobs loads/reloads. <p>
     * It is always called after {@link #onEnable()} <p>
     * <p>
     * This may be called several times (if MythicMobs is reloaded)
     */
    public void onMythicMobsLoaded() {}

    /**
     * This method is called when/if Citizens loads/reloads. <p>
     * It is always called after {@link #onEnable()} <p>
     * <p>
     * This may be called several times (if Citizens is reloaded)
     */
    public void onCitizensLoaded() {}


    /**
     * This method is called when a module is shutting down (server shut down most likely) <p>
     */
    public abstract void onDisable();

    /**
     * @return The name of this module
     */
    public abstract String getName();

    /**
     * @return The default logging prefix for this module (saved to config under options.modulePrefix)
     */
    public abstract String defaultPrefix();


    // -------------------------------------------- //
    // MODULE CONFIG
    // -------------------------------------------- //
    private ModuleConfig moduleConfig = null;
    public final void reloadConfig() {
        moduleConfig.reload();
        moduleConfig.save();
        onConfigLoaded();
    }
    public void saveConfig() {
        moduleConfig.save();
    }
    public String getConfigName() {
        return getName() + "Module.yml";
    }
    public ModuleConfig getConfig() {
        if (moduleConfig == null) {
            @Nullable String moduleYmlPath = getPlugin().getModuleYmlPath();
            if (moduleYmlPath == null) {
                moduleConfig = new ModuleConfig(this, getConfigName());
            }else {
                if (!moduleYmlPath.endsWith("/")) { moduleYmlPath += "/"; }
                moduleConfig = new ModuleConfig(this, moduleYmlPath + getConfigName());
            }
        }
        return moduleConfig;
    }


    public boolean isEnabledInConfig() {
        if (getConfig() == null) {
            getPlugin().getLogger().warning("module (" + getConfigName() + ") config is null");
            return false;
        }

        return getConfig().getBoolean("enabled", isEnabledByDefault());
    }

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
        moduleConfig = null;
    }

    public final void onDisableLater() {
        // Unregister Listeners
        int listeners = listenerList.size();
        getPlugin().unregisterListener(listenerList.toArray(new Listener[0]));
        listenerList.clear();
        if (listeners > 0) {
            getPlugin().getLogger().info("[" + getName() + "] Unregistered " + listeners + " listeners.");
        }


        // Unregister Commands
        int c = unregisterCommands(commandList.toArray(new KamiCommand[0]));
        if (c > 0) {
            getPlugin().getLogger().info("[" + getName() + "] Unregistered " + c + " commands.");
        }


        // Unregister Tasks
        int tasks = taskList.size();
        getPlugin().unregisterTask(taskList.toArray(new BukkitTask[0]));
        taskList.clear();
        if (tasks > 0) {
            getPlugin().getLogger().info("[" + getName() + "] Unregistered " + tasks + " tasks.");
        }

        // Unregister Disableables
        int disableables = disableableList.size();
        getPlugin().unregisterDisableable(disableableList.toArray(new Disableable[0]));
        disableableList.clear();
        if (disableables > 0) {
            getPlugin().getLogger().info("[" + getName() + "] Disabled " + disableables + " disableables.");
        }
    }


    // Listener Methods
    public void registerListener(Listener... listeners) {
        getPlugin().registerListener(listeners);
        listenerList.addAll(Arrays.asList(listeners));
    }
    public void unregisterListener(Listener... listener) {
        getPlugin().unregisterListener(listener);
        listenerList.removeAll(Arrays.asList(listener));
    }
    // Task Methods
    public void registerTask(BukkitTask... tasks) {
        getPlugin().registerTask(tasks);
        taskList.addAll(Arrays.asList(tasks));
    }
    public void unregisterTask(BukkitTask... tasks) {
        getPlugin().unregisterTask(tasks);
        taskList.removeAll(Arrays.asList(tasks));
    }
    // Disableable Methods
    public void registerDisableable(Disableable... disableables) {
        getPlugin().registerDisableable(disableables);
        disableableList.addAll(Arrays.asList(disableables));
    }
    public void unregisterDisableable(Disableable... disableable) {
        getPlugin().unregisterDisableable(disableable);
        disableableList.removeAll(Arrays.asList(disableable));
    }



    private int unregisterCommands(KamiCommand... commands) {
        // Unregister the commands, then remove them from the list of all commands
        Arrays.stream(commands).forEach(KamiCommand::unregisterCommand);
        commandList.removeAll(Arrays.asList(commands));

        // Update the command registrations map, so that bukkit gets rid of them
        KamiCommonCommandRegistration.updateRegistrations();
        return commands.length;
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

    public final String getPrefix() {
        return getConfig().getString("options.modulePrefix");
    }

    /**
     * Builds a MessageBuilder using this Module's config and the provided key <p>
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
     * Builds a MessageBuilder using this Module's config and the provided key <p>
     * It will also automatically replace any {prefix} placeholders in the message with this module's prefix
     * @param key The key to get the message from the config
     * @return The MessageBuilder (see above)
     */
    public MessageBuilder getMessage(String key) {
        return buildMessage(key);
    }
}

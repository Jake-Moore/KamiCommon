package com.kamikazejam.kamicommon;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.configuration.spigot.ConfigObserver;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.util.interfaces.Disableable;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * A set of core methods that both {@link KamiPlugin} and {@link Module} must follow and implement to make their APIs interchangeable.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface CoreMethods {

    // Command Methods
    int registerCommands(KamiCommand... commands);
    int unregisterCommands(KamiCommand... commands);
    int unregisterCommands();

    // Config Methods
    void reloadConfig();
    void saveConfig();
    @NotNull KamiConfigExt getKamiConfig();

    // Listener Methods
    int registerListeners(Listener... listeners);
    int unregisterListeners(Listener... listeners);
    int unregisterListeners();

    // Task Methods
    int registerTasks(BukkitTask... tasks);
    int unregisterTasks(BukkitTask... tasks);
    int unregisterTasks();

    // Disableable Methods
    int registerDisableables(Disableable... disableables);
    int unregisterDisableables(Disableable... disableables);
    int unregisterDisableables();

    // Misc Methods
    boolean registerConfigObserver(@NotNull ConfigObserver observer, @NotNull KamiConfig config);
}

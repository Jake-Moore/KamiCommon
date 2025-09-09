package com.kamikazejam.kamicommon.subsystem.module.commands;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.subsystem.AbstractSubsystem;
import com.kamikazejam.kamicommon.subsystem.commands.CmdSubsystems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * KamiCommand implementation that lists all modules in the provided {@link KamiPlugin} (see constructor).<br>
 * Construct an instance of this class and register it under your own {@link KamiCommand} class.
 */
@SuppressWarnings("unused")
public class CmdModules extends CmdSubsystems {
    public CmdModules(@NotNull KamiPlugin plugin, @Nullable String permission) {
        this(plugin, permission, "modules");
    }

    public CmdModules(@NotNull KamiPlugin plugin, @Nullable String permission, @NotNull String... aliases) {
        super(plugin, permission,"List all modules", supplyModules(), "Modules", aliases);
    }

    private static @NotNull Function<KamiPlugin, List<AbstractSubsystem<?,?>>> supplyModules() {
        return (plugin) -> new ArrayList<>(plugin.getModuleManager().getModuleList());
    }
}

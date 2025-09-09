package com.kamikazejam.kamicommon.subsystem.feature.commands;

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
 * KamiCommand implementation that lists all features in the provided {@link KamiPlugin} (see constructor).<br>
 * Construct an instance of this class and register it under your own {@link KamiCommand} class.
 */
@SuppressWarnings("unused")
public class CmdFeatures extends CmdSubsystems {
    public CmdFeatures(@NotNull KamiPlugin plugin, @Nullable String permission) {
        this(plugin, permission, "features");
    }

    public CmdFeatures(@NotNull KamiPlugin plugin, @Nullable String permission, @NotNull String... aliases) {
        super(plugin, permission,"List all features", supplyFeatures(), "Features", aliases);
    }

    private static @NotNull Function<KamiPlugin, List<AbstractSubsystem<?,?>>> supplyFeatures() {
        return (plugin) -> new ArrayList<>(plugin.getFeatureManager().getFeatureList());
    }
}

package com.kamikazejam.kamicommon.command;

import com.avaje.ebean.validation.NotNull;
import com.kamikazejam.kamicommon.command.impl.KamiUpdateCmd;
import com.kamikazejam.kamicommon.command.impl.KamiVerCmd;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class KamiSubCommands {
    private final JavaPlugin plugin;
    private final boolean versionCmd, updateCmd;
    private final List<KamiSubCommand> subCommands = new ArrayList<>();

    public KamiSubCommands(JavaPlugin plugin) {
        this.plugin = plugin;
        this.versionCmd = false;
        this.updateCmd = false;
    }

    public KamiSubCommands(JavaPlugin plugin, boolean versionCmd) {
        this.plugin = plugin;
        this.versionCmd = versionCmd;
        this.updateCmd = false;
    }

    public KamiSubCommands(JavaPlugin plugin, boolean versionCmd, boolean updateCmd) {
        this.plugin = plugin;
        this.versionCmd = versionCmd;
        this.updateCmd = updateCmd;
    }

    public abstract List<KamiSubCommand> getSubCommands();

    public abstract KamiSubCommand getNoneSubCommand();

    public @NotNull KamiSubCommand fromName(String name) {
        if (subCommands.isEmpty()) {
            this.subCommands.addAll(getSubCommands());

            if (versionCmd) {
                subCommands.add(new KamiVerCmd(plugin));
            }
            if (updateCmd) {
                subCommands.add(new KamiUpdateCmd());
            }
        }

        for (KamiSubCommand commandEnum : subCommands) {
            for (String n : commandEnum.getNames()) {
                if (n.equalsIgnoreCase(name)) {
                    return commandEnum;
                }
            }
        }
        return getNoneSubCommand();
    }
}

package com.kamikazejamplugins.kamicommon.command;

import com.avaje.ebean.validation.NotNull;
import com.kamikazejamplugins.kamicommon.command.impl.KamiUpdateCmd;
import com.kamikazejamplugins.kamicommon.command.impl.KamiVerCmd;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class KamiSubCommands {
    private final boolean versionCmd, updateCmd;
    private final List<KamiSubCommand> subCommands = new ArrayList<>();

    public KamiSubCommands(boolean versionCmd, boolean updateCmd) {
        this.versionCmd = versionCmd;
        this.updateCmd = updateCmd;
    }

    public abstract List<KamiSubCommand> getSubCommands();

    public abstract KamiSubCommand getNoneSubCommand();

    protected @NotNull KamiSubCommand fromName(String name) {
        if (subCommands.isEmpty()) {
            this.subCommands.addAll(getSubCommands());

            if (versionCmd) {
                subCommands.add(new KamiVerCmd());
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

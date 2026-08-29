package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.impl.KamiCommandVersion;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import org.jetbrains.annotations.NotNull;

public class KamiCommonCommand extends KamiCommand {
    public KamiCommonCommand(@NotNull KamiPlugin plugin) {
        addAliases("kamicommon", "kc");

        addRequirements(RequirementHasPerm.get("kamicommon.command.help"));

        addChild(new KamiCommandVersion());
        addChild(new CmdNmsVersion());
        addChild(new CmdNmsProviders());
        addChild(new CmdNmsTest(plugin));
        addChild(new CmdOpenMenu());
        addChild(new CmdReload());
        addChild(new CmdGetItem());
        addChild(new CmdSaveItem());
        addChild(new CmdLoadItem());
        addChild(new CmdItemDump());
        addChild(new CmdTestMsg());
    }
}

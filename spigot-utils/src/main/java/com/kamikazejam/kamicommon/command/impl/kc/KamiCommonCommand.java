package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.impl.KamiCommandVersion;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;

public class KamiCommonCommand extends KamiCommand {
    public KamiCommonCommand() {
        addAliases("kamicommon", "kc");

        addRequirements(RequirementHasPerm.get("kamicommon.command.help"));

        addChild(new KamiCommandVersion());
        addChild(new CmdNmsVersion());
        addChild(new CmdNmsTest());
        addChild(new CmdOpenMenu());
        addChild(new CmdReload());
        addChild(new CmdGetItem());
        addChild(new CmdSaveItem());
        addChild(new CmdLoadItem());
        addChild(new CmdItemDump());
    }
}

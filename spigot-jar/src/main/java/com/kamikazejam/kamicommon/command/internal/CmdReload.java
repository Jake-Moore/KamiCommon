package com.kamikazejam.kamicommon.command.internal;

import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.util.StringUtil;

public class CmdReload extends KamiCommand {
    public CmdReload() {
        addAliases("reload");
        addRequirements(RequirementHasPerm.get("kamicommon.command.reload"));
    }

    @Override
    public void perform() {
        KamiCommon.get().reloadConfig();
        KamiCommon.get().getKamiConfig().reload();
        sender.sendMessage(StringUtil.t("&c[KamiCommon] Reloaded."));
    }
}

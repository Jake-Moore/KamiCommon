package com.kamikazejam.kamicommon.command.internal;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.util.StringUtil;

@SuppressWarnings("SpellCheckingInspection")
public class CmdNmsVersion extends KamiCommand {
    public CmdNmsVersion() {
        addAliases("nmsversion", "nmsv", "nmsver");

        addRequirements(RequirementHasPerm.get("kamicommon.command.nmsversion"));
    }

    @Override
    public void perform() {
        sender.sendMessage(StringUtil.t("&7NMS Version: &f" + NmsVersion.getMCVersion() + " &7(&f" + NmsVersion.getFormattedNmsInteger() + "&7)"));
    }
}

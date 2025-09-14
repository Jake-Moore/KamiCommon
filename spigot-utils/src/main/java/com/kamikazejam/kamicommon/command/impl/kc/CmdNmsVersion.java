package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class CmdNmsVersion extends KamiCommand {
    public CmdNmsVersion() {
        addAliases("nmsversion", "nmsv", "nmsver");

        addRequirements(RequirementHasPerm.get("kamicommon.command.nmsversion"));
    }

    @Override
    public void perform(@NotNull CommandContext context) {
        NmsAPI.getVersionedComponentSerializer().fromMiniMessage(
                "<gray>NMS Version: <white>" + NmsVersion.getMCVersion() + " <gray>(<white>" + NmsVersion.getFormattedNmsInteger() + "<gray>)"
        ).sendTo(context.getSender());
    }
}

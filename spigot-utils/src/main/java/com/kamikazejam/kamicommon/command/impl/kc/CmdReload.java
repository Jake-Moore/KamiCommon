package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import org.jetbrains.annotations.NotNull;

public class CmdReload extends KamiCommand {
    public CmdReload() {
        addAliases("reload");
        addRequirements(RequirementHasPerm.get("kamicommon.command.reload"));
    }

    @Override
    public void perform(@NotNull CommandContext context) {
        SpigotUtilsSource.get().reloadConfig();
        SpigotUtilsSource.getKamiConfig().reload();
        NmsAPI.getVersionedComponentSerializer().fromMiniMessage(
                "<green>[KamiCommon] Reloaded."
        ).sendTo(context.getSender());
    }
}

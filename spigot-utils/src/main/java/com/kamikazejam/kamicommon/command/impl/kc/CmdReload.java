package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
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
        SpigotUtilsSource.get().reloadConfig();
        SpigotUtilsSource.getKamiConfig().reload();
        sender.sendMessage(StringUtil.t("&a[KamiCommon] Reloaded."));
    }
}

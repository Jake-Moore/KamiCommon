package com.kamikazejam.kamicommon.command.internal;

import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.gui.KamiMenuContainer;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.entity.Player;

@SuppressWarnings("SpellCheckingInspection")
public class CmdOpenGUI extends KamiCommand {
    public CmdOpenGUI() {
        addAliases("opengui");

        addParameter(TypeString.get(), "config key");

        addRequirements(RequirementHasPerm.get("kamicommon.command.opengui"));
        addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() throws KamiCommonException {
        Player player = (Player) sender;
        String guiKey = readArg();
        KamiMenuContainer menu = new KamiMenuContainer(KamiCommon.get().getKamiConfig(), guiKey);
        for (String key : menu.getMenuItemMap().keySet()) {
            menu.addMenuClick(key, (p, c) -> {
                p.sendMessage(StringUtil.t("&7Menu Item Click (&f" + c.name() + "&7) on &f" + key));
            });
        }
        for (String key : menu.getPagedItemMap().keySet()) {
            menu.addMenuClick(key, (p, c) -> {
                p.sendMessage(StringUtil.t("&7Page Item Click (&f" + c.name() + "&7) on &f" + key));
            });
        }
        menu.openMenu(player);
    }
}

package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Parameter;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.menu.SimpleMenu;
import com.kamikazejam.kamicommon.menu.api.loaders.SimpleMenuLoader;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.entity.Player;

@SuppressWarnings({"DuplicatedCode"})
public class CmdOpenMenu extends KamiCommand {
    public CmdOpenMenu() {
        addAliases("openmenu");

        addParameter(Parameter.of(TypeString.get()).name("config key"));

        addRequirements(RequirementHasPerm.get("kamicommon.command.openmenu"));
        addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() throws KamiCommonException {
        Player player = (Player) sender;
        String menuKey = readArg();

        SimpleMenu.Builder<?> builder = SimpleMenuLoader.loadMenu(SpigotUtilsSource.getKamiConfig(), menuKey);
        builder.modifyIcons((access) -> {
            for (String id : access.getMenuIconIDs()) {
                access.setMenuClick(id, (p, c) -> p.sendMessage(StringUtil.t("&7Menu Item Click (&f" + c.name() + "&7) on &f" + id)));
            }
        });

        builder.build(player).open();
    }
}

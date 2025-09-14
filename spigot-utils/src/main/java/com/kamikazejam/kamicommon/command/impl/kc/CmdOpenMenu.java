package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Parameter;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.menu.SimpleMenu;
import com.kamikazejam.kamicommon.menu.api.loaders.menu.SimpleMenuLoader;
import com.kamikazejam.kamicommon.util.LegacyColors;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"DuplicatedCode"})
public class CmdOpenMenu extends KamiCommand {
    public CmdOpenMenu() {
        addAliases("openmenu");

        addParameter(Parameter.of(TypeString.get()).name("config key"));

        addRequirements(RequirementHasPerm.get("kamicommon.command.openmenu"));
        addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform(@NotNull CommandContext context) throws KamiCommonException {
        Player player = (Player) context.getSender();
        String menuKey = readArg();

        SimpleMenu.Builder builder = SimpleMenuLoader.loadMenu(SpigotUtilsSource.getKamiConfig(), menuKey);
        builder.modifyIcons((access) -> {
            for (String id : access.getMenuIconIDs()) {
                access.setMenuClick(id, (data) -> data.getPlayer().sendMessage(LegacyColors.t("&7Menu Item Click (&f" + data.getClickType().name() + "&7) on &f" + id)));
            }
        });

        builder.build(player).open();
    }
}

package com.kamikazejam.kamicommon.command.internal;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.util.PlayerUtil;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.entity.Player;

@SuppressWarnings("SpellCheckingInspection")
public class CmdGetItem extends KamiCommand {
    public CmdGetItem() {
        addAliases("getitem");

        addParameter(TypeString.get(), "config key");

        addRequirements(RequirementHasPerm.get("kamicommon.command.getitem"));
        addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() throws KamiCommonException {
        Player player = (Player) sender;
        String guiKey = readArg();
        ItemBuilder builder = new ItemBuilder(PluginSource.getKamiConfig().getConfigurationSection(guiKey), player);
        PlayerUtil.giveItem(player, builder.build());
        player.sendMessage(StringUtil.t("&aGave Item: &f" + guiKey));
    }
}

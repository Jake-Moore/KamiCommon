package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Parameter;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.util.PlayerUtil;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("SpellCheckingInspection")
public class CmdGetItem extends KamiCommand {
    public CmdGetItem() {
        addAliases("getitem");

        addParameter(Parameter.of(TypeString.get()).name("config key"));

        addRequirements(RequirementHasPerm.get("kamicommon.command.getitem"));
        addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() throws KamiCommonException {
        Player player = (Player) sender;
        String itemKey = readArg();
        KamiConfig config = SpigotUtilsSource.getKamiConfig();
        if (!config.isConfigurationSection(itemKey)) {
            player.sendMessage(StringUtil.t("&cInvalid Item Key: &f" + itemKey));
            return;
        }

        ItemStack stack = new ItemBuilder(config.getConfigurationSection(itemKey), player).build();
        PlayerUtil.giveItem(player, stack);
        player.sendMessage(StringUtil.t("&aGave Item: &f" + itemKey));
    }
}

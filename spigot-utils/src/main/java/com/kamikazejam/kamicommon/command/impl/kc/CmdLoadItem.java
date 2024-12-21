package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.util.PlayerUtil;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

@SuppressWarnings("SpellCheckingInspection")
public class CmdLoadItem extends KamiCommand {
    public CmdLoadItem() {
        addAliases("loaditem");

        addParameter(TypeString.get(), "config key");
        addParameter(TypeString.get(), "file name", true);

        addRequirements(RequirementHasPerm.get("kamicommon.command.getitem"));
        addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() throws KamiCommonException {
        Player player = (Player) sender;

        String configKey = readArg();

        String fileName = readArg();
        File file = new File(SpigotUtilsSource.get().getDataFolder(), fileName);

        KamiConfig config = new KamiConfigExt(SpigotUtilsSource.get(), file, false);
        ItemStack stack = config.getItemStack(configKey);
        if (stack == null) {
            player.sendMessage(StringUtil.t("&cNo item found in &f" + file.getAbsolutePath() + "&c: &f" + configKey));
            return;
        }
        PlayerUtil.giveItem(player, stack);
        player.sendMessage(StringUtil.t("&aLoaded and Gave Item"));
    }
}

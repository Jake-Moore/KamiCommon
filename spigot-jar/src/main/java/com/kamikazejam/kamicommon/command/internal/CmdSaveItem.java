package com.kamikazejam.kamicommon.command.internal;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.configuration.config.KamiConfig;
import com.kamikazejam.kamicommon.configuration.config.KamiConfigExt;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@SuppressWarnings("SpellCheckingInspection")
public class CmdSaveItem extends KamiCommand {
    public CmdSaveItem() {
        addAliases("saveitem");

        addParameter(TypeString.get(), "config key");
        addParameter(TypeString.get(), "file name", true);

        addRequirements(RequirementHasPerm.get("kamicommon.command.getitem"));
        addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() throws KamiCommonException {
        Player player = (Player) sender;
        @Nullable ItemStack mainHand = NmsAPI.getItemInMainHand(player);
        if (mainHand == null) {
            player.sendMessage(StringUtil.t("&cYou must be holding an item to save it!"));
            return;
        }
        String configKey = readArg();

        String fileName = readArg();
        File file = new File(PluginSource.get().getDataFolder(), fileName);

        KamiConfig config = new KamiConfigExt(PluginSource.get(), file, false);
        config.setItemStack(configKey, NmsAPI.getItemInMainHand(player));
        config.save();

        player.sendMessage(StringUtil.t("&aSaved Item to &f" + file.getAbsolutePath() + "&a: &f" + configKey));
    }
}

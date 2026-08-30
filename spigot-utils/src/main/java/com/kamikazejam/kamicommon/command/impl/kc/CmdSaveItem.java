package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Parameter;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@SuppressWarnings("SpellCheckingInspection")
public class CmdSaveItem extends KamiCommand {
    public CmdSaveItem() {
        addAliases("saveitem");

        addParameter(Parameter.of(TypeString.get()).name("config key"));
        addParameter(Parameter.of(TypeString.get()).name("file name").concatFromHere(true));

        addRequirements(RequirementHasPerm.get("kamicommon.command.getitem"));
        addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform(@NotNull CommandContext context) throws KamiCommonException {
        Player player = (Player) context.getSender();
        @Nullable ItemStack mainHand = NmsAPI.getItemInMainHand(player);
        if (mainHand == null) {
            NmsAPI.getVersionedComponentSerializer().fromMiniMessage(
                    "<red>You must be holding an item to save it!"
            ).sendTo(player);
            return;
        }
        String configKey = readArg();

        String fileName = readArg();
        File file = new File(SpigotUtilsSource.get().getDataFolder(), fileName);

        // Use defaultsStream=null since there's probably not a default config for this file
        KamiConfig config = new KamiConfigExt(SpigotUtilsSource.get(), file, null);
        config.setItemStack(configKey, NmsAPI.getItemInMainHand(player));
        config.save();

        NmsAPI.getVersionedComponentSerializer().fromMiniMessage(
                "<green>Saved Item to <white>" + file.getAbsolutePath() + "<green>: <white>" + configKey
        ).sendTo(player);
    }
}

package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.actions.Action;
import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageSingle;
import com.kamikazejam.kamicommon.util.StringUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class CmdTestMsg extends KamiCommand {
    public CmdTestMsg() {
        addAliases("testmsg");

        addRequirements(RequirementHasPerm.get("kamicommon.command.testmsg"));
        addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform(@NotNull CommandContext context) {
        Player player = (Player) context.getSender();
        player.sendMessage(StringUtil.t("&7Sending Test KMessageSingle..."));

        KMessageSingle kMessage = new KMessageSingle("§f{display_name} §8» §7test [item]");
        kMessage.setTranslate(true);
        kMessage.addAction(new Action("{display_name}", "KamikazeJAM"));
        kMessage.addAction(new Action("[item]", "§f§2§lHarvester Hoe§f x1"));
        kMessage.addAction(new Action("[anything]", "&cany&4thing"));

        NmsAPI.getMessageManager().processAndSend(player, kMessage);

        Logger logger = SpigotUtilsSource.get().getLogger();
        logger.info("==================== DEBUG ====================");
        logger.info("Message: '" + kMessage.getLine() + "' from " + player.getName());
        logger.info("Translate? " + kMessage.isTranslate());
        for (Action action : kMessage.getActions()) {
            logger.info(
                    "Action: p: '" + action.getPlaceholder() + "' r: '" +
                            action.getReplacement() + "' c: '" +
                            action.getClass().getSimpleName() + "'"
            );
        }
        logger.info("==================== DEBUG ====================");
    }
}

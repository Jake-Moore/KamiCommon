package com.kamikazejam.kamicommon.command.internal;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.gui.container.KamiMenuContainer;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        if (guiKey.equalsIgnoreCase("paged")) {
            this.openPaged(player);
            return;
        }

        KamiMenuContainer menu = new KamiMenuContainer(PluginSource.getKamiConfig(), guiKey);
        for (String key : menu.getIconKeys()) {
            menu.setMenuClick(key, (p, c) ->
                    p.sendMessage(StringUtil.t("&7Menu Item Click (&f" + c.name() + "&7) on &f" + key)));
        }
        for (String key : menu.getPagedIconKeys()) {
            menu.setMenuClick(key, (p, c) ->
                    p.sendMessage(StringUtil.t("&7Page Item Click (&f" + c.name() + "&7) on &f" + key)));
        }
        menu.openMenu(player);
    }

    private void openPaged(@NotNull Player player) {
        KamiMenuContainer container = new KamiMenuContainer("&8&lSample Paged Menu", 5).setOrdered(true);
        for (int i = 1; i < 45; i++) {
            String key = "Item" + i;
            IBuilder builder = new ItemBuilder(XMaterial.STONE)
                    .setName("&fItem &7#" + i);
            container.addPagedIcon(key, builder);
            container.setMenuClick(key, (p, c) -> {
                p.sendMessage(StringUtil.t("&7Page Item Click (&f" + c.name() + "&7) on &f" + key));
            });
        }
        container.setFillerItem(new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE).setName("&7")).openMenu(player);
    }
}

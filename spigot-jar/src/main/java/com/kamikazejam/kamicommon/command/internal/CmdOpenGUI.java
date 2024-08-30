package com.kamikazejam.kamicommon.command.internal;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.gui.KamiMenu;
import com.kamikazejam.kamicommon.gui.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.gui.loader.KamiMenuLoader;
import com.kamikazejam.kamicommon.gui.page.PagedKamiMenu;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"SpellCheckingInspection", "DuplicatedCode"})
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
        if (guiKey.equalsIgnoreCase("paged2")) {
            this.openPaged2(player);
            return;
        }

        KamiMenu menu = KamiMenuLoader.loadMenu(PluginSource.getKamiConfig(), guiKey);
        for (String id : menu.getMenuItemIDs()) {
            menu.setMenuClick(id, (p, c) -> p.sendMessage(StringUtil.t("&7Menu Item Click (&f" + c.name() + "&7) on &f" + id)));
        }
        menu.openMenu(player);
    }

    private void openPaged(@NotNull Player player) {
        // Add paged items
        PagedKamiMenu paged = new PagedKamiMenu(new KamiMenu("&8&lSample Paged Menu", 5));
        for (int i = 1; i < 45; i++) {
            String id = "Item" + i;
            IBuilder builder = new ItemBuilder(XMaterial.STONE).setName("&fItem &7#" + i);
            paged.addPagedItem(builder, i).setMenuClick((p, c) ->
                    p.sendMessage(StringUtil.t("&7Paged Item Click (&f" + c.name() + "&7) on &f" + id))
            );
        }
        paged.applyToParent(0).fill().openMenu(player);
    }

    private void openPaged2(@NotNull Player player) {
        // Add paged items
        PagedKamiMenu paged = new PagedKamiMenu(new KamiMenu("&8&lSample Paged Menu", 6));
        paged.setPageSlots(List.of(14, 15, 16, 23, 24, 25, 32, 33, 34, 41, 42, 43));
        paged.getNextPageIcon().setItemSlot(new StaticItemSlot(52));
        paged.getPrevPageIcon().setItemSlot(new StaticItemSlot(50));

        for (int i = 1; i < 45; i++) {
            String id = "Item" + i;
            IBuilder builder = new ItemBuilder(XMaterial.STONE).setName("&fItem &7#" + i);
            paged.addPagedItem(builder, i).setMenuClick((p, c) ->
                    p.sendMessage(StringUtil.t("&7Paged Item Click (&f" + c.name() + "&7) on &f" + id))
            );
        }
        paged.applyToParent(0).fill().openMenu(player);
    }
}

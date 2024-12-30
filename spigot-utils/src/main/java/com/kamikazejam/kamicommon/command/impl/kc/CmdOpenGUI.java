package com.kamikazejam.kamicommon.command.impl.kc;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Parameter;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.gui.KamiMenu;
import com.kamikazejam.kamicommon.gui.items.MenuItem;
import com.kamikazejam.kamicommon.gui.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.gui.loader.KamiMenuLoader;
import com.kamikazejam.kamicommon.gui.page.PagedKamiMenu;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@SuppressWarnings({"SpellCheckingInspection", "DuplicatedCode"})
public class CmdOpenGUI extends KamiCommand {
    public CmdOpenGUI() {
        addAliases("opengui");

        addParameter(Parameter.of(TypeString.get()).name("config key"));

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
        if (guiKey.equalsIgnoreCase("test1")) {
            this.openTest1(player);
            return;
        }

        KamiMenu menu = KamiMenuLoader.loadMenu(SpigotUtilsSource.getKamiConfig(), guiKey);
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
            paged.addPagedItem(builder).setMenuClick((p, c) ->
                    p.sendMessage(StringUtil.t("&7Paged Item Click (&f" + c.name() + "&7) on &f" + id))
            );
        }
        KamiMenu menu = paged.applyToParent(0);
        ItemStack stackWithMeta = new ItemStack(Material.STONE);
        ItemMeta meta = stackWithMeta.getItemMeta();
        meta.setDisplayName(StringUtil.t("&a&lRandom Name: " + UUID.randomUUID()));
        meta.setLore(List.of(StringUtil.t("&7This is a random lore line."), StringUtil.t("&7This is another random lore line.")));
        stackWithMeta.setItemMeta(meta);
        menu.addMenuItem("RandomItem", stackWithMeta, 0).setMenuClick((p, c) ->
                p.sendMessage(StringUtil.t("&7Menu Item Click (&f" + c.name() + "&7) on &fRandomItem"))
        );

        menu.openMenu(player);
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
            paged.addPagedItem(builder).setMenuClick((p, c) ->
                    p.sendMessage(StringUtil.t("&7Paged Item Click (&f" + c.name() + "&7) on &f" + id))
            );
        }
        paged.applyToParent(0).openMenu(player);
    }

    private void openTest1(@NotNull Player player) {
        KamiMenu menu = new KamiMenu("&8&lTest Menu", 3);

        // Add an item with rotating builders
        List<IBuilder> builders = List.of(
                new ItemBuilder(XMaterial.STONE, 1).setName("&fStone"),
                new ItemBuilder(XMaterial.DIAMOND, 1).setName("&bDiamond"),
                new ItemBuilder(XMaterial.EMERALD, 1).setName("&aEmerald")
        );
        builders.forEach(builder -> builder.setLore("&7This is a lore with the time: {time}"));

        // Rotate every 3 seconds
        menu.addMenuItem(new MenuItem(true, 13, builders).setId("TEST").setBuilderRotateTicks(60));
        // Update the time every 1 second
        menu.setAutoUpdate("TEST", (builder) ->
                builder.replaceBoth("{time}", String.valueOf(System.currentTimeMillis()))
        , 20);

        // When clicked, update the item (on demand)
        menu.setMenuClick("TEST", (p, c) -> {
            p.sendMessage(StringUtil.t("&7Menu Item Click (&f" + c.name() + "&7) on &fTEST"));
            menu.updateItem("TEST");
        });

        menu.openMenu(player);
    }
}

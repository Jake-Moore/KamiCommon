package com.kamikazejam.kamicommon.command.impl.kc;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Parameter;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.command.type.primitive.TypeString;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.entity.Player;

// TODO re-implement
@SuppressWarnings({"SpellCheckingInspection", "DuplicatedCode"})
public class CmdOpenMenu extends KamiCommand {
    public CmdOpenMenu() {
        addAliases("openmenu");

        addParameter(Parameter.of(TypeString.get()).name("config key"));

        addRequirements(RequirementHasPerm.get("kamicommon.command.openmenu"));
        addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() throws KamiCommonException {
        Player player = (Player) sender;
        player.sendMessage(StringUtil.t("&cCOMMAND COMING SOON!")); // TODO

//        String menuKey = readArg();
//        if (menuKey.equalsIgnoreCase("paged")) {
//            this.openPaged(player);
//            return;
//        }
//        if (menuKey.equalsIgnoreCase("paged2")) {
//            this.openPaged2(player);
//            return;
//        }
//        if (menuKey.equalsIgnoreCase("test1")) {
//            this.openTest1(player);
//            return;
//        }
//
//        OLD_KAMI_MENU menu = KamiMenuLoader.loadMenu(SpigotUtilsSource.getKamiConfig(), menuKey);
//        for (String id : menu.getMenuItemIDs()) {
//            menu.setMenuClick(id, (p, c) -> p.sendMessage(StringUtil.t("&7Menu Item Click (&f" + c.name() + "&7) on &f" + id)));
//        }
//        menu.openMenu(player);
    }

//    private void openPaged(@NotNull Player player) {
//        // Add paged items
//        OLD_PAGED_KAMI_MENU paged = new OLD_PAGED_KAMI_MENU(new OLD_KAMI_MENU("&8&lSample Paged Menu", 5));
//        for (int i = 1; i < 45; i++) {
//            String id = "Item" + i;
//            IBuilder builder = new ItemBuilder(XMaterial.STONE).setName("&fItem &7#" + i);
//            paged.addPagedItem(builder).setMenuClick((p, c) ->
//                    p.sendMessage(StringUtil.t("&7Paged Item Click (&f" + c.name() + "&7) on &f" + id))
//            );
//        }
//        OLD_KAMI_MENU menu = paged.applyToParent(0);
//        ItemStack stackWithMeta = new ItemStack(Material.STONE);
//        ItemMeta meta = stackWithMeta.getItemMeta();
//        meta.setDisplayName(StringUtil.t("&a&lRandom Name: " + UUID.randomUUID()));
//        meta.setLore(List.of(StringUtil.t("&7This is a random lore line."), StringUtil.t("&7This is another random lore line.")));
//        stackWithMeta.setItemMeta(meta);
//        menu.addMenuItem("RandomItem", stackWithMeta, 0).setMenuClick((p, c) ->
//                p.sendMessage(StringUtil.t("&7Menu Item Click (&f" + c.name() + "&7) on &fRandomItem"))
//        );
//
//        menu.openMenu(player);
//    }
//
//    private void openPaged2(@NotNull Player player) {
//        // Add paged items
//        OLD_PAGED_KAMI_MENU paged = new OLD_PAGED_KAMI_MENU(new OLD_KAMI_MENU("&8&lSample Paged Menu", 6));
//        paged.setPageSlots(List.of(14, 15, 16, 23, 24, 25, 32, 33, 34, 41, 42, 43));
//        paged.getNextPageIcon().setItemSlot(new StaticItemSlot(52));
//        paged.getPrevPageIcon().setItemSlot(new StaticItemSlot(50));
//
//        for (int i = 1; i < 45; i++) {
//            String id = "Item" + i;
//            IBuilder builder = new ItemBuilder(XMaterial.STONE).setName("&fItem &7#" + i);
//            paged.addPagedItem(builder).setMenuClick((p, c) ->
//                    p.sendMessage(StringUtil.t("&7Paged Item Click (&f" + c.name() + "&7) on &f" + id))
//            );
//        }
//        paged.applyToParent(0).openMenu(player);
//    }
//
//    private void openTest1(@NotNull Player player) {
//        OLD_KAMI_MENU menu = new OLD_KAMI_MENU("&8&lTest Menu", 3);
//
//        // Add an item with rotating builders
//        List<IBuilder> builders = List.of(
//                new ItemBuilder(XMaterial.STONE, 1).setName("&fStone"),
//                new ItemBuilder(XMaterial.DIAMOND, 1).setName("&bDiamond"),
//                new ItemBuilder(XMaterial.EMERALD, 1).setName("&aEmerald")
//        );
//        builders.forEach(builder -> builder.setLore("&7This is a lore with the time: {time}"));
//
//        // Rotate every 3 seconds
//        menu.addMenuItem(new MenuItem(true, 13, builders).setId("TEST").setBuilderRotateTicks(60));
//        // Update the time every 1 second
//        menu.setAutoUpdate("TEST", (builder) ->
//                builder.replaceBoth("{time}", String.valueOf(System.currentTimeMillis()))
//        , 20);
//
//        // When clicked, update the item (on demand)
//        menu.setMenuClick("TEST", (p, c) -> {
//            p.sendMessage(StringUtil.t("&7Menu Item Click (&f" + c.name() + "&7) on &fTEST"));
//            menu.updateItem("TEST");
//        });
//
//        menu.openMenu(player);
//    }
}

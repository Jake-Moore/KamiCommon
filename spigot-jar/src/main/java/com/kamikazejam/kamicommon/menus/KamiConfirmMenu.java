package com.kamikazejam.kamicommon.menus;

import com.kamikazejam.kamicommon.gui.container.KamiMenuContainer;
import com.kamikazejam.kamicommon.gui.clicks.MenuClick;
import com.kamikazejam.kamicommon.gui.clicks.MenuClickPage;
import com.kamikazejam.kamicommon.gui.items.MenuItem;
import com.kamikazejam.kamicommon.gui.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class KamiConfirmMenu {
    @Getter private final KamiMenuContainer container;
    @Nullable private IBuilder infoIcon = null;

    public KamiConfirmMenu(@NotNull String title) {
        container = new KamiMenuContainer(title, 3);
        container.addIcon("confirm",
                new ItemBuilder(XMaterial.GREEN_WOOL, 1).setName("&aConfirm")
        , 11);
        container.addIcon("deny",
                new ItemBuilder(XMaterial.RED_WOOL, 1).setName("&cDeny")
        , 15);

        final XMaterial mat = XMaterial.GRAY_STAINED_GLASS_PANE;
        final IBuilder fillerItem = new ItemBuilder(mat, 1, mat.getData()).setName(" ");
        container.setFillerItem(fillerItem);
    }

    public MenuItem getConfirmItem() {
        return container.getItem("confirm");
    }

    public MenuItem getDenyItem() {
        return container.getItem("deny");
    }

    public KamiConfirmMenu setConfirmItemName(@NotNull String name) {
        getConfirmItem().directModifyBuilders(builder -> builder.setName(name));
        return this;
    }

    public KamiConfirmMenu setDenyItemName(@NotNull String name) {
        getDenyItem().directModifyBuilders(builder -> builder.setName(name));
        return this;
    }

    public KamiConfirmMenu setConfirmItemLore(@NotNull List<String> lore) {
        getConfirmItem().directModifyBuilders(builder -> builder.setLore(lore));
        return this;
    }
    public KamiConfirmMenu setConfirmItemLore(@NotNull String... lore) {
        getConfirmItem().directModifyBuilders(builder -> builder.setLore(lore));
        return this;
    }

    public KamiConfirmMenu setDenyItemLore(@NotNull List<String> lore) {
        getDenyItem().directModifyBuilders(builder -> builder.setLore(lore));
        return this;
    }
    public KamiConfirmMenu setDenyItemLore(@NotNull String... lore) {
        getDenyItem().directModifyBuilders(builder -> builder.setLore(lore));
        return this;
    }

    public KamiConfirmMenu setConfirmItemSlot(int slot) {
        getConfirmItem().setItemSlot(new StaticItemSlot(slot));
        return this;
    }
    public KamiConfirmMenu setConfirmItemSlots(@NotNull List<Integer> slots) {
        getConfirmItem().setItemSlot(new StaticItemSlot(slots));
        return this;
    }

    public KamiConfirmMenu setDenyItemSlot(int slot) {
        getDenyItem().setItemSlot(new StaticItemSlot(slot));
        return this;
    }
    public KamiConfirmMenu setDenyItemSlots(@NotNull List<Integer> slots) {
        getDenyItem().setItemSlot(new StaticItemSlot(slots));
        return this;
    }

    public KamiConfirmMenu setConfirmCallback(@NotNull MenuClick click) {
        container.setMenuClick("confirm", click);
        return this;
    }
    public KamiConfirmMenu setConfirmCallback(MenuClickPage click) {
        container.setMenuClick("confirm", click);
        return this;
    }

    public KamiConfirmMenu setDenyCallback(MenuClick denyCallback) {
        container.setMenuClick("deny", denyCallback);
        return this;
    }
    public KamiConfirmMenu setDenyCallback(MenuClickPage denyCallback) {
        container.setMenuClick("deny", denyCallback);
        return this;
    }

    public KamiConfirmMenu setFillerItem(@Nullable IBuilder iBuilder) {
        container.setFillerItem(iBuilder);
        return this;
    }
    public KamiConfirmMenu setFillerItem(@Nullable ItemStack itemStack) {
        container.setFillerItem(itemStack);
        return this;
    }

    public KamiConfirmMenu setInfoIcon(@Nullable IBuilder iBuilder) {
        this.infoIcon = iBuilder;
        return this;
    }

    public void open(Player player) {
        // Add the info icon if specified
        if (infoIcon != null) {
            container.addIcon("info", infoIcon, 13);
        }

        container.createKamiMenu(player).openMenu(player);
    }
}

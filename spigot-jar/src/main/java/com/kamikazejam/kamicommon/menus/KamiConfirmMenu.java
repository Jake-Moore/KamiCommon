package com.kamikazejam.kamicommon.menus;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.gui.KamiMenuContainer;
import com.kamikazejam.kamicommon.gui.interfaces.MenuClick;
import com.kamikazejam.kamicommon.gui.interfaces.MenuClickPlayer;
import com.kamikazejam.kamicommon.gui.items.KamiMenuItem;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class KamiConfirmMenu {
    @Getter private final KamiMenuContainer container;
    @Nullable private IBuilder infoIcon = null;

    public KamiConfirmMenu(String title) {
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

    public KamiMenuItem getConfirmItem() {
        return container.getItem("confirm");
    }

    public KamiMenuItem getDenyItem() {
        return container.getItem("deny");
    }

    public KamiConfirmMenu setConfirmItemName(String name) {
        getConfirmItem().getIBuilder().setName(name);
        return this;
    }

    public KamiConfirmMenu setDenyItemName(String name) {
        getDenyItem().getIBuilder().setName(name);
        return this;
    }

    public KamiConfirmMenu setConfirmItemLore(List<String> lore) {
        getConfirmItem().getIBuilder().setLore(lore);
        return this;
    }
    public KamiConfirmMenu setConfirmItemLore(String... lore) {
        getConfirmItem().getIBuilder().setLore(lore);
        return this;
    }

    public KamiConfirmMenu setDenyItemLore(List<String> lore) {
        getDenyItem().getIBuilder().setLore(lore);
        return this;
    }
    public KamiConfirmMenu setDenyItemLore(String... lore) {
        getDenyItem().getIBuilder().setLore(lore);
        return this;
    }

    public KamiConfirmMenu setConfirmItemSlot(int slot) {
        getConfirmItem().setSlot(slot);
        return this;
    }
    public KamiConfirmMenu setConfirmItemSlots(List<Integer> slots) {
        getConfirmItem().setSlots(slots);
        return this;
    }

    public KamiConfirmMenu setDenyItemSlot(int slot) {
        getDenyItem().setSlot(slot);
        return this;
    }
    public KamiConfirmMenu setDenyItemSlots(List<Integer> slots) {
        getDenyItem().setSlots(slots);
        return this;
    }

    public KamiConfirmMenu setConfirmCallback(MenuClick click) {
        container.addMenuClick("confirm", click);
        return this;
    }
    public KamiConfirmMenu setConfirmCallback(MenuClickPlayer click) {
        container.addMenuClick("confirm", click);
        return this;
    }

    public KamiConfirmMenu setDenyCallback(MenuClick denyCallback) {
        container.addMenuClick("deny", denyCallback);
        return this;
    }
    public KamiConfirmMenu setDenyCallback(MenuClickPlayer denyCallback) {
        container.addMenuClick("deny", denyCallback);
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

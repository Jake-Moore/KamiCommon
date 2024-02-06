package com.kamikazejam.kamicommon.gui;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.gui.interfaces.*;
import com.kamikazejam.kamicommon.item.IBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter @Setter
@SuppressWarnings("unused")
public abstract class AbstractKamiMenu extends MenuHolder implements Menu {
    public interface MenuOpenCallback {
        void onOpen(Player player, InventoryView view);
    }


    private final Set<String> ignoredClose = new HashSet<>();
    private final Map<MenuItem, MenuClickInfo> clickableItems = new ConcurrentHashMap<>();
    private Predicate<InventoryClickEvent> clickHandler;
    private Consumer<InventoryCloseEvent> closeHandler;
    private Consumer<InventoryCloseEvent> instantCloseHandler;
    private MenuUpdate updateHandler;
    private boolean clearBeforeUpdate = false;
    private boolean allowItemPickup;
    private @Nullable MenuOpenCallback openCallback = null;

    public AbstractKamiMenu() {}

    public AbstractKamiMenu(String name, int lines) {
        super(name, lines);
    }

    public AbstractKamiMenu(String name, InventoryType type) {
        super(name, type);
    }

    @Override
    public InventoryView openMenu(Player player) {
        return openMenu(player, false);
    }

    @Override
    public InventoryView openMenu(Player player, boolean ignoreCloseHandler) {
        update();

        if (ignoreCloseHandler) {
            getIgnoredClose().add(player.getName());
        }

        InventoryView view = player.openInventory(getInventory());
        if (openCallback != null) {
            openCallback.onOpen(player, view);
        }
        return view;
    }

    public int firstEmpty() {
        return getInventory().firstEmpty();
    }

    public int firstEmpty(List<Integer> slots) {
        return firstEmpty(slots.stream().mapToInt(i -> i).toArray());
    }

    @Override
    public int firstEmpty(int[] slots) {
        for (int s : slots) {
            try {
                if (s > getSize()) {
                    throw new IllegalStateException("Slot could fit in this inventory size.");
                }
                ItemStack slotStack = getInventory().getItem(s);
                if (slotStack == null || XMaterial.matchXMaterial(slotStack).equals(XMaterial.AIR)) {
                    return s;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    @Override
    public void setAutoUpdate() {
        MenuTask.getAutoUpdateInventories().add(this);
    }

    @Override
    public void closeInventory(Player player) {
        closeInventory(player, false);
    }

    @Override
    public void closeInventory(Player player, boolean onlyCloseOne) {
        if (!onlyCloseOne) {
            getIgnoredClose().add(player.getName());
        }

        player.closeInventory();
    }

    @Override
    public int getSize() {
        return getInventory().getSize();
    }

    @Override
    public ItemStack getItem(int slot) {
        return getInventory().getItem(slot);
    }

    @Override
    public void addMenuClick(IBuilder builder, MenuClick click, int slot, Player forPlaceholders) {
        addMenuClick(builder.toItemStack(forPlaceholders), click, slot);
    }

    @Override
    public void addMenuClick(IBuilder builder, MenuClickPlayer click, int slot, Player forPlaceholders) {
        addMenuClick(builder.toItemStack(forPlaceholders), click, slot);
    }

    @Override
    public void setUpdateHandler(MenuUpdate updateHandler) {
        this.updateHandler = updateHandler;
        MenuTask.getAutoUpdateInventories().add(this);
    }

    @Override
    public void setClearBeforeUpdate(boolean b) {
        this.clearBeforeUpdate = b;
    }

    public abstract void setItem(int slot, IBuilder stack, Player forPlaceholders);

    public void whenOpened(@Nullable MenuOpenCallback menuOpen) {
        this.openCallback = menuOpen;
    }
}

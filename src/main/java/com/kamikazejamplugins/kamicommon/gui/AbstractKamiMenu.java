package com.kamikazejamplugins.kamicommon.gui;

import com.kamikazejamplugins.kamicommon.gui.interfaces.Menu;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClick;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClickInfo;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuUpdate;
import com.kamikazejamplugins.kamicommon.item.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter @Setter
@SuppressWarnings("unused")
public abstract class AbstractKamiMenu<T extends Player> extends MenuHolder implements Menu<T> {

    private final Set<String> ignoredClose = new HashSet<>();
    private final Map<MenuItem, MenuClickInfo<T>> clickableItems = new ConcurrentHashMap<>();
    private Predicate<InventoryClickEvent> clickHandler;
    private Consumer<InventoryCloseEvent> closeHandler;
    private Consumer<InventoryCloseEvent> instantCloseHandler;
    private MenuUpdate updateHandler;
    private boolean allowItemPickup;

    public AbstractKamiMenu() {}

    public AbstractKamiMenu(String name, int lines) {
        super(name, lines);
    }

    @Override
    public void openMenu(Player player) {
        openMenu(player, false);
    }

    @Override
    public void openMenu(Player player, boolean ignoreCloseHandler) {
        update();

        if (ignoreCloseHandler) {
            getIgnoredClose().add(player.getName());
        }

        player.openInventory(getInventory());
    }

    public int firstEmpty(List<Integer> slots) {
        if (getInventory() == null) {
            return -1;
        }

        for (int s : slots) {
            try {
                if (s > getSize()) {
                    throw new IllegalStateException("Slot could fit in this inventory size.");
                }
                if (getInventory().getItem(s) == null || getInventory().getItem(s).getType() == Material.AIR) {
                    return s;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    @Override
    public int firstEmpty(int[] slots) {
        if (getInventory() == null) {
            return -1;
        }

        for (int s : slots) {
            try {
                if (s > getSize()) {
                    throw new IllegalStateException("Slot could fit in this inventory size.");
                }
                if (getInventory().getItem(s) == null || getInventory().getItem(s).getType() == Material.AIR) {
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
    public void addMenuClick(ItemBuilder builder, MenuClick click, int slot) {
        addMenuClick(builder.toItemStack(), click, slot);
    }

    @Override
    public void setUpdateHandler(MenuUpdate updateHandler) {
        this.updateHandler = updateHandler;
        MenuTask.getAutoUpdateInventories().add(this);
    }

}

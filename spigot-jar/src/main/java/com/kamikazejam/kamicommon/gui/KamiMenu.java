package com.kamikazejam.kamicommon.gui;

import com.kamikazejam.kamicommon.gui.clicks.MenuClick;
import com.kamikazejam.kamicommon.gui.clicks.MenuClickPage;
import com.kamikazejam.kamicommon.gui.clicks.transform.IClickTransform;
import com.kamikazejam.kamicommon.gui.clicks.transform.MenuClickPageTransform;
import com.kamikazejam.kamicommon.gui.clicks.transform.MenuClickTransform;
import com.kamikazejam.kamicommon.gui.interfaces.MenuUpdate;
import com.kamikazejam.kamicommon.gui.interfaces.MenuUpdateTask;
import com.kamikazejam.kamicommon.gui.page.PageBuilder;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter @Setter
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class KamiMenu extends MenuHolder {
    public interface MenuOpenCallback {
        void onOpen(@NotNull Player player, @NotNull InventoryView view);
    }

    // Menu Items
    private final @Nullable PageBuilder<?> parent;
    private final Map<MenuItem, IClickTransform> clickableItems = new ConcurrentHashMap<>();

    // Menu Callbacks
    private @Nullable Predicate<InventoryClickEvent> clickPredicate;
    private @Nullable Consumer<InventoryCloseEvent> preCloseConsumer = null;
    private @Nullable Consumer<Player> postCloseConsumer = null;
    private @Nullable MenuOpenCallback openCallback = null;

    // Menu Options
    private final Set<String> ignoredClose = new HashSet<>();
    private boolean allowItemPickup;

    // Menu Updates
    private @Nullable MenuUpdate updateHandler;
    private final List<MenuUpdateTask> updateSubTasks = new ArrayList<>();
    private boolean clearBeforeUpdate = false;

    public KamiMenu(@NotNull String name, int lines) {
        this(name, lines, null);
    }
    public KamiMenu(@NotNull String name, @NotNull InventoryType type) {
        this(name, type, null);
    }
    public KamiMenu(@NotNull String name, int lines, @Nullable PageBuilder<?> parent) {
        super(name, lines);
        this.parent = parent;
    }
    public KamiMenu(@NotNull String name, @NotNull InventoryType type, @Nullable PageBuilder<?> parent) {
        super(name, type);
        this.parent = parent;
    }

    @NotNull
    public InventoryView openMenu(@NotNull Player player) {
        return openMenu(player, false);
    }

    @NotNull
    public InventoryView openMenu(@NotNull Player player, boolean ignoreCloseHandler) {
        update();

        if (ignoreCloseHandler) {
            getIgnoredClose().add(player.getName());
        }

        InventoryView view = Objects.requireNonNull(player.openInventory(this.getInventory()));
        if (openCallback != null) {
            openCallback.onOpen(player, view);
        }
        return view;
    }

    public void closeInventory(@NotNull Player player) {
        closeInventory(player, false);
    }

    public void closeInventory(@NotNull Player player, boolean onlyCloseOne) {
        if (!onlyCloseOne) {
            getIgnoredClose().add(player.getName());
        }

        player.closeInventory();
    }


    public void addMenuClick(@NotNull IBuilder builder, @NotNull MenuClick click, int slot) {
        this.addMenuClick(builder.build(), click, slot);
    }
    public void addMenuClick(@NotNull IBuilder builder, @NotNull MenuClickPage click, int slot) {
        this.addMenuClick(builder.build(), click, slot);
    }
    public void addMenuClick(@NotNull ItemStack stack, @NotNull MenuClick click, int slot) {
        addClickTransform(stack, new MenuClickTransform(click), slot);
    }
    public void addMenuClick(@NotNull ItemStack stack, @NotNull MenuClickPage click, int slot) {
        addClickTransform(stack, new MenuClickPageTransform(click), slot);
    }
    public void addClickTransform(@NotNull ItemStack stack, @NotNull IClickTransform click, int slot) {
        // prevent inventory null pointers
        if (slot < 0) { return; }

        // limit max amount to 64.
        if (stack.getAmount() > 64) { stack.setAmount(64); }

        this.clickableItems.put(new MenuItem(stack, slot), click);
        super.setItem(slot, stack);
    }


    public void setUpdateHandler(@Nullable MenuUpdate updateHandler) {
        this.updateHandler = updateHandler;
        if (this.updateHandler == null) {
            MenuTask.getAutoUpdateInventories().remove(this);
        }else {
            MenuTask.getAutoUpdateInventories().add(this);
        }
    }

    public void addUpdateHandlerSubTask(MenuUpdateTask subTask) {
        this.updateSubTasks.add(subTask);
        MenuTask.getAutoUpdateInventories().add(this);
    }

    public void whenOpened(@Nullable MenuOpenCallback menuOpen) {
        this.openCallback = menuOpen;
    }

    @Override
    public void clear() {
        super.clear();
        this.clickableItems.clear();
    }

    public void update() {
        MenuUpdate menuUpdate = getUpdateHandler();

        if (menuUpdate != null) {
            if (isClearBeforeUpdate()) { clear(); }
            menuUpdate.onUpdate();
        }
    }

    public void closeAll() {
        @Nullable Inventory inv = this.inventory;
        if (inv == null) { return; }
        inv.getViewers().forEach(HumanEntity::closeInventory);
    }

    @NotNull
    public ItemStack getDefaultFiller() {
        XMaterial mat = XMaterial.GRAY_STAINED_GLASS_PANE;
        return new ItemBuilder(mat, 1, mat.getData()).setName(" ").toItemStack();
    }

    @NotNull
    public KamiMenu fill() {
        fill(getDefaultFiller());
        return this;
    }

    @NotNull
    public KamiMenu fill(@NotNull ItemStack fillerItem) {
        int empty = getInventory().firstEmpty();
        while (empty != -1) {
            this.setItem(empty, fillerItem);
            empty = getInventory().firstEmpty();
        }
        return this;
    }

    @NotNull
    public KamiMenu fill(@Nullable IBuilder iBuilder) {
        if (iBuilder == null) {
            try { throw new Exception("iBuilder is null in fill(iBuilder). Using default filler!");
            }catch (Throwable t) { t.printStackTrace(); }
            return fill();
        }
        return fill(iBuilder.toItemStack());
    }
}

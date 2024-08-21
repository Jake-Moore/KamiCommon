package com.kamikazejam.kamicommon.gui.page;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.gui.KamiMenu;
import com.kamikazejam.kamicommon.gui.MenuItem;
import com.kamikazejam.kamicommon.gui.clicks.MenuClick;
import com.kamikazejam.kamicommon.gui.clicks.MenuClickPage;
import com.kamikazejam.kamicommon.gui.interfaces.MenuUpdateTask;
import com.kamikazejam.kamicommon.item.IBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@Getter @Setter
public class PageItem {

    public final List<IBuilder> iBuilders = new ArrayList<>();
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE) private int bIndex = 0; // iBuilder index
    private int loopTicks = 20; // Default to 1 second

    // Only one should ever be set
    public @Nullable MenuClick menuClick = null;
    public @Nullable MenuClickPage menuClickPage = null;

    // Up to the developer to supply additional builders before use
    public PageItem(@Nullable MenuClick menuClick) {
        this.menuClick = menuClick;
    }
    // Up to the developer to supply additional builders before use
    public PageItem(@Nullable MenuClickPage menuClickPage) {
        this.menuClickPage = menuClickPage;
    }

    public PageItem(IBuilder iBuilder, @Nullable MenuClick menuClick) {
        this.iBuilders.add(iBuilder);
        this.menuClick = menuClick;
    }
    public PageItem(IBuilder iBuilder, @Nullable MenuClickPage menuClickPage) {
        this.iBuilders.add(iBuilder);
        this.menuClickPage = menuClickPage;
    }

    public PageItem(List<IBuilder> iBuilders, @Nullable MenuClick menuClick) {
        this.iBuilders.addAll(iBuilders);
        this.menuClick = menuClick;
    }
    public PageItem(List<IBuilder> iBuilders, @Nullable MenuClickPage menuClickPage) {
        this.iBuilders.addAll(iBuilders);
        this.menuClickPage = menuClickPage;
    }

    public PageItem setIBuilder(IBuilder iBuilder) {
        this.iBuilders.clear();
        this.iBuilders.add(iBuilder);
        return this;
    }

    public PageItem addIBuilder(IBuilder iBuilder) {
        this.iBuilders.add(iBuilder);
        return this;
    }

    public PageItem setMenuClick(MenuClick menuClick) {
        this.menuClick = menuClick;
        this.menuClickPage = null;
        return this;
    }

    public PageItem setMenuClick(MenuClickPage menuClickPage) {
        this.menuClickPage = menuClickPage;
        this.menuClick = null;
        return this;
    }

    @NotNull
    public KamiMenu addToMenu(KamiMenu menu, int slot) {
        // 1. No IBuilder Set
        if (iBuilders.isEmpty()) {
            throw new IllegalArgumentException("No IBuilder set for PageItem");
        }

        // 2. Only 1 IBuilder
        if (iBuilders.size() == 1) {
            IBuilder iBuilder = iBuilders.getFirst();
            this.addMenuClick(menu, iBuilder, slot);
            return menu;
        }

        // 3. Rotating IBuilders
        this.addMenuClick(menu, iBuilders.get(bIndex), slot);
        // Configure the update handler to update the gear icon
        menu.setUpdateHandler(() -> {
            if (!PluginSource.get().isEnabled()) { menu.closeAll(); }
        });
        menu.addUpdateHandlerSubTask(new MenuUpdateTask() {
            @Override
            public void onUpdate(@NotNull KamiMenu menu) {
                bIndex++; if (bIndex >= iBuilders.size()) { bIndex = 0; }
                updateMenuClick(menu, iBuilders.get(bIndex), slot);
            }
            @Override
            public int getLoopTicks() { return loopTicks; }
        });

        // 4. Return the menu
        return menu;
    }

    private void addMenuClick(@NotNull KamiMenu menu, @NotNull IBuilder iBuilder, int slot) {
        // 1. Click defined
        if (menuClick != null) {
            menu.addMenuClick(iBuilder, menuClick, slot);
            return;
        }
        // 2. Page Click defined
        if (menuClickPage != null) {
            menu.addMenuClick(iBuilder, menuClickPage, slot);
            return;
        }

        // 4. no click events defined
        menu.setItem(slot, iBuilder);
    }

    private void updateMenuClick(@NotNull KamiMenu menu, @NotNull IBuilder iBuilder, int slot) {
        // Remove the old icon registration
        List<MenuItem> toRemove = new ArrayList<>();
        for (MenuItem item : menu.getClickableItems().keySet()) {
            if (item.getSlot() == slot) { toRemove.add(item); }
        }
        toRemove.forEach(menu.getClickableItems()::remove);

        // Add the new click event
        this.addMenuClick(menu, iBuilder, slot);
    }

    public IBuilder getIBuilder() {
        return iBuilders.getFirst();
    }
}
package com.kamikazejam.kamicommon.menu.struct;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.menu.items.MenuItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * A container for all the options that every {@link com.kamikazejam.kamicommon.menu.Menu} must allow to be configured.<br>
 * Use Getters and Setters to access and modify these options.
 */
@Getter @Setter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class MenuOptions {
    /**
     * If we should allow the player to pick up items while the menu is open.
     */
    private boolean allowItemPickup = true;
    /**
     * If every click event regarding this GUI (including player slot clicks) should be automatically cancelled.
     */
    private boolean cancelClickEvent = true;

    // Filler Item Configuration
    @Setter(AccessLevel.NONE)
    private @Nullable MenuItem fillerItem = new MenuItem(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setName(" "), -1).setId("filler");
    private final @NotNull Set<Integer> excludedFillSlots;

    public MenuOptions() {
        this.excludedFillSlots = new HashSet<>();
    }
    // Copy Constructor
    public MenuOptions(@NotNull MenuOptions copy) {
        this.allowItemPickup = copy.allowItemPickup;
        this.cancelClickEvent = copy.cancelClickEvent;
        this.fillerItem = (copy.fillerItem == null) ? null : copy.fillerItem.copy();
        this.excludedFillSlots = new HashSet<>(copy.excludedFillSlots);
    }

    @NotNull
    public MenuOptions copy() {
        return new MenuOptions(this);
    }

    public void setFillerItem(@Nullable MenuItem fillerItem) {
        this.fillerItem = fillerItem;
        if (this.fillerItem != null) {
            this.fillerItem.setId("filler");
        }
    }
}

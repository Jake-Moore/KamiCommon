package com.kamikazejam.kamicommon.menu.api.icons;

import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a {@link MenuIcon}, but with an additional priority value
 */
@Getter
public class PrioritizedMenuIcon {
    private final @NotNull MenuIcon icon;
    private final @Nullable IconSlot slot;
    private final int priority; // Higher priority means it will be displayed earlier in the list

    public PrioritizedMenuIcon(@NotNull MenuIcon icon, @Nullable IconSlot slot, int priority) {
        this.icon = icon;
        this.slot = slot;
        this.priority = priority;
    }
    // Copy Constructor
    public PrioritizedMenuIcon(@NotNull PrioritizedMenuIcon icon) {
        this.icon = icon.getIcon().copy();
        this.slot = (icon.getSlot() == null) ? null : icon.getSlot().copy();
        this.priority = icon.getPriority();
    }

    @NotNull
    public PrioritizedMenuIcon copy() {
        return new PrioritizedMenuIcon(this);
    }
}

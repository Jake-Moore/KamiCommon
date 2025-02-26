package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.menu.api.clicks.OneClickMenuTransform;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.oneclick.OneClickMenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeRows;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeType;
import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This Menu class focuses on providing a simple single-frame menu. This is the most versatile menu type
 * because you define everything in the menu, and can create your own custom logic.
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class OneClickMenu extends AbstractMenu<OneClickMenu> {
    boolean clicked = false;
    private final @NotNull OneClickMenuTransform transform;

    // Constructor (Deep Copying from Builder)
    OneClickMenu(@NotNull Builder builder, @NotNull Player player, @NotNull OneClickMenuTransform transform) {
        super(builder, player);
        this.transform = transform;
    }

    public @Nullable InventoryView open() {
        // Ensure the menu resets our click (we reset to permit one click per opening)
        // reopen calls this, so reopening will also reset the click
        clicked = false;
        return super.open();
    }

    // ------------------------------------------------------------ //
    //                        Builder Pattern                       //
    // ------------------------------------------------------------ //
    public static final class Builder extends AbstractMenuBuilder<OneClickMenu, Builder> {
        public Builder(@NotNull MenuSize size, @NotNull MenuEvents events, @NotNull MenuOptions options) {
            super(size, events, options);
        }

        public Builder(@NotNull MenuSize size) {
            this(size, new MenuEvents(), new OneClickMenuOptions());
        }
        public Builder(int rows) {
            this(new MenuSizeRows(rows));
        }
        public Builder(@NotNull InventoryType type) {
            this(new MenuSizeType(type));
        }

        public @NotNull Builder oneClickOptions(OneClickMenuOptions.@NotNull OneClickMenuOptionsModification modification) {
            Preconditions.checkNotNull(modification, "Modification must not be null.");
            modification.modify((OneClickMenuOptions) this.options);
            return this;
        }

        @CheckReturnValue
        public @NotNull OneClickMenu build(@NotNull Player player, @NotNull OneClickMenuTransform transform) {
            Preconditions.checkNotNull(player, "Player must not be null.");
            return new OneClickMenu(this, player, transform);
        }

        // Static factory methods
        public static Builder create(@NotNull MenuSize size) {
            return new Builder(size);
        }

        public static Builder create(int rows) {
            return new Builder(new MenuSizeRows(rows));
        }

        public static Builder create(@NotNull InventoryType type) {
            return new Builder(new MenuSizeType(type));
        }
    }
}

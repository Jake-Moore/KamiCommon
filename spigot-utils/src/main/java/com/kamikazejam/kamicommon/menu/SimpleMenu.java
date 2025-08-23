package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.simple.SimpleMenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeRows;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeType;
import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

/**
 * This Menu class focuses on providing a simple single-frame menu. This is the most versatile menu type
 * because you define everything in the menu, and can create your own custom logic.
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class SimpleMenu extends AbstractMenu<SimpleMenu> {

    // Constructor (Deep Copying from Builder)
    SimpleMenu(@NotNull Builder builder, @NotNull Player player) {
        super(builder, player);
    }

    // ------------------------------------------------------------ //
    //                        Builder Pattern                       //
    // ------------------------------------------------------------ //
    public static final class Builder extends AbstractMenuBuilder<SimpleMenu, Builder> {
        public Builder(@NotNull MenuSize size, @NotNull MenuEvents<SimpleMenu> events, @NotNull MenuOptions<SimpleMenu> options) {
            super(size, events, options);
        }

        public Builder(@NotNull MenuSize size) {
            this(size, new MenuEvents<>(), new SimpleMenuOptions());
        }

        public Builder(int rows) {
            this(new MenuSizeRows(rows));
        }

        public Builder(@NotNull InventoryType type) {
            this(new MenuSizeType(type));
        }

        @CheckReturnValue
        public @NotNull SimpleMenu build(@NotNull Player player) {
            Preconditions.checkNotNull(player, "Player must not be null.");
            return new SimpleMenu(this, player);
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

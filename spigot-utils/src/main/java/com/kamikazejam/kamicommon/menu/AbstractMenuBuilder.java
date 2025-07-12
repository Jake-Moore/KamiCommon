package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.access.IMenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.access.MenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.icons.PrioritizedMenuIconMap;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.api.title.MenuTitleCalculator;
import com.kamikazejam.kamicommon.menu.api.title.MenuTitleProvider;
import com.kamikazejam.kamicommon.menu.api.title.MenuTitleReplacement;
import com.kamikazejam.kamicommon.util.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public sealed abstract class AbstractMenuBuilder<M extends Menu<M>, T extends AbstractMenuBuilder<M, T>> permits SimpleMenu.Builder, PaginatedMenu.Builder, OneClickMenu.Builder {
    // Menu Details
    protected @NotNull MenuSize size;
    protected final @NotNull MenuTitleCalculator titleCalculator = new MenuTitleCalculator();
    // Menu Icons
    protected final PrioritizedMenuIconMap<M> menuIcons = new PrioritizedMenuIconMap<>();
    // Additional Configuration
    protected final MenuEvents<M> events;
    protected final MenuOptions<M> options;

    protected AbstractMenuBuilder(@NotNull MenuSize size, @NotNull MenuEvents<M> events, @NotNull MenuOptions<M> options) {
        this.size = size;
        // Add the default filler icon
        this.menuIcons.add(MenuIcon.getDefaultFillerIcon(), null);
        // Set the initial events and options
        this.events = events;
        this.options = options;
    }

    public final @NotNull MenuSize getSize() {
        return size;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull T size(@NotNull MenuSize size) {
        Preconditions.checkNotNull(size, "Size must not be null.");
        this.size = size;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull T title(@Nullable String title) {
        this.titleCalculator.setProvider((p) -> (title != null) ? title : " ");
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull T title(@NotNull MenuTitleProvider titleProvider) {
        Preconditions.checkNotNull(titleProvider, "Title callback must not be null.");
        this.titleCalculator.setProvider(titleProvider);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull T titleReplacement(@NotNull CharSequence target,
                                             @NotNull CharSequence replacement) {
        Preconditions.checkNotNull(target, "Target must not be null.");
        Preconditions.checkNotNull(replacement, "Replacement must not be null.");
        this.titleCalculator.getReplacements().add(new MenuTitleReplacement(target, replacement));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull T options(@NotNull MenuOptions.MenuOptionsModification modification) {
        Preconditions.checkNotNull(modification, "Modification must not be null.");
        modification.modify(this.options);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull T events(@NotNull MenuEvents.MenuEventsModification modification) {
        Preconditions.checkNotNull(modification, "Modification must not be null.");
        modification.modify(this.events);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull T fillerIcon(@Nullable MenuIcon<M> fillerIcon) {
        if (fillerIcon == null) {
            this.menuIcons.remove("filler");
            return (T) this;
        }
        fillerIcon.setId("filler");
        this.menuIcons.add(fillerIcon, null);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final @NotNull T modifyIcons(@NotNull Consumer<IMenuIconsAccess<M>> consumer) {
        consumer.accept(new MenuIconsAccess<>(this.size, this.menuIcons));
        return (T) this;
    }
}


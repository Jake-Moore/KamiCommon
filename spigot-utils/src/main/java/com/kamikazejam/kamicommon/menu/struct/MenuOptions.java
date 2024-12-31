package com.kamikazejam.kamicommon.menu.struct;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

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
    public interface MenuOptionsModification {
        void modify(@NotNull MenuOptions options);
    }

    /**
     * If we should allow the player to pick up items while the menu is open.
     */
    private boolean allowItemPickup = true;
    /**
     * If every click event regarding this GUI (including player slot clicks) should be automatically cancelled.
     */
    private boolean cancelClickEvent = true;
    /**
     * Configure slots that won't be filled by the filler item, even if they are empty.
     */
    private final @NotNull Set<Integer> excludedFillSlots;

    /**
     * Should each call to open() from the same {@link com.kamikazejam.kamicommon.menu.Menu} reset the visuals of the menu?<br>
     * When false, if menu is re-opened, it will look identical to when it was closed last.<br>
     * This mostly pertains to how the auto updating items are handled, and whether they which state in time they are in.
     */
    private boolean resetVisualsOnOpen = true;

    public MenuOptions() {
        this.excludedFillSlots = new HashSet<>();
    }
    // Copy Constructor
    public MenuOptions(@NotNull MenuOptions copy) {
        this.allowItemPickup = copy.allowItemPickup;
        this.cancelClickEvent = copy.cancelClickEvent;
        this.excludedFillSlots = new HashSet<>(copy.excludedFillSlots);
        this.resetVisualsOnOpen = copy.resetVisualsOnOpen;
    }

    @NotNull
    public MenuOptions copy() {
        return new MenuOptions(this);
    }
}

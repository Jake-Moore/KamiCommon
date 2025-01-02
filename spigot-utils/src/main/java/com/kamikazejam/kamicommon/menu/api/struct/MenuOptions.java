package com.kamikazejam.kamicommon.menu.api.struct;

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
     * If we should allow the player to drop items while the menu is open.
     */
    private boolean allowItemDrop = true;
    /**
     * If every click event regarding this GUI (not including player slots) should be automatically cancelled.<br>
     * To cancel player slot clicks, use {@link #cancelPlayerClickEvent}.
     */
    private boolean cancelClickEvent = true;
    /**
     * If every click event regarding the player's inventory (not including menu slots) should be automatically cancelled.<br>
     * To cancel GUI slot clicks, use {@link #cancelClickEvent}.<br>
     * <br>
     * BE CAREFUL SETTING THIS TO FALSE. If you have menu clicks cancelling, and there is at least one empty slot in the menu,
     * a player can shift_click an item from their inventory into the menu, since that click is on the player inventory. Then, since the
     * item is in the menu, they cannot get it back.
     */
    private boolean cancelPlayerClickEvent = true;
    /**
     * Configure slots that won't be filled by the filler icon, even if they are empty.
     */
    private final @NotNull Set<Integer> excludedFillSlots;

    /**
     * Should each call to open() from the same {@link com.kamikazejam.kamicommon.menu.Menu} reset the visuals of the menu?<br>
     * When false, if menu is re-opened, it will look identical to when it was closed last.<br>
     * This mostly pertains to how the auto updating icons are handled, and whether they which state in time they are in.
     */
    private boolean resetVisualsOnOpen = true;

    public MenuOptions() {
        this.excludedFillSlots = new HashSet<>();
    }
    // Copy Constructor
    private MenuOptions(@NotNull MenuOptions copy) {
        this.allowItemPickup = copy.allowItemPickup;
        this.allowItemDrop = copy.allowItemDrop;
        this.cancelClickEvent = copy.cancelClickEvent;
        this.cancelPlayerClickEvent = copy.cancelPlayerClickEvent;
        this.excludedFillSlots = new HashSet<>(copy.excludedFillSlots);
        this.resetVisualsOnOpen = copy.resetVisualsOnOpen;
    }

    @NotNull
    public MenuOptions copy() {
        return new MenuOptions(this);
    }
}

package com.kamikazejam.kamicommon.menu.api.icons.access;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.struct.SlotData;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MenuIconsAccess implements IMenuIconsAccess {
    private final @NotNull MenuSize menuSize;
    private final @NotNull Map<String, MenuIcon> menuIcons;
    private final @NotNull Map<Integer, SlotData> menuSlots;
    public MenuIconsAccess(@NotNull MenuSize menuSize, @NotNull Map<String, MenuIcon> menuIcons, @NotNull Map<Integer, SlotData> menuSlots) {
        this.menuSize = menuSize;
        this.menuIcons = menuIcons;
        this.menuSlots = menuSlots;
    }

    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull MenuIcon addMenuIcon(@NotNull MenuIcon menuIcon, @Nullable IconSlot iconSlot) {
        this.menuIcons.put(menuIcon.getId(), menuIcon);
        // Add this icon to the slots it corresponds to
        if (iconSlot == null) { return menuIcon; }
        int totalSlots = menuSize.getNumberOfSlots();
        for (int slot : iconSlot.get(menuSize)) {
            if (slot < 0 || slot >= totalSlots) { continue; } // sanitize slot values
            this.menuSlots.put(slot, new SlotData(iconSlot, menuIcon.getId()));
        }

        return menuIcon;
    }

    @Override
    public @Nullable MenuIcon removeMenuIcon(@NotNull String id) {
        return this.menuIcons.remove(id);
    }

    @Override
    public void clearMenuIcons() {
        this.menuIcons.clear();
    }



    // ------------------------------------------------------------ //
    //                   Icon Management (by ID)                    //
    // ------------------------------------------------------------ //
    @NotNull
    public Optional<MenuIcon> getMenuIcon(@NotNull String id) {
        if (!this.menuIcons.containsKey(id)) { return Optional.empty(); }
        return Optional.ofNullable(this.menuIcons.get(id));
    }
    public boolean isValidMenuIconID(@NotNull String id) {
        return this.menuIcons.containsKey(id);
    }
    @NotNull
    public Set<String> getMenuIconIDs() {
        return this.menuIcons.keySet();
    }


    // ------------------------------------------------------------ //
    //                  Icon Management (by slot)                   //
    // ------------------------------------------------------------ //

    @Override
    public @NotNull Optional<MenuIcon> getMenuIconForSlot(int slot) {
        @Nullable SlotData slotData = this.menuSlots.get(slot);
        if (slotData == null) {
            // We don't have a specific MenuIcon for this slot, so see if the filler is going to be used
            @Nullable MenuIcon filler = this.menuIcons.getOrDefault("filler", null);
            if (filler == null || !filler.isEnabled()) { return Optional.empty(); }
            return Optional.of(filler);
        }
        String iconId = slotData.getId();
        return this.getMenuIcon(iconId);
    }

    @Override
    public boolean hasMenuIconForSlot(int slot) {
        return this.menuSlots.containsKey(slot);
    }
}

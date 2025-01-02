package com.kamikazejam.kamicommon.menu.api.icons.access.paginated;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.PrioritizedMenuIcon;
import com.kamikazejam.kamicommon.menu.api.struct.icons.PrioritizedMenuIconMap;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class PageIconsAccess implements IPageIconsAccess {
    private final @NotNull PrioritizedMenuIconMap pagedIcons;
    public PageIconsAccess(@NotNull PrioritizedMenuIconMap pagedIcons) {
        this.pagedIcons = pagedIcons;
    }

    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull MenuIcon addPagedIcon(@NotNull MenuIcon menuIcon) {
        return this.addPagedIcon(new PrioritizedMenuIcon(menuIcon, null, this.pagedIcons.size()));
    }
    @Override
    public @NotNull MenuIcon addPagedIcon(@NotNull PrioritizedMenuIcon indexed) {
        if (this.pagedIcons.contains(indexed.getIcon().getId())) {
            // throw error so the developer can fix it
            throw new IllegalArgumentException("Duplicate MenuIcon ID in PagedKamiMenu: '" + indexed.getIcon().getId() + "'. Existing IDs are: " + Arrays.toString(this.pagedIcons.keySet().toArray()));
        }
        this.pagedIcons.add(indexed);
        return indexed.getIcon();
    }
    @Override
    public void clearPagedIcons() {
        this.pagedIcons.clear();
    }

    // ------------------------------------------------------------ //
    //                   Icon Management (by ID)                    //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull Optional<MenuIcon> getPagedIcon(@NotNull String id) {
        if (!pagedIcons.contains(id)) { return Optional.empty(); }
        return pagedIcons.get(id);
    }
    @Override
    public boolean isValidPagedIconID(@NotNull String id) {
        return pagedIcons.contains(id);
    }
    @Override
    public @NotNull Set<String> getPagedIconIDs() {
        return pagedIcons.keySet();
    }
}

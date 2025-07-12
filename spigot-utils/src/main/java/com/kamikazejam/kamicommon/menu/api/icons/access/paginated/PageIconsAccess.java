package com.kamikazejam.kamicommon.menu.api.icons.access.paginated;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.PrioritizedMenuIcon;
import com.kamikazejam.kamicommon.menu.api.struct.icons.PrioritizedMenuIconMap;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class PageIconsAccess<M extends Menu<M>> implements IPageIconsAccess<M> {
    private final @NotNull PrioritizedMenuIconMap<M> pagedIcons;

    public PageIconsAccess(@NotNull PrioritizedMenuIconMap<M> pagedIcons) {
        this.pagedIcons = pagedIcons;
    }

    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull MenuIcon<M> addPagedIcon(@NotNull MenuIcon<M> menuIcon) {
        return this.addPagedIcon(new PrioritizedMenuIcon<>(menuIcon, null, this.pagedIcons.size()));
    }

    @Override
    public @NotNull MenuIcon<M> addPagedIcon(@NotNull PrioritizedMenuIcon<M> indexed) {
        if (this.pagedIcons.contains(indexed.getIcon().getId())) {
            // throw error so the developer can fix it
            throw new IllegalArgumentException("Duplicate MenuIcon<M> ID in PagedKamiMenu: '" + indexed.getIcon().getId() + "'. Existing IDs are: " + Arrays.toString(this.pagedIcons.keySet().toArray()));
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
    public @NotNull Optional<MenuIcon<M>> getPagedIcon(@NotNull String id) {
        if (!pagedIcons.contains(id)) {return Optional.empty();}
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

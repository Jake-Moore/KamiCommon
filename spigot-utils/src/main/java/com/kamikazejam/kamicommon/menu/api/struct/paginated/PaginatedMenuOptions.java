package com.kamikazejam.kamicommon.menu.api.struct.paginated;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.paginated.layout.PaginationLayout;
import com.kamikazejam.kamicommon.menu.api.struct.paginated.title.DefaultPaginatedMenuTitle;
import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A container for all the options that every {@link com.kamikazejam.kamicommon.menu.PaginatedMenu} must allow to be configured.<br>
 * Use Getters and Setters to access and modify these options.
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class PaginatedMenuOptions extends MenuOptions {
    // Both Fields are not final, since they can be extended and then set, so long as they are an instance of the correct class
    private @NotNull PaginationLayout layout;
    private @NotNull DefaultPaginatedMenuTitle titleFormat = new DefaultPaginatedMenuTitle();
    // Icons
    @Setter
    private @Nullable MenuIcon nextPageIcon = new MenuIcon(true, new ItemBuilder(XMaterial.ARROW).setName("&a&lNext Page &a▶"));
    @Setter
    private @Nullable MenuIcon prevPageIcon = new MenuIcon(true, new ItemBuilder(XMaterial.ARROW).setName("&a◀ &a&lPrevious Page"));

    public PaginatedMenuOptions(@NotNull PaginationLayout layout) {
        Preconditions.checkNotNull(layout, "layout cannot be null");
        this.layout = layout;
    }

    public void setLayout(@NotNull PaginationLayout layout) {
        Preconditions.checkNotNull(layout, "layout cannot be null");
        this.layout = layout;
    }

    public void setTitleFormat(@NotNull DefaultPaginatedMenuTitle titleFormat) {
        Preconditions.checkNotNull(titleFormat, "titleFormat cannot be null");
        this.titleFormat = titleFormat;
    }

    public interface PaginatedMenuOptionsModification {
        void modify(@NotNull PaginatedMenuOptions options);
    }
}

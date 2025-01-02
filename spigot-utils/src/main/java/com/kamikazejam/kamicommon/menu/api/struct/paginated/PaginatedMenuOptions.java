package com.kamikazejam.kamicommon.menu.api.struct.paginated;

import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.paginated.title.DefaultPaginatedMenuTitle;
import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * A container for all the options that every {@link com.kamikazejam.kamicommon.menu.PaginatedMenu} must allow to be configured.<br>
 * Use Getters and Setters to access and modify these options.
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class PaginatedMenuOptions extends MenuOptions {
    public interface PaginatedMenuOptionsModification {
        void modify(@NotNull PaginatedMenuOptions options);
    }

    // Both Fields are not final, since they can be extended and then set, so long as they are an instance of the correct class
    private @NotNull PaginatedMenuLayout layout = new PaginatedMenuLayout();
    private @NotNull DefaultPaginatedMenuTitle titleFormat = new DefaultPaginatedMenuTitle();

    public void setLayout(@NotNull PaginatedMenuLayout layout) {
        Preconditions.checkNotNull(layout, "layout cannot be null");
        this.layout = layout;
    }

    public void setTitleFormat(@NotNull DefaultPaginatedMenuTitle titleFormat) {
        Preconditions.checkNotNull(titleFormat, "titleFormat cannot be null");
        this.titleFormat = titleFormat;
    }
}

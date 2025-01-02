package com.kamikazejam.kamicommon.menu.api.struct.paginated.title;

import com.kamikazejam.kamicommon.menu.api.MenuHolder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPaginatedMenuTitle {
    @Getter @Setter
    private boolean appendTitleWithPage = true;

    // We use a cached title, since after the first modification to include page information
    // We will have lost the original title information
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private transient @Nullable String cachedTitle = null;

    public final @NotNull String getMenuTitle(@NotNull MenuHolder menu, int currentPage, int maxPages) {
        String base = (cachedTitle == null) ? cachedTitle = menu.getTitle() : cachedTitle;
        if (!isAppendTitleWithPage()) {
            return base;
        }

        return getMenuTitleWithPage(base, currentPage, maxPages);
    }

    protected abstract @NotNull String getMenuTitleWithPage(@NotNull String baseTitle, int currentPage, int maxPages);
}

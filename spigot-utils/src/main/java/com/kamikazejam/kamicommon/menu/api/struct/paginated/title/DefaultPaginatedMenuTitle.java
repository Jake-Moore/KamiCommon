package com.kamikazejam.kamicommon.menu.api.struct.paginated.title;

import org.jetbrains.annotations.NotNull;

public final class DefaultPaginatedMenuTitle extends AbstractPaginatedMenuTitle {
    @Override
    protected @NotNull String getMenuTitleWithPage(@NotNull String base, int currentPage, int maxPages) {
        return base + (maxPages > 1 ? " (Page " + (currentPage) + "/" + maxPages + ")" : "");
    }
}

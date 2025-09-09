package com.kamikazejam.kamicommon.menu.api.struct.paginated.title;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import org.jetbrains.annotations.NotNull;

public final class DefaultPaginatedMenuTitle extends AbstractPaginatedMenuTitle {
    @Override
    protected @NotNull VersionedComponent getMenuTitleWithPage(@NotNull VersionedComponent base, int currentPage, int maxPages) {
        String miniMessage = base.serializeMiniMessage();
        // Append page data inheriting, not specifying any new styles to inherit from the base title
        return NmsAPI.getVersionedComponentSerializer().fromMiniMessage(
                miniMessage + (maxPages > 1 ? " (Page " + (currentPage) + "/" + maxPages + ")" : "")
        );
    }

    @Override
    public @NotNull AbstractPaginatedMenuTitle copy() {
        return new DefaultPaginatedMenuTitle();
    }
}

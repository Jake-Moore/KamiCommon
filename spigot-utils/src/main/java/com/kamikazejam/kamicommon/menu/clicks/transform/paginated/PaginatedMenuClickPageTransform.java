package com.kamikazejam.kamicommon.menu.clicks.transform.paginated;

import com.kamikazejam.kamicommon.menu.clicks.MenuClickPage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class PaginatedMenuClickPageTransform implements IPaginatedClickTransform {

    private final @NotNull MenuClickPage click;
    public PaginatedMenuClickPageTransform(@NotNull MenuClickPage click) {
        this.click = Objects.requireNonNull(click);
    }

    @Override
    public void process(@NotNull Player player, @NotNull InventoryClickEvent event, int page) {
        click.onClick(player, event.getClick(), page);
    }
}

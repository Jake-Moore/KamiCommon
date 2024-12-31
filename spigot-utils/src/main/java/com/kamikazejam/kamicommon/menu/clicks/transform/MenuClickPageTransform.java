package com.kamikazejam.kamicommon.menu.clicks.transform;

import com.kamikazejam.kamicommon.menu.clicks.MenuClickPage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class MenuClickPageTransform implements IClickTransform {

    private final @NotNull MenuClickPage click;
    public MenuClickPageTransform(@NotNull MenuClickPage click) {
        this.click = Objects.requireNonNull(click);
    }

    @Override
    public void process(@NotNull Player player, @NotNull InventoryClickEvent event, int page) {
        click.onClick(player, event.getClick(), page);
    }
}

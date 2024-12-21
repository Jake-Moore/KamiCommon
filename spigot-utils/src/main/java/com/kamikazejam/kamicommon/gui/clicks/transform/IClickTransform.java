package com.kamikazejam.kamicommon.gui.clicks.transform;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface IClickTransform {

    void process(@NotNull Player player, @NotNull InventoryClickEvent event, int page);

}
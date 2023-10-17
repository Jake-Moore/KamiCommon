package com.kamikazejamplugins.kamicommon.gui;

import com.google.common.collect.Sets;
import com.kamikazejamplugins.kamicommon.gui.interfaces.Menu;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class MenuTask implements Runnable {

    @Getter private static final Set<Menu> autoUpdateInventories = Sets.newCopyOnWriteArraySet();

    @Override
    public void run() {
        for (Menu inv : autoUpdateInventories) {
            if (!inv.getInventory().getViewers().isEmpty()) {
                if (inv.isClearBeforeUpdate()) { inv.clear(); }
                inv.update();

                for (HumanEntity entity : inv.getInventory().getViewers()) {
                    Player p = (Player) entity;
                    p.updateInventory();
                }
            }
        }
    }
}

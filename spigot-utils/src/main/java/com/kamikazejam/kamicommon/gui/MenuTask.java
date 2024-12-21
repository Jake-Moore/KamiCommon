package com.kamikazejam.kamicommon.gui;

import com.google.common.collect.Sets;
import com.kamikazejam.kamicommon.SpigotUtilsSource;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * This task is called every tick in a task registered by {@link SpigotUtilsSource}
 */
public class MenuTask implements Runnable {

    @Getter private static final Set<KamiMenu> autoUpdateInventories = Sets.newCopyOnWriteArraySet();

    @Override
    public void run() {
        Set<KamiMenu> updated = new HashSet<>();

        // Check and run any sub-tasks for each inventory
        for (KamiMenu inv : autoUpdateInventories) {
            if (inv.getInventory().getViewers().isEmpty()) { continue; }
            inv.getTickCounter().getAndIncrement();
            // Trigger dynamic item updates on this menu
            inv.update();
            updated.add(inv);
        }

        // Send updates to all players affected by modified guis
        updated.forEach((inv) -> {
            for (HumanEntity entity : inv.getInventory().getViewers()) {
                if (!(entity instanceof Player p)) { continue; }
                p.updateInventory();
            }
        });
    }
}

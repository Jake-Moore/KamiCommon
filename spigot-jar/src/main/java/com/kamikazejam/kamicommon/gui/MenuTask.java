package com.kamikazejam.kamicommon.gui;

import com.google.common.collect.Sets;
import com.kamikazejam.kamicommon.PluginSource;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This task is called every tick by {@link PluginSource}
 */
public class MenuTask implements Runnable {

    @Getter private static final Set<KamiMenu> autoUpdateInventories = Sets.newCopyOnWriteArraySet();
    private static final AtomicInteger tickCounter = new AtomicInteger(0);

    @Override
    public void run() {
        Set<KamiMenu> updated = new HashSet<>();
        int tick = tickCounter.getAndIncrement();               // start at 0 (no delay for first loops)
        // With integer max, it would be 1242.75 day before an integer overflow, I think we're fine

        // Check and run any sub-tasks for each inventory
        for (KamiMenu inv : autoUpdateInventories) {
            if (inv.getInventory().getViewers().isEmpty()) { continue; }
            // Trigger dynamic item updates on this menu
            inv.update(tick);
            updated.add(inv);
        }

        // Send updates to all players affected by modified guis
        updated.forEach((inv) -> {
            for (HumanEntity entity : inv.getInventory().getViewers()) {
                Player p = (Player) entity;
                p.updateInventory();
            }
        });
    }
}

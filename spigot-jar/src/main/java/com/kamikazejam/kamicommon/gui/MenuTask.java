package com.kamikazejam.kamicommon.gui;

import com.google.common.collect.Sets;
import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.gui.interfaces.Menu;
import com.kamikazejam.kamicommon.gui.interfaces.MenuTicked;
import com.kamikazejam.kamicommon.gui.interfaces.MenuUpdateTask;
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

    @Getter private static final Set<MenuTicked> autoUpdateInventories = Sets.newCopyOnWriteArraySet();
    private static final AtomicInteger tickCounter = new AtomicInteger(0);

    @Override
    public void run() {
        Set<Menu> updated = new HashSet<>();
        int tick = tickCounter.getAndIncrement(); // start at 0 (no delay for first loops)

        // Run the standard autoUpdateInventories every 20 ticks
        if (tick % 20 == 0) {
            runRegular20TickUpdates(updated);
        }

        // Check and run any sub-tasks for each inventory
        for (MenuTicked inv : autoUpdateInventories) {
            if (inv.getInventory().getViewers().isEmpty()) { continue; }
            if (inv.getUpdateSubTasks().isEmpty()) { continue; }

            for (MenuUpdateTask task : inv.getUpdateSubTasks()) {
                if (task.getLoopTicks() <= 0 || tick % task.getLoopTicks() != 0) { continue; }
                task.onUpdate(inv);
                updated.add(inv);
            }
        }

        // Send updates to all players affected by modified guis
        updated.forEach((inv) -> {
            for (HumanEntity entity : inv.getInventory().getViewers()) {
                Player p = (Player) entity;
                p.updateInventory();
            }
        });
    }

    private void runRegular20TickUpdates(Set<Menu> updated) {
        for (Menu inv : autoUpdateInventories) {
            if (inv.getInventory().getViewers().isEmpty()) { continue; }

            if (inv.isClearBeforeUpdate()) { inv.clear(); }     // clear before updating (if necessary)
            inv.update();                                       // run the traditional update method

            updated.add(inv);
        }
    }
}

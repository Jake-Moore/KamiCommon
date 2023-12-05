package com.kamikazejam.kamicommon.util.engine;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.interfaces.Active;
import com.kamikazejam.kamicommon.util.predicate.PredicateStartsWithIgnoreCase;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;

@SuppressWarnings("unused")
public abstract class Engine implements Active, Listener, Runnable {
    // -------------------------------------------- //
    // REGISTRY
    // -------------------------------------------- //

    @Getter
    private static final Set<Engine> allInstances = new KamiSet<>();

    // -------------------------------------------- //
    // PLUGIN
    // -------------------------------------------- //

    @Getter
    private KamiPlugin plugin = null;

    public boolean hasKamiPlugin() {
        return this.getPlugin() != null;
    }

    public void setKamiPlugin(KamiPlugin plugin) {
        this.plugin = plugin;
    }

    public void setPluginSoft(KamiPlugin plugin) {
        if (this.hasKamiPlugin()) return;
        this.plugin = plugin;
    }

    // -------------------------------------------- //
    // TASK
    // -------------------------------------------- //

    @Getter
    private Long delay = 0L;

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    @Getter
    private Long period = null;

    public void setPeriod(Long period) {
        this.period = period;
    }

    @Getter
    private boolean sync = true;

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    @Getter
    private BukkitTask task = null;

    public Integer getTaskId() {
        return this.task == null ? null : this.task.getTaskId();
    }

    @Override
    public void run() {

    }

    // -------------------------------------------- //
    // ACTIVE
    // -------------------------------------------- //

    @Override
    public boolean isActive() {
        return getAllInstances().contains(this);
    }

    @Override
    public void setActive(boolean active) {
        this.setActiveListener(active);
        this.setActiveTask(active);
        this.setActiveInner(active);
        if (active) {
            getAllInstances().add(this);
        } else {
            getAllInstances().remove(this);
        }
    }

    @Override
    public void setActivePlugin(Plugin plugin) {
        this.setPluginSoft((KamiPlugin) plugin);
    }

    @Override
    public KamiPlugin getActivePlugin() {
        return this.getPlugin();
    }

    @Override
    public void setActive(Plugin plugin) {
        this.setActivePlugin(plugin);
        this.setActive(plugin != null);
    }

    // -------------------------------------------- //
    // ACTIVE > EVENTS
    // -------------------------------------------- //

    public void setActiveListener(boolean active) {
        if (active) {
            // Support without at load
            KamiPlugin plugin = this.getPlugin();
            if (plugin.isEnabled()) {
                Bukkit.getPluginManager().registerEvents(this, this.getPlugin());
            }
        } else {
            HandlerList.unregisterAll(this);
        }
    }

    // -------------------------------------------- //
    // ACTIVE > TASK
    // -------------------------------------------- //

    public void setActiveTask(boolean active) {
        if (active) {
            if (this.getPeriod() != null) {
                // Support without at load
                KamiPlugin plugin = this.getPlugin();
                if (plugin.isEnabled()) {
                    if (this.isSync()) {
                        this.task = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), this, this.getDelay(), this.getPeriod());
                    } else {
                        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(this.getPlugin(), this, this.getDelay(), this.getPeriod());
                    }
                }
            }
        } else {
            if (this.task != null) {
                this.task.cancel();
                this.task = null;
            }
        }
    }

    // -------------------------------------------- //
    // ACTIVE > INNER
    // -------------------------------------------- //

    public void setActiveInner(boolean active) {
        // NOTE: Here you can add some extra custom logic.
    }

    // -------------------------------------------- //
    // IS FAKE
    // -------------------------------------------- //

    public static final PredicateStartsWithIgnoreCase STARTING_WITH_FAKE = PredicateStartsWithIgnoreCase.get("fake");

    public static boolean isFake(Event event) {
        final Class<?> clazz = event.getClass();
        if (event instanceof BlockPlaceEvent) {
            return !BlockPlaceEvent.class.equals(clazz) && !BlockMultiPlaceEvent.class.equals(clazz);
        } else {
            return STARTING_WITH_FAKE.apply(clazz.getSimpleName());
        }
    }

    // -------------------------------------------- //
    // IS OFF HAND
    // -------------------------------------------- //

    public static boolean isOffHand(PlayerInteractEntityEvent event) {
        try {
            return event.getHand() == org.bukkit.inventory.EquipmentSlot.OFF_HAND;
        } catch (Throwable t) {
            return false;
        }
    }

}

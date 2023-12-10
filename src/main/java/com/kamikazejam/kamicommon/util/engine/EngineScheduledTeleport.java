package com.kamikazejam.kamicommon.util.engine;

import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import com.kamikazejam.kamicommon.util.mson.MsonMessenger;
import com.kamikazejam.kamicommon.util.teleport.ScheduledTeleport;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class EngineScheduledTeleport extends Engine {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final EngineScheduledTeleport i = new EngineScheduledTeleport();

	@Contract(pure = true)
	public static EngineScheduledTeleport get() {
		return i;
	}

	public EngineScheduledTeleport() {
		this.setPeriod(1L);
	}

	// -------------------------------------------- //
	// SCHEDULED TELEPORT INDEX
	// -------------------------------------------- //

	protected Map<String, ScheduledTeleport> teleporteeIdToScheduledTeleport = new ConcurrentHashMap<>();

	public boolean isScheduled(ScheduledTeleport st) {
		return this.teleporteeIdToScheduledTeleport.containsValue(st);
	}

	public ScheduledTeleport schedule(@NotNull ScheduledTeleport st) {
		ScheduledTeleport old = this.teleporteeIdToScheduledTeleport.get(st.getTeleporteeId());
		if (old != null) old.unschedule();

		this.teleporteeIdToScheduledTeleport.put(st.getTeleporteeId(), st);

		st.setDueMillis(System.currentTimeMillis() + st.getDelaySeconds() * 1000L);

		return old;
	}

	public boolean unschedule(@NotNull ScheduledTeleport st) {
		ScheduledTeleport old = this.teleporteeIdToScheduledTeleport.get(st.getTeleporteeId());
		if (old == null) return false;
		if (old != st) return false;

		return this.teleporteeIdToScheduledTeleport.remove(st.getTeleporteeId()) != null;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void run() {
		long now = System.currentTimeMillis();
		for (ScheduledTeleport st : teleporteeIdToScheduledTeleport.values()) {
			if (st.isDue(now)) {
				st.run();
			}
		}
	}

	// -------------------------------------------- //
	// LISTENER: CANCEL TELEPORT
	// -------------------------------------------- //

	public void cancelTeleport(Player player) {
		if (KUtil.isntPlayer(player)) return;

		// If there there is a ScheduledTeleport ...
		ScheduledTeleport scheduledTeleport = teleporteeIdToScheduledTeleport.get(IdUtilLocal.getId(player));
		if (scheduledTeleport == null) return;

		// ... unschedule it ...
		scheduledTeleport.unschedule();

		// ... and inform the teleportee.
		MsonMessenger.get().msgOne(scheduledTeleport.getTeleporteeId(), "<rose>Cancelled <i>teleport to <h>" + scheduledTeleport.getDestination().getDesc(scheduledTeleport.getTeleporteeId()) + "<i>.");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelTeleport(@NotNull PlayerMoveEvent event) {
		if (KUtil.isSameBlock(event)) return;

		final Player player = event.getPlayer();
		this.cancelTeleport(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelTeleport(@NotNull EntityDamageEvent event) {
		final Entity entity = event.getEntity();
		if (KUtil.isntPlayer(entity)) return;
		final Player player = (Player) entity;

		this.cancelTeleport(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelTeleport(@NotNull PlayerDeathEvent event) {
		final Player player = event.getEntity();
		if (KUtil.isntPlayer(player)) return;

		this.cancelTeleport(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelTeleport(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		this.cancelTeleport(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelTeleport2(PlayerKickEvent event) {
		final Player player = event.getPlayer();
		this.cancelTeleport(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelTeleport(@NotNull InventoryOpenEvent event) {
		final HumanEntity human = event.getPlayer();
		if (KUtil.isntPlayer(human)) return;
		final Player player = (Player) human;

		this.cancelTeleport(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelTeleport(@NotNull InventoryClickEvent event) {
		final HumanEntity human = event.getWhoClicked();
		if (KUtil.isntPlayer(human)) return;
		final Player player = (Player) human;

		this.cancelTeleport(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelTeleport(@NotNull PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		if (KUtil.isntPlayer(player)) return;

		this.cancelTeleport(player);
	}
}

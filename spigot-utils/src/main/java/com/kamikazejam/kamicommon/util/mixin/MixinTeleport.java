package com.kamikazejam.kamicommon.util.mixin;

import com.kamikazejam.kamicommon.event.PlayerPSTeleportEvent;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.engine.EngineTeleportMixinCause;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import com.kamikazejam.kamicommon.util.teleport.Destination;
import com.kamikazejam.kamicommon.util.teleport.ScheduledTeleport;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class MixinTeleport extends Mixin {

	public interface TeleportCallback {
		void run();
	}

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final MixinTeleport d = new MixinTeleport();
	@SuppressWarnings("FieldMayBeFinal")
	private static MixinTeleport i = d;

	@Contract(pure = true)
	public static MixinTeleport get() {
		return i;
	}

	// -------------------------------------------- //
	// METHODS
	// -------------------------------------------- //

	public boolean isCausedByMixin(PlayerTeleportEvent event) {
		return EngineTeleportMixinCause.get().isCausedByTeleportMixin(event);
	}

	public void teleport(Object teleportee, Destination destination) throws KamiCommonException {
		this.teleport(teleportee, destination, 0);
	}

	public void teleport(Object teleportee, Destination destination, Permissible delayPermissible) throws KamiCommonException {
		int delaySeconds = KUtil.getTpdelay(delayPermissible);
		this.teleport(teleportee, destination, delaySeconds);
	}

	// -------------------------------------------- //
	// CORE LOGIC
	// -------------------------------------------- //

	public static void teleportPlayer(@NotNull Player player, @NotNull PS ps) throws KamiCommonException {
		// Base the PS location on the entity location
		ps = ps.getEntity(true);
		ps = PS.valueOf(player.getLocation()).with(ps);

		// Bukkit Location
		Location location;
		try {
			location = ps.asBukkitLocation();

		} catch (Exception e) {
			String s = String.format("&cCould not calculate the location: %s", e.getMessage());
			throw new KamiCommonException().addMsg(StringUtil.t(s));
		}

		// eject passengers and unmount before transport
		player.eject();
		Entity vehicle = player.getVehicle();
		if (vehicle != null) vehicle.eject();

		// Do the teleport
		EngineTeleportMixinCause.get().setMixinCausedTeleportIncoming(true);
		player.teleport(location);
		EngineTeleportMixinCause.get().setMixinCausedTeleportIncoming(false);

		// Bukkit velocity
		Vector velocity;
		try {
			velocity = ps.asBukkitVelocity();
		} catch (Exception e) {
			return;
		}
		// IntegrationSpartan.get().disableVelocityProtection(player);
		player.setVelocity(velocity);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	public void teleport(Object teleporteeObject, Destination destination, int delaySeconds) throws KamiCommonException {
		this.teleportInternal(teleporteeObject, destination, null, null, delaySeconds);
	}

	public void teleport(Object teleporteeObject, TeleportCallback callback, int delaySeconds) throws KamiCommonException {
		this.teleportInternal(teleporteeObject, null, callback, null, delaySeconds);
	}
	public void teleport(Object teleporteeObject, TeleportCallback callback, String desc, int delaySeconds) throws KamiCommonException {
		this.teleportInternal(teleporteeObject, null, callback, desc, delaySeconds);
	}

	public void teleportInternal(Object teleporteeObject, @Nullable Destination destination, @Nullable TeleportCallback callback, @Nullable String desc, int delaySeconds) throws KamiCommonException {
		String teleporteeId = IdUtilLocal.getId(teleporteeObject);
		if (!IdUtilLocal.isPlayerId(teleporteeId)) {
			String s = String.format("&f%s &cis not a player.", MixinDisplayName.get().getDisplayName(teleporteeId, IdUtilLocal.getConsole()));
			throw new KamiCommonException().addMsg(StringUtil.t(s));
		}

		if (delaySeconds > 0) {
			if (desc == null && destination != null) {
				desc = destination.getDesc(teleporteeId);
			}

			// With delay
			CommandSender sender = KUtil.getSender(teleporteeId);
			if (desc != null && !desc.isEmpty()) {
				if (sender != null) {
					String s = "&eTeleporting to &d" + desc + " &ein &d" + delaySeconds + "s &eunless you move.";
					sender.sendMessage(StringUtil.t(s));
				}
			} else {
				if (sender != null) {
					String s = "&eTeleporting in &d" + delaySeconds + "s &eunless you move.";
					sender.sendMessage(StringUtil.t(s));
				}
			}

			if (destination != null) {
				new ScheduledTeleport(teleporteeId, destination, desc, delaySeconds).schedule();
			}else if (callback != null) {
				new ScheduledTeleport(teleporteeId, callback, desc, delaySeconds).schedule();
			}

		} else if (destination != null || callback != null) {
			if (destination != null) {
				// Without delay AKA "now"/"at once"
				PS ps;
				try {
					ps = destination.getPs(teleporteeId);
				} catch (Exception e) {
					throw new KamiCommonException().addMsg(e.getMessage());
				}

				// Run event
				PlayerPSTeleportEvent event = new PlayerPSTeleportEvent(teleporteeId, MixinSenderPs.get().getSenderPs(teleporteeId), destination);
				event.run();
				if (event.isCancelled()) return;
				destination = event.getDestination();
				desc = destination.getDesc(teleporteeId);

				if (desc != null && !desc.isEmpty()) {
					CommandSender sender = KUtil.getSender(teleporteeId);
					if (sender != null) {
						sender.sendMessage(StringUtil.t("&eTeleporting to &d" + desc + "&e."));
					}
				}

				Player teleportee = IdUtilLocal.getPlayer(teleporteeId);
				if (teleportee != null) {
					teleportPlayer(teleportee, ps);
				} else {
					MixinSenderPs.get().setSenderPs(teleporteeId, ps);
				}
			}else {
				if (desc != null && !desc.isEmpty()) {
					CommandSender sender = KUtil.getSender(teleporteeId);
					if (sender != null) {
						sender.sendMessage(StringUtil.t("&eTeleporting to &d" + desc + "&e."));
					}
				}

				callback.run();
			}
		}
	}

}

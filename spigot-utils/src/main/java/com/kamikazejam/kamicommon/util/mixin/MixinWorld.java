package com.kamikazejam.kamicommon.util.mixin;

import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import com.kamikazejam.kamicommon.util.teleport.ps.PSFormatDesc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class MixinWorld extends Mixin {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final MixinWorld d = new MixinWorld();
	@SuppressWarnings("FieldMayBeFinal")
	private static MixinWorld i = d;

	@Contract(pure = true)
	public static MixinWorld get() {
		return i;
	}

	// -------------------------------------------- //
	// METHODS
	// -------------------------------------------- //

	public boolean canSeeWorld(Permissible permissible, String worldId) {
		return true;
	}

	public List<String> getWorldIds() {
		// Create
		List<String> ret = new ArrayList<>();

		// Fill
		for (World world : Bukkit.getWorlds()) {
			ret.add(world.getName());
		}

		// Return
		return ret;
	}

	public List<String> getVisibleWorldIds(Permissible permissible) {
		// Create
		List<String> ret = new ArrayList<>();

		// Fill
		for (String worldId : this.getWorldIds()) {
			if (!this.canSeeWorld(permissible, worldId)) continue;
			ret.add(worldId);
		}

		// Return
		return ret;
	}

	public ChatColor getWorldColor(String worldId) {
		return ChatColor.WHITE;
	}

	public List<String> getWorldAliases(String worldId) {
		return new ArrayList<>();
	}

	public String getWorldAliasOrId(String worldId) {
		List<String> aliases = this.getWorldAliases(worldId);
		if (!aliases.isEmpty()) return aliases.getFirst();
		return worldId;
	}

	public String getWorldDisplayName(String worldId) {
		return this.getWorldColor(worldId).toString() + this.getWorldAliasOrId(worldId);
	}

	public PS getWorldSpawnPs(String worldId) {
		World world = Bukkit.getWorld(worldId);
		if (world == null) return null;
		return PS.valueOf(world.getSpawnLocation());
	}

	public void setWorldSpawnPs(String worldId, PS spawnPs) {
		World world = Bukkit.getWorld(worldId);
		if (world == null) return;

		spawnPs = spawnPs.withWorld(world.getName());

		Location location;
		try {
			location = spawnPs.asBukkitLocation(true);
		} catch (Exception e) {
			return;
		}

		world.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public boolean trySetWorldSpawnWp(CommandSender sender, String worldId, PS goal, boolean verboseChange, boolean verboseSame) {
		World world = Bukkit.getWorld(worldId);
		if (world == null) {
			if (verboseChange || verboseSame) {
				sender.sendMessage(String.format(StringUtil.t("&cUnknown world &d%s&c."), worldId));
			}
			return false;
		}

		// Pre Calculations
		String worldDisplayName = MixinWorld.get().getWorldDisplayName(worldId);
		PS current = this.getWorldSpawnPs(worldId);
		String currentFormatted = current.toString(PSFormatDesc.get());
		String goalFormatted = goal.toString(PSFormatDesc.get());

		// No change?
		if (KUtil.equals(goal, current)) {
			if (verboseSame) {
				String s = String.format("&eSpawn location is already &d%s &efor &d%s&e.", currentFormatted, worldDisplayName);
				sender.sendMessage(StringUtil.t(s));
			}
			return true;
		}

		// Report
		if (verboseChange) {
			String s = String.format("&eChanging spawn location from &d%s &eto &d%s &efor &d%s&e.", currentFormatted, goalFormatted, worldDisplayName);
			sender.sendMessage(StringUtil.t(s));
		}

		// Set it
		this.setWorldSpawnPs(worldId, goal);

		// Return
		return true;
	}

}

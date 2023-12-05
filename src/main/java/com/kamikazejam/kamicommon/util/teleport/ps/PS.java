package com.kamikazejam.kamicommon.util.teleport.ps;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.comparator.ComparatorSmart;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * # Introduction
 * PS stands for PhysicalState.
 * This class stores data related to just that.
 * When coding plugins you may find yourself wanting to store a player location.
 * Another time you may want to store the player location but without the worldName info.
 * Another time you may want to store pitch and yaw only.
 * This class is supposed to be usable in all those cases.
 * Hopefully this class will save you from implementing special classes for all those combinations.
 * <p>
 * # Field Groups
 * velocity: velocityX, velocityY, velocityZ
 * blockCoords: blockX, blockY, blockZ
 * locationCoords: locationX, locationY, locationZ
 * chunkCoords: chunkX, chunkZ
 * head: pitch, yaw
 * block: world, blockX, blockY, blockZ
 * location: world, locationX, locationY, locationZ, pitch, yaw
 * chunk: world, chunkX, chunkZ
 * entity: world, locationX, locationY, locationZ, pitch, yaw, velocityX, velocityY, velocityZ
 */
@SuppressWarnings({"unused", "SpellCheckingInspection", "DuplicatedCode"})
public final class PS implements Serializable, Comparable<PS> {
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	public static final float DEFAULT_BUKKIT_PITCH = 0F;
	public static final float DEFAULT_BUKKIT_YAW = 0F;

	public static final String NAME_SERIALIZED_WORLD = "w";
	public static final String NAME_SERIALIZED_BLOCKX = "bx";
	public static final String NAME_SERIALIZED_BLOCKY = "by";
	public static final String NAME_SERIALIZED_BLOCKZ = "bz";
	public static final String NAME_SERIALIZED_LOCATIONX = "lx";
	public static final String NAME_SERIALIZED_LOCATIONY = "ly";
	public static final String NAME_SERIALIZED_LOCATIONZ = "lz";
	public static final String NAME_SERIALIZED_CHUNKX = "cx";
	public static final String NAME_SERIALIZED_CHUNKZ = "cz";
	public static final String NAME_SERIALIZED_PITCH = "p";
	public static final String NAME_SERIALIZED_YAW = "y";
	public static final String NAME_SERIALIZED_VELOCITYX = "vx";
	public static final String NAME_SERIALIZED_VELOCITYY = "vy";
	public static final String NAME_SERIALIZED_VELOCITYZ = "vz";

	public static final String NAME_FULL_WORLD = "world";
	public static final String NAME_FULL_BLOCKX = "blockX";
	public static final String NAME_FULL_BLOCKY = "blockY";
	public static final String NAME_FULL_BLOCKZ = "blockZ";
	public static final String NAME_FULL_LOCATIONX = "locationX";
	public static final String NAME_FULL_LOCATIONY = "locationY";
	public static final String NAME_FULL_LOCATIONZ = "locationZ";
	public static final String NAME_FULL_CHUNKX = "chunkX";
	public static final String NAME_FULL_CHUNKZ = "chunkZ";
	public static final String NAME_FULL_PITCH = "pitch";
	public static final String NAME_FULL_YAW = "yaw";
	public static final String NAME_FULL_VELOCITYX = "velocityX";
	public static final String NAME_FULL_VELOCITYY = "velocityY";
	public static final String NAME_FULL_VELOCITYZ = "velocityZ";

	public static final String NAME_VERBOSE_WORLD = "World";
	public static final String NAME_VERBOSE_BLOCKX = "Block X";
	public static final String NAME_VERBOSE_BLOCKY = "Block Y";
	public static final String NAME_VERBOSE_BLOCKZ = "Block Z";
	public static final String NAME_VERBOSE_LOCATIONX = "Location X";
	public static final String NAME_VERBOSE_LOCATIONY = "Location Y";
	public static final String NAME_VERBOSE_LOCATIONZ = "Location Z";
	public static final String NAME_VERBOSE_CHUNKX = "Chunk X";
	public static final String NAME_VERBOSE_CHUNKZ = "Chunk Z";
	public static final String NAME_VERBOSE_PITCH = "Pitch";
	public static final String NAME_VERBOSE_YAW = "Yaw";
	public static final String NAME_VERBOSE_VELOCITYX = "Velocity X";
	public static final String NAME_VERBOSE_VELOCITYY = "Velocity Y";
	public static final String NAME_VERBOSE_VELOCITYZ = "Velocity Z";

	@Deprecated
	public static final String NAME_VERBOOSE_WORLD = "World";
	@Deprecated
	public static final String NAME_VERBOOSE_BLOCKX = "Block X";
	@Deprecated
	public static final String NAME_VERBOOSE_BLOCKY = "Block Y";
	@Deprecated
	public static final String NAME_VERBOOSE_BLOCKZ = "Block Z";
	@Deprecated
	public static final String NAME_VERBOOSE_LOCATIONX = "Location X";
	@Deprecated
	public static final String NAME_VERBOOSE_LOCATIONY = "Location Y";
	@Deprecated
	public static final String NAME_VERBOOSE_LOCATIONZ = "Location Z";
	@Deprecated
	public static final String NAME_VERBOOSE_CHUNKX = "Chunk X";
	@Deprecated
	public static final String NAME_VERBOOSE_CHUNKZ = "Chunk Z";
	@Deprecated
	public static final String NAME_VERBOOSE_PITCH = "Pitch";
	@Deprecated
	public static final String NAME_VERBOOSE_YAW = "Yaw";
	@Deprecated
	public static final String NAME_VERBOOSE_VELOCITYX = "Velocity X";
	@Deprecated
	public static final String NAME_VERBOOSE_VELOCITYY = "Velocity Y";
	@Deprecated
	public static final String NAME_VERBOOSE_VELOCITYZ = "Velocity Z";

	public static final String SPACE_WASNT_SET = " wasn't set";

	// -------------------------------------------- //
	// STANDARD INSTANCES
	// -------------------------------------------- //

	public static final PS NULL = new PS(null, null, null, null, null, null, null, null, null, null, null, null, null, null);

	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //

	@Getter
	@SerializedName(NAME_SERIALIZED_WORLD)
	private final String world;

	@Getter
	@SerializedName(NAME_SERIALIZED_BLOCKX)
	private final Integer blockX;

	@Getter
	@SerializedName(NAME_SERIALIZED_BLOCKY)
	private final Integer blockY;

	@Getter
	@SerializedName(NAME_SERIALIZED_BLOCKZ)
	private final Integer blockZ;

	@Getter
	@SerializedName(NAME_SERIALIZED_LOCATIONX)
	private final Double locationX;

	@Getter
	@SerializedName(NAME_SERIALIZED_LOCATIONY)
	private final Double locationY;

	@Getter
	@SerializedName(NAME_SERIALIZED_LOCATIONZ)
	private final Double locationZ;

	@Getter
	@SerializedName(NAME_SERIALIZED_CHUNKX)
	private final Integer chunkX;

	@Getter
	@SerializedName(NAME_SERIALIZED_CHUNKZ)
	private final Integer chunkZ;

	@Getter
	@SerializedName(NAME_SERIALIZED_PITCH)
	private final Float pitch;

	@Getter
	@SerializedName(NAME_SERIALIZED_YAW)
	private final Float yaw;

	@Getter
	@SerializedName(NAME_SERIALIZED_VELOCITYX)
	private final Double velocityX;

	@Getter
	@SerializedName(NAME_SERIALIZED_VELOCITYY)
	private final Double velocityY;

	@Getter
	@SerializedName(NAME_SERIALIZED_VELOCITYZ)
	private final Double velocityZ;

	public Integer getRegionX() {
		return this.chunkX == null ? null : this.chunkX >> 5;
	}

	public Integer getRegionZ() {
		return this.chunkZ == null ? null : this.chunkZ >> 5;
	}

	// -------------------------------------------- //
	// FIELDS: WITH
	// -------------------------------------------- //

	@Contract("_ -> new")
	public @NotNull PS withWorld(String world) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withBlockX(Integer blockX) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withBlockY(Integer blockY) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withBlockZ(Integer blockZ) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withLocationX(Double locationX) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withLocationY(Double locationY) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withLocationZ(Double locationZ) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withChunkX(Integer chunkX) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withChunkZ(Integer chunkZ) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withPitch(Float pitch) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withYaw(Float yaw) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withVelocityX(Double velocityX) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withVelocityY(Double velocityY) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("_ -> new")
	public @NotNull PS withVelocityZ(Double velocityZ) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	public PS with(@NotNull PS ps) {
		PSBuilder builder = this.builder();

		if (ps.getWorld() != null) builder.world(ps.getWorld());
		if (ps.getBlockX() != null) builder.blockX(ps.getBlockX());
		if (ps.getBlockY() != null) builder.blockY(ps.getBlockY());
		if (ps.getBlockZ() != null) builder.blockZ(ps.getBlockZ());
		if (ps.getLocationX() != null) builder.locationX(ps.getLocationX());
		if (ps.getLocationY() != null) builder.locationY(ps.getLocationY());
		if (ps.getLocationZ() != null) builder.locationZ(ps.getLocationZ());
		if (ps.getChunkX() != null) builder.chunkX(ps.getChunkX());
		if (ps.getChunkZ() != null) builder.chunkZ(ps.getChunkZ());
		if (ps.getPitch() != null) builder.pitch(ps.getPitch());
		if (ps.getYaw() != null) builder.yaw(ps.getYaw());
		if (ps.getVelocityX() != null) builder.velocityX(ps.getVelocityX());
		if (ps.getVelocityY() != null) builder.velocityY(ps.getVelocityY());
		if (ps.getVelocityZ() != null) builder.velocityZ(ps.getVelocityZ());

		return builder.build();
	}

	// -------------------------------------------- //
	// FIELDS: PLUS
	// -------------------------------------------- //

	public PS plusChunkCoords(int chunkX, int chunkZ) {
		PSBuilder builder = this.builder();

		if (builder.chunkX() != null) {
			builder.chunkX(builder.chunkX() + chunkX);
		}

		if (builder.chunkZ() != null) {
			builder.chunkZ(builder.chunkZ() + chunkZ);
		}

		return builder.build();
	}

	// -------------------------------------------- //
	// PRIVATE CONSTRUCTOR
	// -------------------------------------------- //

	private PS(String worldName, Integer blockX, Integer blockY, Integer blockZ, Double locationX, Double locationY, Double locationZ, Integer chunkX, Integer chunkZ, Float pitch, Float yaw, Double velocityX, Double velocityY, Double velocityZ) {
		this.world = worldName;
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.locationX = throwIfStrange(locationX, NAME_VERBOSE_LOCATIONX);
		this.locationY = throwIfStrange(locationY, NAME_VERBOSE_LOCATIONY);
		this.locationZ = throwIfStrange(locationZ, NAME_VERBOSE_LOCATIONZ);
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.pitch = throwIfStrange(pitch, NAME_VERBOSE_PITCH);
		this.yaw = throwIfStrange(yaw, NAME_VERBOSE_YAW);
		this.velocityX = throwIfStrange(velocityX, NAME_VERBOSE_VELOCITYX);
		this.velocityY = throwIfStrange(velocityY, NAME_VERBOSE_VELOCITYY);
		this.velocityZ = throwIfStrange(velocityZ, NAME_VERBOSE_VELOCITYZ);
	}

	@Contract("null, _ -> null")
	public static Double throwIfStrange(Double d, String name) {
		if (d == null) return null;
		if (d.isInfinite()) throw new IllegalArgumentException(name + " should not be Infinite!");
		if (d.isNaN()) throw new IllegalArgumentException(name + " should not be NaN!");
		return d;
	}

	@Contract("null, _ -> null")
	public static Float throwIfStrange(Float f, String name) {
		if (f == null) return null;
		if (f.isInfinite()) throw new IllegalArgumentException(name + " should not be Infinite!");
		if (f.isNaN()) throw new IllegalArgumentException(name + " should not be NaN!");
		return f;
	}

	// -------------------------------------------- //
	// BUILDER
	// -------------------------------------------- //

	@Contract(" -> new")
	public @NotNull PSBuilder builder() {
		return new PSBuilder(this);
	}

	// -------------------------------------------- //
	// FACTORY: VALUE OF
	// -------------------------------------------- //

	@Contract("_, _, _, _, _, _, _, _, _, _, _, _, _, _ -> new")
	public static @NotNull PS valueOf(String world, Integer blockX, Integer blockY, Integer blockZ, Double locationX, Double locationY, Double locationZ, Integer chunkX, Integer chunkZ, Float pitch, Float yaw, Double velocityX, Double velocityY, Double velocityZ) {
		return new PS(world, blockX, blockY, blockZ, locationX, locationY, locationZ, chunkX, chunkZ, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("null -> null; !null -> !null")
	public static PS valueOf(Location location) {
		if (location == null) return null;
		String world = calcWorldName(location.getWorld());
		Double locationX = location.getX();
		Double locationY = location.getY();
		Double locationZ = location.getZ();
		Float pitch = location.getPitch();
		Float yaw = location.getYaw();
		return valueOf(world, null, null, null, locationX, locationY, locationZ, null, null, pitch, yaw, null, null, null);
	}

	@Contract("null -> null; !null -> !null")
	public static PS valueOf(Vector velocity) {
		if (velocity == null) return null;
		Double velocityX = velocity.getX();
		Double velocityY = velocity.getY();
		Double velocityZ = velocity.getZ();
		return valueOf(null, null, null, null, null, null, null, null, null, null, null, velocityX, velocityY, velocityZ);
	}

	@Contract("null -> null; !null -> !null")
	public static PS valueOf(Entity entity) {
		if (entity == null) return null;

		Location location = entity.getLocation();
		String world = calcWorldName(location.getWorld());
		Double locationX = location.getX();
		Double locationY = location.getY();
		Double locationZ = location.getZ();
		Float pitch = location.getPitch();
		Float yaw = location.getYaw();

		Vector velocity = entity.getVelocity();
		Double velocityX = velocity.getX();
		Double velocityY = trimEntityVelocityY(velocity.getY());
		Double velocityZ = velocity.getZ();

		return valueOf(world, null, null, null, locationX, locationY, locationZ, null, null, pitch, yaw, velocityX, velocityY, velocityZ);
	}

	@Contract("null -> null; !null -> !null")
	public static PS valueOf(Block block) {
		if (block == null) return null;
		String world = calcWorldName(block.getWorld());
		Integer blockX = block.getX();
		Integer blockY = block.getY();
		Integer blockZ = block.getZ();
		return valueOf(world, blockX, blockY, blockZ, null, null, null, null, null, null, null, null, null, null);
	}

	@Contract("_, _, _ -> new")
	public static @NotNull PS valueOf(String world, int chunkX, int chunkZ) {
		return valueOf(world, null, null, null, null, null, null, chunkX, chunkZ, null, null, null, null, null);
	}

	@Contract("null -> null; !null -> !null")
	public static PS valueOf(Chunk chunk) {
		if (chunk == null) return null;
		String world = calcWorldName(chunk.getWorld());
		int chunkX = chunk.getX();
		int chunkZ = chunk.getZ();
		return valueOf(world, chunkX, chunkZ);
	}

	@Contract("_, _ -> new")
	public static @NotNull PS valueOf(int chunkX, int chunkZ) {
		return valueOf(null, null, null, null, null, null, null, chunkX, chunkZ, null, null, null, null, null);
	}


	@Contract("null -> null")
	public static PS valueOf(final JsonElement jsonElement) {
		if (jsonElement == null) return null;
		if (jsonElement.isJsonNull()) return null;

		final JsonObject jsonObject = jsonElement.getAsJsonObject();
		final PSBuilder builder = new PSBuilder();

		if (jsonObject.has("world") && jsonObject.has("yaw")) {
			// Old Faction LazyLocation
			for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				final String key = entry.getKey();
				final JsonElement value = entry.getValue();

				if ("world".equals(key)) {
					builder.world(value.getAsString());
				} else if ("x".equals(key)) {
					builder.locationX(value.getAsDouble());
				} else if ("y".equals(key)) {
					builder.locationY(value.getAsDouble());
				} else if ("z".equals(key)) {
					builder.locationZ(value.getAsDouble());
				} else if ("pitch".equals(key)) {
					builder.pitch(value.getAsFloat());
				} else if ("yaw".equals(key)) {
					builder.yaw(value.getAsFloat());
				}
			}
		} else {
			// The Standard Format
			for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				final String key = entry.getKey();
				final JsonElement value = entry.getValue();

				if (NAME_SERIALIZED_WORLD.equals(key)) {
					builder.world(value.getAsString());
				} else if (NAME_SERIALIZED_BLOCKX.equals(key)) {
					builder.blockX(value.getAsInt());
				} else if (NAME_SERIALIZED_BLOCKY.equals(key)) {
					builder.blockY(value.getAsInt());
				} else if (NAME_SERIALIZED_BLOCKZ.equals(key)) {
					builder.blockZ(value.getAsInt());
				} else if (NAME_SERIALIZED_LOCATIONX.equals(key)) {
					builder.locationX(value.getAsDouble());
				} else if (NAME_SERIALIZED_LOCATIONY.equals(key)) {
					builder.locationY(value.getAsDouble());
				} else if (NAME_SERIALIZED_LOCATIONZ.equals(key)) {
					builder.locationZ(value.getAsDouble());
				} else if (NAME_SERIALIZED_CHUNKX.equals(key)) {
					builder.chunkX(value.getAsInt());
				} else if (NAME_SERIALIZED_CHUNKZ.equals(key)) {
					builder.chunkZ(value.getAsInt());
				} else if (NAME_SERIALIZED_PITCH.equals(key)) {
					builder.pitch(value.getAsFloat());
				} else if (NAME_SERIALIZED_YAW.equals(key)) {
					builder.yaw(value.getAsFloat());
				} else if (NAME_SERIALIZED_VELOCITYX.equals(key)) {
					builder.velocityX(value.getAsDouble());
				} else if (NAME_SERIALIZED_VELOCITYY.equals(key)) {
					builder.velocityY(value.getAsDouble());
				} else if (NAME_SERIALIZED_VELOCITYZ.equals(key)) {
					builder.velocityZ(value.getAsDouble());
				}
			}
		}

		return builder.build();
	}

	// -------------------------------------------- //
	// GET SINGLE FIELD (CALC FLAG INCLUDED)
	// -------------------------------------------- //

	public String getWorld(boolean calc) {
		return world;
	}

	public Integer getBlockX(boolean calc) {
		return getBlockCoord(calc, locationX, blockX, chunkX);
	}

	public Integer getBlockY(boolean calc) {
		return getBlockCoord(calc, locationY, blockY, null);
	}

	public Integer getBlockZ(boolean calc) {
		return getBlockCoord(calc, locationZ, blockZ, chunkZ);
	}

	public Double getLocationX(boolean calc) {
		return getLocationCoord(calc, locationX, blockX, chunkX);
	}

	public Double getLocationY(boolean calc) {
		return getLocationCoord(calc, locationY, blockY, null);
	}

	public Double getLocationZ(boolean calc) {
		return getLocationCoord(calc, locationZ, blockZ, chunkZ);
	}

	public Integer getChunkX(boolean calc) {
		return getChunkCoord(calc, locationX, blockX, chunkX);
	}

	public Integer getChunkZ(boolean calc) {
		return getChunkCoord(calc, locationZ, blockZ, chunkZ);
	}

	public Integer getRegionX(boolean calc) {
		return getChunkCoord(calc, locationX, blockX, chunkX) >> 5;
	}

	public Integer getRegionZ(boolean calc) {
		return getChunkCoord(calc, locationZ, blockZ, chunkZ) >> 5;
	}

	public Float getPitch(boolean calc) {
		return getPitch(calc, pitch);
	}

	public Float getYaw(boolean calc) {
		return getYaw(calc, yaw);
	}

	public Double getVelocityX(boolean calc) {
		return getVelocityCoord(calc, locationX, blockX, chunkX, velocityX);
	}

	public Double getVelocityY(boolean calc) {
		return getVelocityCoord(calc, locationY, blockY, null, velocityY);
	}

	public Double getVelocityZ(boolean calc) {
		return getVelocityCoord(calc, locationZ, blockZ, chunkZ, velocityZ);
	}

	@Contract(value = "false, _, _, _ -> param3; true, _, !null, _ -> param3", pure = true)
	public static Integer getBlockCoord(boolean calc, Double location, Integer block, Integer chunk) {
		if (calc) return calcBlockCoord(location, block, chunk);
		return block;
	}

	@Contract(value = "false, _, _, _ -> param2; true, !null, _, _ -> param2", pure = true)
	public static Double getLocationCoord(boolean calc, Double location, Integer block, Integer chunk) {
		if (calc) return calcLocationCoord(location, block, chunk);
		return location;
	}

	@Contract(value = "false, _, _, _ -> param4; true, _, _, !null -> param4", pure = true)
	public static Integer getChunkCoord(boolean calc, Double location, Integer block, Integer chunk) {
		if (calc) return calcChunkCoord(location, block, chunk);
		return chunk;
	}

	@Contract(value = "false, _ -> param2; true, !null -> param2", pure = true)
	public static Float getPitch(boolean calc, Float pitch) {
		if (calc) return calcPitch(pitch);
		return pitch;
	}

	@Contract(value = "false, _ -> param2; true, !null -> param2", pure = true)
	public static Float getYaw(boolean calc, Float yaw) {
		if (calc) return calcYaw(yaw);
		return yaw;
	}

	@Contract(value = "false, _, _, _, _ -> param5; true, _, _, _, !null -> param5", pure = true)
	public static Double getVelocityCoord(boolean calc, Double location, Integer block, Integer chunk, Double velocity) {
		if (calc) return calcVelocityCoord(location, block, chunk, velocity);
		return velocity;
	}

	@Contract(value = "_, !null, _ -> param2; null, null, null -> null", pure = true)
	public static Integer calcBlockCoord(Double location, Integer block, Integer chunk) {
		if (block != null) return block;
		if (location != null) return Location.locToBlock(location);
		if (chunk != null) return chunk * 16;
		return null;
	}

	@Contract(value = "!null, _, _ -> param1; null, null, null -> null", pure = true)
	public static Double calcLocationCoord(Double location, Integer block, Integer chunk) {
		if (location != null) return location;
		if (block != null) return (double) block;
		if (chunk != null) return chunk * 16D;
		return null;
	}

	@Contract(value = "_, _, !null -> param3; null, null, null -> null", pure = true)
	public static Integer calcChunkCoord(Double location, Integer block, Integer chunk) {
		if (chunk != null) return chunk;
		if (location != null) return Location.locToBlock(location) >> 4;
		if (block != null) return block >> 4;
		return null;
	}

	@Contract(value = "!null -> param1", pure = true)
	public static Float calcPitch(Float pitch) {
		if (pitch != null) return pitch;
		return DEFAULT_BUKKIT_PITCH;
	}

	@Contract(value = "!null -> param1", pure = true)
	public static Float calcYaw(Float yaw) {
		if (yaw != null) return yaw;
		return DEFAULT_BUKKIT_YAW;
	}

	@Contract(value = "_, _, _, !null -> param4; !null, _, _, null -> param1; null, null, null, null -> null", pure = true)
	public static Double calcVelocityCoord(Double location, Integer block, Integer chunk, Double velocity) {
		if (velocity != null) return velocity;
		if (location != null) return location;
		if (block != null) return (double) block;
		if (chunk != null) return chunk * 16D;
		return null;
	}

	// -------------------------------------------- //
	// GET FIELD GROUPS
	// -------------------------------------------- //

	public PS getVelocity() {
		return this.getVelocity(false);
	}

	public PS getVelocity(boolean calc) {
		return new PSBuilder()
				.velocityX(this.getVelocityX(calc))
				.velocityY(this.getVelocityY(calc))
				.velocityZ(this.getVelocityZ(calc))
				.build();
	}

	public PS getBlockCoords() {
		return this.getBlockCoords(false);
	}

	public PS getBlockCoords(boolean calc) {
		return new PSBuilder()
				.blockX(this.getBlockX(calc))
				.blockY(this.getBlockY(calc))
				.blockZ(this.getBlockZ(calc))
				.build();
	}

	public PS getLocationCoords() {
		return this.getLocationCoords(false);
	}

	public PS getLocationCoords(boolean calc) {
		return new PSBuilder()
				.locationX(this.getLocationX(calc))
				.locationY(this.getLocationY(calc))
				.locationZ(this.getLocationZ(calc))
				.build();
	}

	public PS getChunkCoords() {
		return this.getChunkCoords(false);
	}

	public PS getChunkCoords(boolean calc) {
		return new PSBuilder()
				.chunkX(this.getChunkX(calc))
				.chunkZ(this.getChunkZ(calc))
				.build();
	}

	public PS getHead() {
		return this.getHead(false);
	}

	public PS getHead(boolean calc) {
		return new PSBuilder()
				.pitch(this.getPitch(calc))
				.yaw(this.getYaw(calc))
				.build();
	}

	public PS getBlock() {
		return this.getBlock(false);
	}

	public PS getBlock(boolean calc) {
		return new PSBuilder()
				.world(this.getWorld(calc))
				.blockX(this.getBlockX(calc))
				.blockY(this.getBlockY(calc))
				.blockZ(this.getBlockZ(calc))
				.build();
	}

	public PS getLocation() {
		return this.getLocation(false);
	}

	public PS getLocation(boolean calc) {
		return new PSBuilder()
				.world(this.getWorld(calc))
				.locationX(this.getLocationX(calc))
				.locationY(this.getLocationY(calc))
				.locationZ(this.getLocationZ(calc))
				.pitch(this.getPitch(calc))
				.yaw(this.getYaw(calc))
				.build();
	}

	public PS getChunk() {
		return this.getChunk(false);
	}

	public PS getChunk(boolean calc) {
		return new PSBuilder()
				.world(this.getWorld(calc))
				.chunkX(this.getChunkX(calc))
				.chunkZ(this.getChunkZ(calc))
				.build();
	}

	public PS getEntity() {
		return this.getEntity(false);
	}

	public PS getEntity(boolean calc) {
		return new PSBuilder()
				.world(this.getWorld(calc))
				.locationX(this.getLocationX(calc))
				.locationY(this.getLocationY(calc))
				.locationZ(this.getLocationZ(calc))
				.pitch(this.getPitch(calc))
				.yaw(this.getYaw(calc))
				.velocityX(this.getVelocityX(false))
				.velocityY(this.getVelocityY(false))
				.velocityZ(this.getVelocityZ(false))
				.build();
	}

	// -------------------------------------------- //
	// AS BUKKIT EQUIVALENT
	// -------------------------------------------- //

	public @NotNull World asBukkitWorld() throws IllegalStateException {
		return this.asBukkitWorld(false);
	}

	public @NotNull World asBukkitWorld(boolean calc) throws IllegalStateException {
		return asBukkitWorld(this.getWorld(calc));
	}

	public @NotNull Block asBukkitBlock() throws IllegalStateException {
		return this.asBukkitBlock(false);
	}

	public @NotNull Block asBukkitBlock(boolean calc) throws IllegalStateException {
		return asBukkitBlock(this.getBlock(calc));
	}

	@Contract(" -> new")
	public @NotNull Location asBukkitLocation() throws IllegalStateException {
		return this.asBukkitLocation(false);
	}

	@Contract("_ -> new")
	public @NotNull Location asBukkitLocation(boolean calc) throws IllegalStateException {
		return asBukkitLocation(this.getLocation(calc));
	}

	public @NotNull Chunk asBukkitChunk() throws IllegalStateException {
		return this.asBukkitChunk(false);
	}

	public @NotNull Chunk asBukkitChunk(boolean calc) throws IllegalStateException {
		return asBukkitChunk(this.getChunk(calc));
	}

	@Contract(" -> new")
	public @NotNull Vector asBukkitVelocity() throws IllegalStateException {
		return this.asBukkitVelocity(false);
	}

	@Contract("_ -> new")
	public @NotNull Vector asBukkitVelocity(boolean calc) throws IllegalStateException {
		return asBukkitVelocity(this.getVelocity(calc));
	}

	@Contract("null -> fail")
	public static @NotNull World asBukkitWorld(String world) throws IllegalStateException, NullPointerException {
		if (world == null) throw new NullPointerException(NAME_FULL_WORLD + SPACE_WASNT_SET);
		World ret = Bukkit.getWorld(world);
		if (ret == null)
			throw new IllegalStateException("The world " + world + " does not exist.");
		return ret;
	}

	public static @NotNull Block asBukkitBlock(@NotNull PS ps) throws IllegalStateException {
		World world = ps.asBukkitWorld();

		Integer blockX = ps.getBlockX();
		if (blockX == null) throw new IllegalStateException(NAME_FULL_BLOCKX + SPACE_WASNT_SET);

		Integer blockY = ps.getBlockY();
		if (blockY == null) throw new IllegalStateException(NAME_FULL_BLOCKY + SPACE_WASNT_SET);

		Integer blockZ = ps.getBlockZ();
		if (blockZ == null) throw new IllegalStateException(NAME_FULL_BLOCKZ + SPACE_WASNT_SET);

		return world.getBlockAt(blockX, blockY, blockZ);
	}

	@Contract("_ -> new")
	public static @NotNull Location asBukkitLocation(@NotNull PS ps) throws IllegalStateException {
		World world = ps.asBukkitWorld();

		Double locationX = ps.getLocationX();
		if (locationX == null) throw new IllegalStateException(NAME_FULL_LOCATIONX + SPACE_WASNT_SET);

		Double locationY = ps.getLocationY();
		if (locationY == null) throw new IllegalStateException(NAME_FULL_LOCATIONY + SPACE_WASNT_SET);

		Double locationZ = ps.getLocationZ();
		if (locationZ == null) throw new IllegalStateException(NAME_FULL_LOCATIONZ + SPACE_WASNT_SET);

		Float pitch = ps.getPitch();
		if (pitch == null) pitch = DEFAULT_BUKKIT_PITCH;

		Float yaw = ps.getYaw();
		if (yaw == null) yaw = DEFAULT_BUKKIT_YAW;

		return new Location(world, locationX, locationY, locationZ, yaw, pitch);
	}

	public static @NotNull Chunk asBukkitChunk(@NotNull PS ps) throws IllegalStateException {
		World world = ps.asBukkitWorld();

		Integer chunkX = ps.getChunkX();
		if (chunkX == null) throw new IllegalStateException(NAME_FULL_CHUNKX + SPACE_WASNT_SET);

		Integer chunkZ = ps.getChunkZ();
		if (chunkZ == null) throw new IllegalStateException(NAME_FULL_CHUNKZ + SPACE_WASNT_SET);

		return world.getChunkAt(chunkX, chunkZ);
	}

	@Contract("_ -> new")
	public static @NotNull Vector asBukkitVelocity(@NotNull PS ps) throws IllegalStateException {
		Double velocityX = ps.getVelocityX();
		if (velocityX == null) throw new IllegalStateException(NAME_FULL_VELOCITYX + SPACE_WASNT_SET);

		Double velocityY = ps.getVelocityY();
		if (velocityY == null) throw new IllegalStateException(NAME_FULL_VELOCITYY + SPACE_WASNT_SET);

		Double velocityZ = ps.getVelocityZ();
		if (velocityZ == null) throw new IllegalStateException(NAME_FULL_VELOCITYZ + SPACE_WASNT_SET);

		return new Vector(velocityX, velocityY, velocityZ);
	}

	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //

	@Contract("null -> null; !null -> !null")
	public static String calcWorldName(World world) {
		if (world == null) return null;
		return world.getName();
	}

	// Because of something in the physics engine players actually
	// have a small negative velocityY even when standing still.
	// We remove this redundant small negative value.
	@Contract(value = "null -> null", pure = true)
	public static Double trimEntityVelocityY(Double velocityY) {
		if (velocityY == null) return null;
		if (velocityY >= 0) return velocityY;
		if (velocityY < -0.1D) return velocityY;
		return 0D;
	}

	// -------------------------------------------- //
	// TO STRING
	// -------------------------------------------- //

	@Override
	public String toString() {
		return this.toString(PSFormatFormal.get());
	}

	public String toString(@NotNull PSFormat format) {
		return format.format(this);
	}

	public static String toString(PS ps, @NotNull PSFormat format) {
		return format.format(ps);
	}

	// -------------------------------------------- //
	// PARTIAL COMPARES
	// -------------------------------------------- //

	@Contract("null, _ -> null; !null, null -> null")
	public static Double locationDistanceSquared(PS one, PS two) {
		if (one == null) return null;
		if (two == null) return null;

		String w1 = one.getWorld();
		String w2 = two.getWorld();

		if (!KUtil.equals(w1, w2)) return null;

		Double x1 = one.getLocationX(true);
		if (x1 == null) return null;

		Double y1 = one.getLocationY(true);
		if (y1 == null) return null;

		Double z1 = one.getLocationZ(true);
		if (z1 == null) return null;

		Double x2 = two.getLocationX(true);
		if (x2 == null) return null;

		Double y2 = two.getLocationY(true);
		if (y2 == null) return null;

		Double z2 = two.getLocationZ(true);
		if (z2 == null) return null;

		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2);
	}

	public static @Nullable Double locationDistance(PS one, PS two) {
		Double ret = locationDistanceSquared(one, two);
		if (ret == null) return null;
		return Math.sqrt(ret);
	}

	@Contract("null, _ -> false; !null, null -> false")
	public static boolean inSameWorld(PS one, PS two) {
		if (one == null) return false;
		if (two == null) return false;

		String w1 = one.getWorld();
		String w2 = two.getWorld();

		if (w1 == null) return false;
		if (w2 == null) return false;

		return w1.equalsIgnoreCase(w2);
	}

	// -------------------------------------------- //
	// GET SETS
	// -------------------------------------------- //

	public static @NotNull Set<PS> getDistinctChunks(@NotNull Collection<PS> pss) {
		Set<PS> ret = new KamiSet<>();
		for (PS ps : pss) {
			ret.add(ps.getChunk(true));
		}
		return ret;
	}

	public static @NotNull Set<String> getDistinctWorlds(@NotNull Collection<PS> pss) {
		Set<String> ret = new KamiSet<>();
		for (PS ps : pss) {
			ret.add(ps.getWorld());
		}
		return ret;
	}

	// -------------------------------------------- //
	// HASHCODE (CACHED)
	// -------------------------------------------- //

	private transient volatile boolean hashed = false;
	private transient volatile int hashcode = 0;

	@Override
	public int hashCode() {
		if (!this.hashed) {
			this.hashcode = this.calcHashCode();
			this.hashed = true;
		}
		return this.hashcode;
	}

	public int calcHashCode() {
		return PS.calcHashCode(this);
	}

	public static int calcHashCode(@NotNull PS ps) {
		return Objects.hash(
				ps.world,
				ps.blockX,
				ps.blockY,
				ps.blockZ,
				ps.locationX,
				ps.locationY,
				ps.locationZ,
				ps.chunkX,
				ps.chunkZ,
				ps.pitch,
				ps.yaw,
				ps.velocityX,
				ps.velocityY,
				ps.velocityZ
		);
	}

	// -------------------------------------------- //
	// EQUALS
	// -------------------------------------------- //

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof PS)) return false;
		PS that = (PS) object;

		return KUtil.equals(
				this.world, that.world,
				this.blockX, that.blockX,
				this.blockY, that.blockY,
				this.blockZ, that.blockZ,
				this.locationX, that.locationX,
				this.locationY, that.locationY,
				this.locationZ, that.locationZ,
				this.chunkX, that.chunkX,
				this.chunkZ, that.chunkZ,
				this.pitch, that.pitch,
				this.yaw, that.yaw,
				this.velocityX, that.velocityX,
				this.velocityY, that.velocityY,
				this.velocityZ, that.velocityZ
		);
	}

	// -------------------------------------------- //
	// COMPARE
	// -------------------------------------------- //

	@Override
	public int compareTo(@NotNull PS that) {
		return ComparatorSmart.get().compare(
				this.world, that.world,
				this.blockX, that.blockX,
				this.blockY, that.blockY,
				this.blockZ, that.blockZ,
				this.locationX, that.locationX,
				this.locationY, that.locationY,
				this.locationZ, that.locationZ,
				this.chunkX, that.chunkX,
				this.chunkZ, that.chunkZ,
				this.pitch, that.pitch,
				this.yaw, that.yaw,
				this.velocityX, that.velocityX,
				this.velocityY, that.velocityY,
				this.velocityZ, that.velocityZ
		);
	}

}

package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Objects;

@SuppressWarnings("unused")
public class DestinationUtil {
    @Contract("null -> fail")
    public static @NotNull Player getPlayer(CommandSender sender) throws KamiCommonException {
        if (!(sender instanceof Player))
            throw new KamiCommonException().addMsgFromMiniMessage("<red>You must be a player to use this destination.");
        return (Player) sender;
    }

    // We strictly avoid blocks since they have a tendency to not accept outside world coordinates.

    public static @Nullable Location getThatLocation(@NotNull LivingEntity livingEntity) {
        BlockIterator iter = createHeadlessIterator(livingEntity);
        Block block = nextSolid(iter);

        // Nothing solid in sight
        if (block == null) return null;

        Location oldLocation = livingEntity.getLocation();
        return moveLocationToBlock(oldLocation, block);
    }

    public static @Nullable Location getThereLocation(@NotNull LivingEntity livingEntity) {
        BlockIterator iter = createHeadlessIterator(livingEntity);
        Block block = nextBeforeSolid(iter);

        // Nothing solid in sight
        if (block == null) return null;

        Location oldLocation = livingEntity.getLocation();
        return moveLocationToBlock(oldLocation, block);
    }

    public static @Nullable Location getJumpLocation(@NotNull LivingEntity livingEntity) {
        BlockIterator iter = createHeadlessIterator(livingEntity);
        Block block = nextSolid(iter);

        // Nothing solid in sight
        if (block == null) return null;

        Location oldLocation = livingEntity.getLocation();
        return moveUp(moveLocationToBlock(oldLocation, block));
    }

    public static @NotNull BlockIterator createHeadlessIterator(@NotNull LivingEntity livingEntity) {
        BlockIterator ret = new BlockIterator(livingEntity, 300);
        ret.next();
        return ret;
    }

    @Contract("null -> null")
    public static Block nextSolid(Iterator<Block> iter) {
        if (iter == null) return null;
        while (iter.hasNext()) {
            Block block = iter.next();
            if (block.getType().isSolid()) return block;
        }
        return null;
    }

    @Contract("null -> null")
    public static Block nextBeforeSolid(Iterator<Block> iter) {
        if (iter == null) return null;
        Block ret = null;
        while (iter.hasNext()) {
            Block block = iter.next();
            if (block.getType().isSolid()) break;
            ret = block;
        }
        return ret;
    }

    public static @NotNull Location moveUp(@NotNull Location location) {
        Location ret = location.clone();
        while (!canStandIn(ret)) {
            ret.add(0, 1, 0);
        }
        return ret;
    }

    public static boolean canStandIn(@NotNull Location location) {
        return canStandIn(Objects.requireNonNull(location.getWorld()), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static boolean canStandIn(@NotNull World world, int x, int y, int z) {
        if (isSolid(world, x, y, z)) return false;
        if (isSolid(world, x, y + 1, z)) return false;
        return isSolid(world, x, y - 1, z);
    }

    public static boolean isSolid(@NotNull World world, int x, int y, int z) {
        if (y > world.getMaxHeight()) return false;
        if (y < 0) return false;
        return world.getBlockAt(x, y, z).getType().isSolid();
    }

    public static @NotNull Location moveLocationToBlock(@NotNull Location location, @NotNull Block block) {
        return moveLocationToBlockCoords(location, block.getX(), block.getY(), block.getZ());
    }

    public static @NotNull Location moveLocationToBlockCoords(@NotNull Location location, int x, int y, int z) {
        Location ret = location.clone();

        ret.setX(x + location.getX() - location.getBlockX());
        ret.setY(y + location.getY() - location.getBlockY());
        ret.setZ(z + location.getZ() - location.getBlockZ());

        return ret;
    }
}

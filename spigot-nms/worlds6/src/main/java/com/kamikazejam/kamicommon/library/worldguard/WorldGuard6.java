package com.kamikazejam.kamicommon.library.worldguard;

import com.kamikazejam.kamicommon.nms.wrappers.NMSWrapper;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

@SuppressWarnings({"deprecation", "SpellCheckingInspection"})
public class WorldGuard6 implements WorldGuardApi {
    private final @NotNull WorldGuardPlugin wg;
    private final @NotNull NMSWrapper<NMSWorld, World> worldWrapper;
    public WorldGuard6(@NotNull Plugin wg, @NotNull NMSWrapper<NMSWorld, World> worldWrapper) {
        this.wg = (WorldGuardPlugin) wg;
        this.worldWrapper = worldWrapper;
    }

    @Override
    public boolean canPVP(@NotNull Player player) {
        return this.canPVP(player, player.getLocation());
    }

    @Override
    public boolean canPVP(@NotNull Player player, @NotNull Location location) {
        World world = location.getWorld();
        Vector pt = toVector(location);

        RegionManager regionManager = wg.getRegionManager(world);
        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
        return set.allows(DefaultFlag.PVP);
    }

    // Check if player can build at location by worldguards rules.
    // Returns:
    //	True: Player can build in the region.
    //	False: Player can not build in the region.
    @Override
    public boolean playerCanBuild(@NotNull Player player, @NotNull Location loc) {
        return wg.canBuild(player, loc);
    }

    @Override
    public boolean hasRegionsInChunk(@NotNull Chunk chunk) {
        NMSWorld world = this.worldWrapper.get(chunk.getWorld());

        int minChunkX = chunk.getX() << 4;
        int minChunkZ = chunk.getZ() << 4;
        int maxChunkX = minChunkX + 15;
        int maxChunkZ = minChunkZ + 15;

        BlockVector minChunk = new BlockVector(minChunkX, world.getMinHeight(), minChunkZ);
        BlockVector maxChunk = new BlockVector(maxChunkX, world.getMaxHeight(), maxChunkZ);

        RegionManager regionManager = wg.getRegionManager(chunk.getWorld());
        ProtectedCuboidRegion region = new ProtectedCuboidRegion("wgfactionoverlapcheck", minChunk, maxChunk);
        Map<String, ProtectedRegion> allRegions = regionManager.getRegions();
        Collection<ProtectedRegion> allRegionsList = new ArrayList<>(allRegions.values());
        List<ProtectedRegion> overlaps;
        boolean foundregions = false;

        try {
            overlaps = region.getIntersectingRegions(allRegionsList);
            foundregions = overlaps != null && !overlaps.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return foundregions;
    }

    @Override
    public @NotNull String getVersion() {
        return wg.getDescription().getVersion();
    }
}

package com.kamikazejam.kamicommon.nms.wrappers.world;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.provider.Provider;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.ChunkProvider_1_16_R3;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunkProvider;
import lombok.Getter;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class NMSWorld_1_16_R3 implements NMSWorld {
    @Getter
    private final @NotNull World bukkitWorld;
    private final @NotNull WorldServer worldServer;
    private final @NotNull CraftWorld craftWorld;
    private final @NotNull Provider<AbstractBlockUtil> provider;
    public NMSWorld_1_16_R3(@NotNull World world, @NotNull Provider<AbstractBlockUtil> provider) {
        this.bukkitWorld = world;
        this.craftWorld = (CraftWorld) world;
        this.worldServer = this.craftWorld.getHandle();
        this.provider = provider;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.worldServer;
    }

    @Override
    public @NotNull AbstractBlockUtil getBlockUtil() {
        return this.provider.get();
    }

    @Override
    public int getMinHeight() {
        return this.bukkitWorld.getMinHeight();
    }

    @Override
    public int getMaxHeight() {
        return this.bukkitWorld.getMaxHeight();
    }

    @Override
    public @NotNull NMSChunkProvider getChunkProvider() {
        return new ChunkProvider_1_16_R3(this, this.worldServer.getChunkProvider());
    }

    @Override
    public void refreshBlockAt(@NotNull Player player, int x, int y, int z) {
        BlockPosition blockPosition = new BlockPosition(x, y, z);
        PacketPlayOutBlockChange change = new PacketPlayOutBlockChange(this.worldServer, blockPosition);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(change);
    }

    @Override
    public <T extends Entity> @NotNull T spawnEntity(@NotNull Location location, @NotNull Class<T> aClass, CreatureSpawnEvent.@NotNull SpawnReason spawnReason) {
        return this.craftWorld.spawn(location, aClass, (e) -> {}, spawnReason);
    }
}

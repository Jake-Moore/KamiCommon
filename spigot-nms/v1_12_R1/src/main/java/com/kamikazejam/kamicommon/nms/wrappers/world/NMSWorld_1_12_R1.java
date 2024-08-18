package com.kamikazejam.kamicommon.nms.wrappers.world;

import com.kamikazejam.kamicommon.nms.wrappers.chunk.ChunkProvider_1_12_R1;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunkProvider;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NMSWorld_1_12_R1 implements NMSWorld {
    private final @NotNull WorldServer worldServer;
    public NMSWorld_1_12_R1(@NotNull World world) {
        this.worldServer = ((CraftWorld) world).getHandle();
    }

    @Override
    public @NotNull Object getHandle() {
        return this.worldServer;
    }

    @Override
    public int getMinHeight() {
        return 0;
    }

    @Override
    public int getMaxHeight() {
        return this.worldServer.getHeight();
    }

    @Override
    public @NotNull NMSChunkProvider getChunkProvider() {
        return new ChunkProvider_1_12_R1(this.worldServer.getChunkProviderServer());
    }

    @Override
    public void refreshBlockAt(@NotNull Player player, int x, int y, int z) {
        BlockPosition blockPosition = new BlockPosition(x, y, z);
        PacketPlayOutBlockChange change = new PacketPlayOutBlockChange(this.worldServer, blockPosition);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(change);
    }
}

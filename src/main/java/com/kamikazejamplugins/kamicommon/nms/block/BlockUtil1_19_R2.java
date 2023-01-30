package com.kamikazejamplugins.kamicommon.nms.block;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejamplugins.kamicommon.KamiCommon;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;

public class BlockUtil1_19_R2 implements IBlockUtil {
    @Override
    public void setBlockSuperFast(Block b, XMaterial xMaterial, boolean lightUpdate, boolean physics) {

        WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
        Chunk chunk = w.k().a(b.getX() >> 4, b.getZ() >> 4, true); // load chunk (if not loaded)

        if (chunk == null) {
            KamiCommon.get().getLogger().warning("[BlockUtil] Chunk is null! " + b.getX() + ", " + b.getZ());
            b.setType(xMaterial.parseMaterial());
            return;
        }

        BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());
        IBlockData ibd = net.minecraft.world.level.block.Block.a(xMaterial.getId());

        // 1.19 is weird, this is the best I can think to do
        chunk.setBlockState(bp, ibd, false, true);
    }
}

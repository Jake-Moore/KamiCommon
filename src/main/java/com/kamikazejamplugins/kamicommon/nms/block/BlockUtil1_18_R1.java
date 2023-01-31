package com.kamikazejamplugins.kamicommon.nms.block;

import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.util.MaterialData;
import com.kamikazejamplugins.kamicommon.util.VectorW;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;

@SuppressWarnings("deprecation")
public class BlockUtil1_18_R1 extends IBlockUtil {

    @Override
    boolean supportsCombined() {
        return false;
    }

    @Override
    public void setMaterialData(VectorW v, MaterialData materialData, boolean lightUpdate, boolean physics) {

        if (materialData.getData() != 0) {
            set1_13BlockData(v, materialData, lightUpdate, physics);
            return;
        }

        WorldServer w = ((CraftWorld) v.getWorld()).getHandle();
        Chunk chunk = w.k().a(v.getBlockX() >> 4, v.getBlockZ() >> 4, true); // load chunk (if not loaded)

        if (chunk == null) {
            KamiCommon.get().getLogger().warning("[BlockUtil] Chunk is null! " + v.getX() + ", " + v.getZ());
            v.toLocation().getBlock().setType(materialData.getMaterial());
            return;
        }

        BlockPosition bp = new BlockPosition(v.getBlockX(), v.getBlockY(), v.getBlockZ());
        IBlockData ibd = net.minecraft.world.level.block.Block.a(materialData.getMaterial().getId());

        // 1.19 is weird, this is the best I can think to do
        chunk.setBlockState(bp, ibd, false, true);
    }
}

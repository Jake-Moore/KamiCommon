package com.kamikazejamplugins.kamicommon.nms.block;

import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.util.MaterialData;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;

@SuppressWarnings("deprecation")
public class BlockUtil1_18_R2 extends IBlockUtil {

    @Override
    boolean supportsCombined() {
        return false;
    }

    @Override
    public void setMaterialData(Block b, MaterialData materialData, boolean lightUpdate, boolean physics) {

        if (materialData.getData() != 0) {
            set1_13BlockData(b, materialData, lightUpdate, physics);
            return;
        }

        WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
        Chunk chunk = w.k().a(b.getX() >> 4, b.getZ() >> 4, true); // load chunk (if not loaded)

        if (chunk == null) {
            KamiCommon.get().getLogger().warning("[BlockUtil] Chunk is null! " + b.getX() + ", " + b.getZ());
            assert materialData.getMaterial() != null;
            b.setType(materialData.getMaterial());
            return;
        }

        BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());
        IBlockData ibd = net.minecraft.world.level.block.Block.a(materialData.getMaterial().getId());

        // 1.19 is weird, this is the best I can think to do
        chunk.setBlockState(bp, ibd, false, true);
    }
}

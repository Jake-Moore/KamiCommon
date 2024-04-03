package com.kamikazejam.kamicommon.nms.block;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.block.PlaceType;
import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.Chunk;
import net.minecraft.server.v1_8_R2.IBlockData;
import net.minecraft.server.v1_8_R2.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class BlockUtil1_8_R2 extends IBlockUtil {
    @Override
    public void setBlock(@NotNull Block b, @NotNull XMaterial xMaterial, @NotNull PlaceType placeType) {
        assert xMaterial.parseMaterial() != null;

        if (placeType == PlaceType.BUKKIT) {
            // physics = true, light = true
            b.setTypeIdAndData(xMaterial.parseMaterial().getId(), xMaterial.getData(), true);

        }else if (placeType == PlaceType.NO_PHYSICS) {
            // physics = false, light = true
            b.setTypeIdAndData(xMaterial.parseMaterial().getId(), xMaterial.getData(), false);

        }else if (placeType == PlaceType.NMS) {
            // physics = false, light = false
            WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
            Chunk chunk = w.getChunkAt(b.getX() >> 4, b.getZ() >> 4);
            BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());

            IBlockData ibd = net.minecraft.server.v1_8_R2.Block.getByCombinedId(legacyGetCombined(xMaterial));
            try {
                chunk.a(bp, ibd);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            w.notify(bp);
        }
    }
}

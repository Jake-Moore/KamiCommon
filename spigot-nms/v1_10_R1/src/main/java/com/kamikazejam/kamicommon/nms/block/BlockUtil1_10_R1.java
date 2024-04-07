package com.kamikazejam.kamicommon.nms.block;

import com.kamikazejam.kamicommon.xseries.XMaterial;
import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.block.PlaceType;
import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.Chunk;
import net.minecraft.server.v1_10_R1.IBlockData;
import net.minecraft.server.v1_10_R1.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"DuplicatedCode", "deprecation"})
public class BlockUtil1_10_R1 extends AbstractBlockUtil {
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

            IBlockData old = net.minecraft.server.v1_10_R1.Block.getByCombinedId(legacyGetCombined(b.getType().getId(), b.getData()));
            IBlockData ibd = net.minecraft.server.v1_10_R1.Block.getByCombinedId(legacyGetCombined(xMaterial));
            try {
                chunk.a(bp, ibd);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            w.notify(bp, old, ibd, 0);
        }
    }
}


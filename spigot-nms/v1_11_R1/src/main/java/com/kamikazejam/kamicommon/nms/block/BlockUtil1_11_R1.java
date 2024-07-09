package com.kamikazejam.kamicommon.nms.block;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.block.PlaceType;
import com.kamikazejam.kamicommon.util.data.XBlockData;
import com.kamikazejam.kamicommon.util.data.XMaterialData;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.Chunk;
import net.minecraft.server.v1_11_R1.IBlockData;
import net.minecraft.server.v1_11_R1.WorldServer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"DuplicatedCode", "deprecation"})
public class BlockUtil1_11_R1 extends AbstractBlockUtil {
    @Override
    public void setBlock(@NotNull Block b, @NotNull XBlockData blockData, @NotNull PlaceType placeType) {
        XMaterialData materialData = blockData.getMaterialData();

        byte data = materialData.getData();
        Material material = materialData.getMaterial().parseMaterial();
        assert material != null;

        if (placeType == PlaceType.BUKKIT) {
            // physics = true, light = true
            b.setTypeIdAndData(material.getId(), data, true);

        }else if (placeType == PlaceType.NO_PHYSICS) {
            // physics = false, light = true
            b.setTypeIdAndData(material.getId(), data, false);

        }else if (placeType == PlaceType.NMS) {
            // physics = false, light = false
            WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
            Chunk chunk = w.getChunkAt(b.getX() >> 4, b.getZ() >> 4);
            BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());

            IBlockData old = net.minecraft.server.v1_11_R1.Block.getByCombinedId(legacyGetCombined(b.getType().getId(), b.getData()));
            IBlockData ibd = net.minecraft.server.v1_11_R1.Block.getByCombinedId(legacyGetCombined(material, data));
            try {
                chunk.a(bp, ibd);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            w.notify(bp, old, ibd, 0);
        }
    }
}

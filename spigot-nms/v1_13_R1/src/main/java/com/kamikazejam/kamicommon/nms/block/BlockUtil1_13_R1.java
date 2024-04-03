package com.kamikazejam.kamicommon.nms.block;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtil1_13;
import com.kamikazejam.kamicommon.nms.abstraction.block.PlaceType;
import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.Chunk;
import net.minecraft.server.v1_13_R1.IBlockData;
import net.minecraft.server.v1_13_R1.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.block.data.CraftBlockData;
import org.jetbrains.annotations.NotNull;

public class BlockUtil1_13_R1 extends IBlockUtil1_13 {
    @Override
    public void setBlock(@NotNull Block b, @NotNull XMaterial xMaterial, @NotNull PlaceType placeType) {
        // In 1.13 the flattening occurred, so now we can disregard the data value in XMaterial
        assert xMaterial.parseMaterial() != null;

        if (placeType == PlaceType.BUKKIT) {
            // physics = true, light = true
            b.setType(xMaterial.parseMaterial(), true);
        }else if (placeType == PlaceType.NO_PHYSICS) {
            // physics = false, light = true
            b.setType(xMaterial.parseMaterial(), false);

        }else if (placeType == PlaceType.NMS) {
            // physics = false, light = false
            IBlockData ibd = net.minecraft.server.v1_13_R1.Block.getByCombinedId(legacyGetCombined(xMaterial));
            this.setNMS(b, ibd);
        }
    }

    @Override
    public void setBlockSuperFast(@NotNull Block b, @NotNull BlockData blockData, @NotNull PlaceType placeType) {
        if (placeType == PlaceType.BUKKIT) {
            // physics = true, light = true
            b.setBlockData(blockData, true);
        }else if (placeType == PlaceType.NO_PHYSICS) {
            // physics = false, light = true
            b.setBlockData(blockData, false);

        }else if (placeType == PlaceType.NMS) {
            // physics = false, light = false
            IBlockData ibd = ((CraftBlockData) blockData).getState();
            this.setNMS(b, ibd);
        }
    }





    // physics = false, light = false
    private void setNMS(@NotNull Block b, @NotNull IBlockData ibd) {
        WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
        Chunk chunk = w.getChunkAt(b.getX() >> 4, b.getZ() >> 4);
        BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());

        IBlockData old = chunk.getType(bp);
        try {
            chunk.a(bp, ibd, false);
            // According to co-pilot:
            // The boolean parameter in the chunk.a method is used to control whether
            //  the method should cause a block update or not.
        } catch (Throwable t) {
            t.printStackTrace();
        }
        w.notify(bp, old, ibd, 3);

        // According to co-pilot:
        /*
        The i parameter in the WorldServer.notify method is a flag that determines what kind of block
            update to perform. It's an integer where each bit represents a different kind of notification:
        1 (0b1): Cause a block update.
        2 (0b10): Send the change to clients.
        4 (0b100): Prevent the block from being re-rendered.
        8 (0b1000): Force the client to re-render the block.
        16 (0b10000): Deny a neighbor reaction (for example, fences connecting, observers pulsing).
        These flags can be combined using bitwise OR. For example, a flag of 3 (0b11) would cause a block
            update and send the change to clients.
        In your code, the flag is set to 3, which means it will cause a block update and send the change to clients.
         */
    }
}

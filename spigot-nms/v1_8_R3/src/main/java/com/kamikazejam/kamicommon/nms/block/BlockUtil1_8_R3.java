package com.kamikazejam.kamicommon.nms.block;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.block.PlaceType;
import com.kamikazejam.kamicommon.util.data.XBlockData;
import com.kamikazejam.kamicommon.util.data.XMaterialData;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

@SuppressWarnings({"deprecation", "DuplicatedCode"})
public class BlockUtil1_8_R3 extends AbstractBlockUtil {
    private final @Nullable Method method;
    @SneakyThrows
    @SuppressWarnings("all")
    public BlockUtil1_8_R3() {
        if (NmsVersion.isWineSpigot()) {
            // WineSpigot: No light and no block update
            // w.setTypeAndData(bp, ibd, -2, false);
            this.method = WorldServer.class.getMethod("setTypeAndData", BlockPosition.class, IBlockData.class, int.class, boolean.class);
            Bukkit.getLogger().info("[KamiCommon] Detected WineSpigot method for NMS block placement");
        }else {
            this.method = null;
        }
    }

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

            IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(legacyGetCombined(material, data));
            try {
                if (method != null) {
                    // WineSpigot: No light and no block update
                    method.invoke(w, bp, ibd, -2, false);
                }else {
                    // Best we can do on vanilla, still causes block update
                    // for falling blocks and liquids, but at least no light update
                    chunk.a(bp, ibd);
                    w.notify(bp);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}

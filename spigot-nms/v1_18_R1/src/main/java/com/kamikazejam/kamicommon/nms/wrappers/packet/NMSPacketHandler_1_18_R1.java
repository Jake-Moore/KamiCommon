package com.kamikazejam.kamicommon.nms.wrappers.packet;

import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.NMSOutEntityDestroy;
import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.OutEntityDestroy_1_18_R1;
import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.OutEntityStatus_1_18_R1;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NMSPacketHandler_1_18_R1 implements NMSPacketHandler {

    @Override
    public @NotNull NMSPacket wrapPacket(@NotNull Object packet) {
        if (packet instanceof ClientboundEntityEventPacket) {
            return new OutEntityStatus_1_18_R1((ClientboundEntityEventPacket) packet);
        }
        throw new IllegalArgumentException("Unknown packet type: " + packet.getClass().getName());
    }

    @Override
    public @NotNull NMSOutEntityDestroy createDestroyPacket(final int... ids) {
        return new OutEntityDestroy_1_18_R1(new ClientboundRemoveEntitiesPacket(ids));
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void sendPacket(@NotNull Player player, @NotNull NMSPacket packet) {
        ((CraftPlayer) player).getHandle().connection.send((Packet) packet.getHandle());
    }
}

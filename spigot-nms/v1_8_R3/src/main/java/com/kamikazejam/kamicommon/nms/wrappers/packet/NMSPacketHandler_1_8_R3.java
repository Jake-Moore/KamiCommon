package com.kamikazejam.kamicommon.nms.wrappers.packet;

import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.NMSOutEntityDestroy;
import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.OutEntityDestroy_1_8_R3;
import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.OutEntityStatus_1_8_R3;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NMSPacketHandler_1_8_R3 implements NMSPacketHandler {

    @Override
    public @NotNull NMSPacket wrapPacket(@NotNull Object packet) {
        if (packet instanceof PacketPlayOutEntityStatus) {
            return new OutEntityStatus_1_8_R3((PacketPlayOutEntityStatus) packet);
        }
        throw new IllegalArgumentException("Unknown packet type: " + packet.getClass().getName());
    }

    @Override
    public @NotNull NMSOutEntityDestroy createDestroyPacket(final int... ids) {
        return new OutEntityDestroy_1_8_R3(new PacketPlayOutEntityDestroy(ids));
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void sendPacket(@NotNull Player player, @NotNull NMSPacket packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet) packet.getHandle());
    }
}
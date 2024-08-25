package com.kamikazejam.kamicommon.nms.wrappers.packet;

import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.NMSOutEntityDestroy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface NMSPacketHandler {
    @NotNull
    NMSPacket wrapPacket(@NotNull Object packet) throws IllegalStateException;

    @NotNull
    NMSOutEntityDestroy createDestroyPacket(int... ids);

    void sendPacket(@NotNull Player player, @NotNull NMSPacket nmsPacket);
}

package com.kamikazejam.kamicommon.nms.wrappers.packet.impl;

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.jetbrains.annotations.NotNull;

public class OutEntityDestroy_1_20_R2 implements NMSOutEntityDestroy {
    private final @NotNull ClientboundRemoveEntitiesPacket packet;

    public OutEntityDestroy_1_20_R2(@NotNull ClientboundRemoveEntitiesPacket packet) {
        this.packet = packet;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.packet;
    }

    @Override
    public int[] getToDestroy() {
        return this.packet.getEntityIds().toIntArray();
    }
}

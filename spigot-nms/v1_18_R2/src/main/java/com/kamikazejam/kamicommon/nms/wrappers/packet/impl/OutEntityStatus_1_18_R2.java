package com.kamikazejam.kamicommon.nms.wrappers.packet.impl;

import com.kamikazejam.kamicommon.nms.reflection.FieldHandle;
import com.kamikazejam.kamicommon.nms.reflection.FieldHandles;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class OutEntityStatus_1_18_R2 implements NMSOutEntityStatus {
    private static final FieldHandle<Integer> entityHandle = (FieldHandle<Integer>) FieldHandles.getHandle("a", ClientboundEntityEventPacket.class);

    private final @NotNull ClientboundEntityEventPacket packet;

    public OutEntityStatus_1_18_R2(@NotNull ClientboundEntityEventPacket packet) {
        this.packet = packet;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.packet;
    }

    @Override
    public int getEntityID() {
        return OutEntityStatus_1_18_R2.entityHandle.get(this.packet);
    }

    @Override
    public byte getStatus() {
        return this.packet.getEventId();
    }
}

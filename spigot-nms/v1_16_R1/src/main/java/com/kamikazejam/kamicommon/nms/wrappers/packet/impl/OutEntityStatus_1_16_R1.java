package com.kamikazejam.kamicommon.nms.wrappers.packet.impl;

import com.kamikazejam.kamicommon.nms.reflection.FieldHandle;
import com.kamikazejam.kamicommon.nms.reflection.FieldHandles;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class OutEntityStatus_1_16_R1 implements NMSOutEntityStatus {
    private static final FieldHandle<Integer> entityHandle = (FieldHandle<Integer>) FieldHandles.getHandle("a", PacketPlayOutEntityStatus.class);
    private static final FieldHandle<Byte> statusHandle = (FieldHandle<Byte>) FieldHandles.getHandle("b", PacketPlayOutEntityStatus.class);

    private final @NotNull PacketPlayOutEntityStatus packet;

    public OutEntityStatus_1_16_R1(@NotNull PacketPlayOutEntityStatus packet) {
        this.packet = packet;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.packet;
    }

    @Override
    public int getEntityID() {
        return OutEntityStatus_1_16_R1.entityHandle.get(this.packet);
    }

    @Override
    public byte getStatus() {
        return OutEntityStatus_1_16_R1.statusHandle.get(this.packet);
    }
}

package com.kamikazejam.kamicommon.nms.wrappers.packet.impl;

import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacket;

@SuppressWarnings("unused")
public interface NMSOutEntityDestroy extends NMSPacket {
    int[] getToDestroy();
}

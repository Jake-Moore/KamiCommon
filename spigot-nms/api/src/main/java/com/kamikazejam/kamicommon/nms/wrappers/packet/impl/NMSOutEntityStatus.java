package com.kamikazejam.kamicommon.nms.wrappers.packet.impl;

import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacket;

@SuppressWarnings("unused")
public interface NMSOutEntityStatus extends NMSPacket {

    int getEntityID();

    byte getStatus();
}

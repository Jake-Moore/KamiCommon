package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@SuppressWarnings("unused")
public interface Destination extends Serializable {
	PS getPs(Object watcherObject) throws KamiCommonException;

	boolean hasPs();

    @NotNull
    VersionedComponent getMessagePsNull(Object watcherObject);

    @NotNull
    VersionedComponent getDesc(Object watcherObject);

	void setDesc(@NotNull VersionedComponent desc);
}

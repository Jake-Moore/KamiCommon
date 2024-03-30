package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;

import java.io.Serializable;

@SuppressWarnings("unused")
public interface Destination extends Serializable {
	PS getPs(Object watcherObject) throws KamiCommonException;

	boolean hasPs();

	String getMessagePsNull(Object watcherObject);

	String getDesc(Object watcherObject);

	void setDesc(String desc);
}

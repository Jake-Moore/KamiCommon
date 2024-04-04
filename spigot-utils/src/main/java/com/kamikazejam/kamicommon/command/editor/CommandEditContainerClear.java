package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.util.collections.ContainerUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.jetbrains.annotations.NotNull;

public class CommandEditContainerClear<O, V> extends CommandEditContainerAbstract<O, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditContainerClear(@NotNull EditSettings<O> settings, @NotNull Property<O, V> property) {
		// Super	
		super(settings, property);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void alter(V container) throws KamiCommonException {
		// Apply
		ContainerUtil.clear(container);
	}

}

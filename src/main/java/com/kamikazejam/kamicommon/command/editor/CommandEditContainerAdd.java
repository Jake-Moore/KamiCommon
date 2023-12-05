package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.collections.ContainerUtil;
import org.jetbrains.annotations.NotNull;

public class CommandEditContainerAdd<O, V> extends CommandEditContainerAbstract<O, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditContainerAdd(@NotNull EditSettings<O> settings, @NotNull Property<O, V> property) {
		// Super
		super(settings, property);

		// Aliases
		this.addAliases("put");

		// Parameters
		this.addParametersElement(true);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void alter(V container) throws KamiCommonException {
		// Args
		Object element = this.readElement();

		// Alter
		ContainerUtil.addElement(container, element);
	}

}

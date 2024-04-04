package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.command.type.Type;
import org.jetbrains.annotations.NotNull;

public class CommandEditProperties<O, V> extends CommandEditAbstract<O, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditProperties(@NotNull EditSettings<O> parentSettings, @NotNull Property<O, V> childProperty) {
		// Super
		super(parentSettings, childProperty, null);

		// Show
		this.addChild(new CommandEditShow<>(parentSettings, childProperty));

		// Parameters
		if (childProperty.isEditable()) {
			Type<V> type = this.getValueType();
			EditSettings<V> childSettings = new EditSettings<>(parentSettings, childProperty);
			for (Property<V, ?> prop : type.getInnerProperties()) {
				this.addChild(prop.createEditCommand(childSettings));
			}
		}
	}

}

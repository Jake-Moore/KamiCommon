package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.editor.CommandEditAbstract;
import com.kamikazejam.kamicommon.command.editor.Property;
import com.kamikazejam.kamicommon.util.Txt;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
public class RequirementEditorPropertyCreated extends RequirementAbstract {

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	protected final boolean createdTarget;

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	public static @NotNull RequirementEditorPropertyCreated get(boolean createdTarget) {
		return new RequirementEditorPropertyCreated(createdTarget);
	}

	public RequirementEditorPropertyCreated(boolean createdTarget) {
		this.createdTarget = createdTarget;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(CommandSender sender, KamiCommand command) {
		return this.applyInner(sender, command);
	}

	public <O, V> boolean applyInner(CommandSender sender, KamiCommand command) {
		if (!(command instanceof CommandEditAbstract)) return false;

		@SuppressWarnings("unchecked")
		CommandEditAbstract<O, V> commandEditor = (CommandEditAbstract<O, V>) command;

		Property<O, V> property = commandEditor.getProperty();
		if (property == null) return false;

		O used = commandEditor.getObject(sender);
		if (used == null) return false;

		boolean created = (property.getRaw(used) != null);

		return created == this.isCreatedTarget();
	}

	@Override
	public String createErrorMessage(CommandSender sender, KamiCommand command) {
		if (!(command instanceof CommandEditAbstract<?, ?>))
			return Txt.parse("<b>This is not an editor!");

		CommandEditAbstract<?, ?> commandEditor = (CommandEditAbstract<?, ?>) command;
		Property<?, ?> property = commandEditor.getProperty();
		return Txt.parse("<b>You must " + (this.isCreatedTarget() ? "create" : "delete") + " " + (property != null ? property.getName() : "the property") + " before you " + getDesc(command) + ".");
	}

}

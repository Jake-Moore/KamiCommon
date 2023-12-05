package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.editor.CommandEditAbstract;
import com.kamikazejam.kamicommon.util.Txt;
import org.bukkit.command.CommandSender;

public class RequirementEditorUse extends RequirementAbstract {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final RequirementEditorUse i = new RequirementEditorUse();

	public static RequirementEditorUse get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(CommandSender sender, KamiCommand command) {
		if (!(command instanceof CommandEditAbstract<?, ?>)) return false;

		CommandEditAbstract<?, ?> commandEditor = (CommandEditAbstract<?, ?>) command;
		return commandEditor.getSettings().getUsed(sender) != null;
	}

	@Override
	public String createErrorMessage(CommandSender sender, KamiCommand command) {
		if (!(command instanceof CommandEditAbstract<?, ?>))
			return Txt.parse("<b>This is not an editor!");

		CommandEditAbstract<?, ?> commandEditor = (CommandEditAbstract<?, ?>) command;
		String noun = commandEditor.getSettings().getObjectType().getName();
		String aan = Txt.aan(noun);

		return Txt.parse("<b>You must use %s %s to edit it.", aan, noun);
	}

}

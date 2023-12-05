package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.mson.Mson;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KamiCommandHelp extends KamiCommand {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	protected static KamiCommandHelp i = new KamiCommandHelp();

	public static KamiCommandHelp get() {
		return i;
	}

	public KamiCommandHelp() {
		// Aliases
		this.addAliases("?", "h", "help");

		// Parameters
		this.addParameter(Parameter.getPage());

		// Other
		this.setDesc("");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform() throws KamiCommonException {
		// Args
		int page = this.readArg();

		// Get parent command
		if (!this.hasParent()) return;
		KamiCommand parent = this.getParent();

		// Create Lines
		List<Mson> lines = new KamiList<>();
		for (Object helpline : parent.getHelp()) {
			lines.add(mson(Mson.parse("<a># "), helpline).color(ChatColor.YELLOW));
		}

		for (KamiCommand child : parent.getChildren()) {
			if (!child.isVisibleTo(sender)) continue;

			// Add another visibility check for if they don't have the perms for it
			if (!child.isFullChainMet(sender)) continue;

			lines.add(child.getTemplate(true, true, sender));
		}

		// Send Lines
		message(Txt.getPage(lines, page, "Help for command \"" + parent.getAliases().get(0) + "\"", this));
	}

	@Override
	public boolean isVisibleTo(CommandSender sender) {
		boolean visible = super.isVisibleTo(sender);
		if (!(this.hasParent() && visible)) return visible;

		int visibleSiblingCount = 0;
		for (KamiCommand sibling : this.getParent().getChildren()) {
			if (sibling instanceof KamiCommandHelp) continue;
			if (sibling.isVisibleTo(sender)) visibleSiblingCount++;
		}

		int pageHeight = (sender instanceof Player) ? Txt.PAGEHEIGHT_PLAYER : Txt.PAGEHEIGHT_CONSOLE;
		return visibleSiblingCount > pageHeight;
	}

}

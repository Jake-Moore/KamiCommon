package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.command.type.primitive.TypeInteger;
import com.kamikazejam.kamicommon.command.util.CommandPaging;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.abstraction.chat.KMessage;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageSingle;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
		this.addParameter(Parameter.of(TypeInteger.get())
				.name("page")
				.defaultValue(1)
		);

		// Other
		this.setDesc("");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform(@NotNull CommandContext context) throws KamiCommonException {
		// Args
		int page = this.readArg();

		// Get parent command
		if (!this.hasParent()) return;
		KamiCommand parent = this.getParent();

		// Create Lines
		List<KMessageSingle> lines = new ArrayList<>();

		// Add comments (if specified), using the comment format from the config
		List<KMessageSingle> comments = parent.getHelpComments();
		for (KMessageSingle single : comments) {
            String text = String.format(KamiCommand.Lang.getHelpCommentFormat(), single.getLine());
			lines.add(new KMessageSingle(StringUtil.t(text)));
		}

		CommandSender sender = context.getSender();
		for (KamiCommand child : parent.getChildren()) {
			if (!child.isVisibleTo(sender)) continue;

			// Add another visibility check for if they don't have the perms for it
			if (!child.isFullChainMet(sender)) continue;

			lines.add(parent.getHelpClickable(child, sender));
		}

		// Add title line (becomes the first line)
        @NotNull CommandContext parentContext = Preconditions.checkNotNull(parent.getContext(), "Parent command context cannot be null");
		String title = Config.getHelpTitleFormat().replace(Config.getPlaceholderTitle(), parentContext.getLabel());
        List<KMessage> messages = CommandPaging.getPage(this, lines, page, title);
		NmsAPI.getMessageManager().processAndSend(sender, messages);
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

		int pageHeight = (sender instanceof Player) ? CommandPaging.PAGEHEIGHT_PLAYER : CommandPaging.PAGEHEIGHT_CONSOLE;
		return visibleSiblingCount > pageHeight;
	}

    public static class Config {
        @Getter
        private static final @NotNull String placeholderTitle = "{TITLE}";

        @Getter @Setter
        private static @NotNull String helpTitleFormat = "Help for command \"" + placeholderTitle + "\"";
    }
}

package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@SuppressWarnings({"unused"})
public class KamiCommonBukkitCommand extends Command implements PluginIdentifiableCommand {
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	protected final KamiCommand kamiCommand;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public KamiCommonBukkitCommand(String name, @NotNull KamiCommand kamiCommand) {
		super(
				name,
				kamiCommand.getDesc(),
				kamiCommand.getFullTemplate(false, false, false, null),
				Collections.emptyList() // We don't use aliases
		);
		this.kamiCommand = kamiCommand;

        // Set the command's permission so that root-level tab completion and execution is restricted by the command's permission.
        // - Tab completion (after `/`) will only show if sender has the permission.
        // - Execution will only succeed if the sender has the permission.
        //
        // This is derived from the current permission requirements of the command.
        @Nullable String permission = this.kamiCommand.getBukkitCommandPermission();
        if (permission != null && !permission.isEmpty()) {
            this.setPermission(permission);
        }
	}

	// -------------------------------------------- //
	// OVERRIDE: PLUGIN IDENTIFIABLE COMMAND
	// -------------------------------------------- //

	@NotNull
	@Override
	public Plugin getPlugin() {
		return this.getKamiCommand().getPlugin();
	}

	// -------------------------------------------- //
	// OVERRIDE: EXECUTE
	// -------------------------------------------- //

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		List<String> argList = this.createArgList(args);
		this.getKamiCommand().execute(sender, commandLabel, argList);
		return true;
	}

	public List<String> createArgList(String[] args) {
		List<String> ret;
		if (this.getKamiCommand().isTokenizing()) {
			ret = Txt.tokenizeArguments(Txt.implode(args, " "));
		} else {
			ret = KUtil.list(args);
		}

		if (this.getKamiCommand().isUnsmart()) {
			List<String> oldArgList = ret;
			ret = new ArrayList<>(oldArgList.size());
			for (String arg : oldArgList) {
				ret.add(Txt.removeSmartQuotes(arg));
			}
		}
		return ret;
	}

	// -------------------------------------------- //
	// OVERRIDE: TAB COMPLETE
	// -------------------------------------------- //

	// NOTE: There is some Vanilla bugs, described here.

	// Test 1. These bugs occur when using commands provided by Bukkit plugins.
	// Test 2. These bugs do also occur when using BungeeCoord commands.
	// BungeeCoord commands are handled MUCH differently than Bukkit commands.
	// In fact BungeeCoord commands are not related to Bukkit at all.
	// Test 3. These bugs do also occur in plain single-player vanilla MineCraft.

	// These notes suggests that this is a client side bug and NOT a server side one.

	// BUG 1. Tab complete to first common prefix then normal.
	// Desc: Tab completes to the first common prefix of the available completions
	// after that it will tab complete normally.
	// Happens when:
	//	1. All possible suggestions has the same common prefix.
	//	2. The common prefix must be of at least two characters,
	//	3. There is more than one suggestion.
	//	4. Tab completing from the end of the chat bar. (There is only text to the left)
	//	5. The user typed in the beginning of the arg to tab complete.

	// BUG 2. Tab complete to first common prefix then nothing.
	// Desc: Tab completes to the first common prefix of the available completions
	// after that it will refuse to tab complete anymore.
	// Happens when:
	//	1. All possible suggestions has the same common prefix.
	//	3. There is more than one suggestion.
	//	4. Tab completing from the middle of the chat bar. (There text on both sides)
	//	5. The user typed in the beginning of the arg to tab complete.

	@NotNull
	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] rawArgs) throws IllegalArgumentException {
		// The JavaDocs for Command says these checks will be made.
		// So we should follow that contract.
		if (sender == null) throw new IllegalArgumentException("sender must not be null");
		if (rawArgs == null) throw new IllegalArgumentException("args must not be null");
		if (alias == null) throw new IllegalArgumentException("args must not be null");

		List<String> args = new KamiList<>();

		// When several spaces are next to each other, empty elements in the array will occur.
		// To avoid such whitespace we do the following
		// NOTE: The last arg can be empty, and will be in many cases.	
		for (int i = 0; i < rawArgs.length - 1; i++) {
			String str = rawArgs[i];
			if (str == null) continue;
			if (str.isEmpty()) continue;
			args.add(str);
		}
		// Here we add the last element.
		args.add(rawArgs[rawArgs.length - 1]);

		List<String> ret = this.getKamiCommand().getTabCompletions(args, sender);

		int retSize = ret.size();
		int maxSize = 100; // The limit for tab completions in the vanilla client is 100.
		if (retSize > maxSize) {
            String miniMessage = String.format(KamiCommand.Config.getCommandTooManyTabSuggestionsMini(), retSize);
            NmsAPI.getVersionedComponentSerializer().fromMiniMessage(miniMessage).sendTo(sender);
			return Collections.emptyList();
		}

		return ret;
	}

}

package com.kamikazejam.kamicommon.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "SameReturnValue"})
public abstract class KamiSubCommand {
	public abstract List<String> getNames();

	/**
	 * This will be checked after verifying that the command does not require admin
	 * @param sender The command sender to check permissions for
	 * @return If this sender has permission
	 */
	public abstract boolean hasPermission(CommandSender sender);

	public abstract boolean performCommand(CommandSender sender, String label, String[] args);

	public List<String> performTabComplete(CommandSender sender, String label, String[] args) {
		return new ArrayList<>();
	}

	public abstract boolean requiresPlayer();

	/**
	 * If this is true, the manager should check the admin permission and then allow the command
	 * @return If the player needs to be an admin to run this command.
	 */
	public abstract boolean requiresAdmin();

	/**
	 * Utility method for parsing args against known valid options (helpful for Tab completion)
	 * @param args The arguments from the command
	 * @param index The index of the argument to check
	 * @param options The list of valid tab completions to check
	 * @return Every option that matches or begins to match the argument at the specified index
	 */
	public List<String> getArgsAtIndex(String[] args, int index, List<String> options) {
		return getArgsAtIndex(args, index, options.toArray(new String[0]));
	}

	/**
	 * Utility method for parsing args against known valid options (helpful for Tab completion)
	 * @param args The arguments from the command
	 * @param index The index of the argument to check
	 * @param options The list of valid tab completions to check
	 * @return Every option that matches or begins to match the argument at the specified index
	 */
	public List<String> getArgsAtIndex(String[] args, int index, String... options) {
		if (args.length > index) {
			if (!args[index].trim().isEmpty()) {
				return getValidArgsFromStem(args[index], options);
			}
		}
		return Arrays.asList(options);
	}

	/**
	 * Utility method checking if a string (stem) matches or begins to match a set of options
	 * @param stem The stem to check against the options
	 * @param options The list of valid options to check
	 * @return Every option that matches or begins to match the stem
	 */
	public List<String> getValidArgsFromStem(String stem, String... options) {
		List<String> stemOptions = new ArrayList<>();
		for (String option : options) {
			if (option.toLowerCase().startsWith(stem.toLowerCase())) {
				stemOptions.add(option);
			}
		}
		return (stemOptions.isEmpty()) ? Arrays.asList(options) : stemOptions;
	}
}

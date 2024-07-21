package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.util.StringUtil;
import org.bukkit.ChatColor;

public class Lang {

    public static final String REQ_PERM_DENIED = StringUtil.t("&cYou don't have permission to do that.");

    public static final String COMMAND_SENDER_MUST_BE_PLAYER = StringUtil.t("&cThis command can only be used by ingame players.");
    public static final String COMMAND_SENDER_MUST_NOT_BE_PLAYER = StringUtil.t("&cThis command can not be used by ingame players.");
    public static final String COMMAND_TOO_FEW_ARGUMENTS = StringUtil.t("&cNot enough command input. &eYou should use it like this:");
    public static final String COMMAND_TOO_MANY_ARGUMENTS = StringUtil.t("&cToo much command input %s&c.");
    public static final String COMMAND_TOO_MANY_ARGUMENTS2 = StringUtil.t("&eYou should use the command like this:");

    public static final String COMMAND_REPLACEMENT = "{REPLACEMENT}";

    public static final String COMMAND_CHILD_AMBIGUOUS = ChatColor.YELLOW + "The sub command " + COMMAND_REPLACEMENT + ChatColor.YELLOW + " is ambiguous.";
    public static final String COMMAND_CHILD_NONE = ChatColor.YELLOW + "The sub command " + COMMAND_REPLACEMENT + ChatColor.YELLOW + " couldn't be found.";

    public static final String COMMAND_CHILD_HELP = ChatColor.YELLOW + "Use " + COMMAND_REPLACEMENT + ChatColor.YELLOW  + " to see all commands.";

    public static final String COMMAND_TOO_MANY_TAB_SUGGESTIONS = StringUtil.t("&d%d &ctab completions available. Be more specific and try again.");
}


package com.kamikazejamplugins.kamicommon.config.testing;

import com.kamikazejamplugins.kamicommon.config.annotation.ConfigValue;
import com.kamikazejamplugins.kamicommon.config.data.KamiConfig;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter @Setter
public class Config extends KamiConfig {

    @ConfigValue(above = "The following settings are help messages.\nThe first is for normal players, the second for admins.\n{prefix} is replaced with cmdHelpPrefix.")
    public List<String> cmdHelp = new ArrayList<>(Arrays.asList(
            "&6_____________.[ &2FriendlyRaid Help &6]._______________",
            "{prefix} raid &3<faction> &eInvite a faction to a friendly raid.",
            "{prefix} forfeit &eLeave a friendly raid. (You cannot rejoin)",
            "{prefix} counter &3confirm &eConfirm a counter schematic from ( %uploadsUrl% )",
            "{prefix} counter &3edit &eRemove counter schematics from your profile.",
            "{prefix} setspawn &eSet your faction's spawn point in a friendly raid.",
            "{prefix} list &eView active Friendly Raids."
    ));

    @ConfigValue()
    public List<String> cmdHelpManage = new ArrayList<>(Arrays.asList(
            "&6_____________.[ &2FriendlyRaid Help &6]._______________",
            "{prefix} raid &3<faction> &eInvite a faction to a friendly raid.",
            "{prefix} forfeit &eLeave a friendly raid. (You cannot rejoin)",
            "{prefix} counter &3confirm &eConfirm a counter schematic from ( %uploadsUrl% )",
            "{prefix} counter &3edit &eRemove counter schematics from your profile.",
            "{prefix} setspawn &eSet your faction's spawn point in a friendly raid.",
            "{prefix} list &eView active Friendly Raids.",
            "{prefix} reload &eReload the plugin config.yml",
            "{prefix} freeitems <set|add|view> &eManage free items given in a Friendly Raid.",
            "{prefix} tournament <start|stop|view> &eManage tournament mode (will start immediately)."
    ));

    @ConfigValue(above = "This is the format for the {prefix} above.\n{stem} is the command stem, e.g. /fraid.\nYou can add colors to the prefix here.")
    public String cmdHelpPrefix = "&b{stem}";

    @ConfigValue(path = "cmdUsages", above = {"", "\nThe following messages are sent when a player\n uses a command incorrectly."})
    public String counterConfirm = "{prefix} counter &3(confirm | edit)";

    public static void main(String[] args) throws Exception {
        KamiConfig.create(Config.class, new File("C:\\Users\\Jake\\Desktop\\test.yml"));
    }
}

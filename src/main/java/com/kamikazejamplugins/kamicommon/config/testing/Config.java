package com.kamikazejamplugins.kamicommon.config.testing;

//import com.kamikazejamplugins.kamicommon.config.data.KamiConfig;
//import lombok.Getter;
//import lombok.Setter;
//import org.bukkit.plugin.java.JavaPlugin;
//
//import javax.annotation.Nullable;
//import java.io.File;
//
//@Getter @Setter
//public class Config extends KamiConfig {
//
//    public Config(@Nullable JavaPlugin plugin, File file) {
//        super(plugin, file);
//
//        addCommentAbove("friendlyRaid",
//                "-----------------------------------------------------------------",
//                "Welcome to the config.yml file. Everything you see here is configurable.",
//                "<br>",
//                "Terminology: A 'raid plan' is a proposal to raid another faction. It is created",
//                " when a player runs '/fr raid <fac>' in the form of a gui.",
//                "Terminology: A 'friendly raid' is the raid world that is created when two factions",
//                " agree to raid each other.",
//                "<br>",
//                "Contact: Kore Discord Tickets",
//                "-----------------------------------------------------------------",
//                ""
//        );
//
//        addCommentAbove("friendlyRaid.blacklistedCommands",
//                "Commands that under no circumstances will be allowed.",
//                "The Whitelist is checked first, and then the blacklist to prevent those."
//        );
//
//        addCommentAbove("friendlyRaid.whitelistedCommands",
//                "Commands that will be allowed.",
//                "The blacklist can contain a more specific version of a command to block."
//        );
//
//        addCommentAbove("friendlyRaid.blacklistedShopItems",
//                "These items will not be allowed to be purchased in the shop."
//        );
//
//        addCommentAbove("friendlyRaid.bufferCounterWallChunks",
//                "How many chunks of walls should be after the front of the counter."
//        );
//
//        addCommentAbove("friendlyRaid.options",
//                "The following 5 options are the strings displayed on the items.",
//                "You can add a setting with the same name ending in DefaultIndex to specify which string in the list should be the default",
//                "Reminder: Indexes start at 0."
//        );
//
//        addCommentAbove("friendlyRaid.options.mid-chunks",
//                "This option defines how many chunks separate the two bases.",
//                "Increasing this will increase the distance between them,",
//                "  increasing the world and space in unused corners."
//        );
//
//        addCommentAbove("friendlyRaid.maxDurationMinutes",
//                "This option defines the max duration (in minutes) a friendly raid can run."
//        );
//
//        addCommentAbove("friendlyRaid.maxSimultaneousRaids",
//                "This option defines the max amount of raids that can run simultaneously."
//        );
//
//        addCommentAbove("friendlyRaid.wagerCurrencies",
//                "Here you can toggle which currencies are allowed to be wagered.",
//                "Tokens is subject to token plugin compatibility,",
//                "  and may disable itself if it can't find a plugin."
//        );
//
//        addCommentAbove("frCommandAliases",
//                "",
//                "Below are the aliases for the friendlyraid command",
//                "Do not include a slash."
//        );
//
//        addCommentAbove("factionCommandAliases",
//                "Below are the aliases for the fr command when using /f ...",
//                "You must include a slash for these."
//        );
//
//        addCommentAbove("tpaCommandAliases",
//                "",
//                "Below are the aliases for /tpa.",
//                "In general you probably don't have to touch this."
//        );
//
//        addCommentAbove("spawnRegion",
//                "",
//                "Please configure your spawn region below.",
//                "This is a cuboid that defines your spawn region."
//        );
//
//        addCommentAbove("spectate",
//                "",
//                "Below you can configure spectator settings."
//        );
//
//        addCommentAbove("spectate.warmupSec",
//                "How many seconds before enabling spectator mode.",
//                "Set to 0 to disable."
//        );
//
//        addCommentAbove("spectate.maxY",
//                "What is the max Y a spectator can fly to."
//        );
//
//        addCommentAbove("spectate.fallBackSpawn",
//                "Please configure a fallback spawn for spectators.",
//                "If a spectator's previous location is lost, this will be used!"
//        );
//
//        addCommentAbove("spectate.blacklisted-commands",
//                "These commands are not allowed to be used by spectators."
//        );
//
//        addCommentAbove("spectate.whitelisted-commands",
//                "These commands are allowed to be used by spectators."
//        );
//
//        addCommentAbove("freeItems",
//                "",
//                "Below you can configure the free items players get in a raid."
//        );
//
//        addCommentAbove("freeItems.enableFreeItemChests",
//                "Should bases contain chests with configured FreeItems?"
//        );
//
//        addCommentAbove("freeItems.enableFreeTnt",
//                "Should factions get free tnt in the world?",
//                "Cannons will be prefilled and will refill automatically."
//        );
//
//        addCommentAbove("tournament",
//                "",
//                "Below you can configure the tournament settings."
//        );
//
//        addCommentAbove("tournament.signUpSeconds",
//                "How long do players have to sign up for the tournament?"
//        );
//
//        addCommentAbove("tournament.maxDurationMins",
//                "How long (in minutes) can tournament matches run?",
//                "If they run out of time, winners will be determined by closest shot to base."
//        );
//
//        addCommentAbove("tournament.allowWagering",
//                "Should players be able to wager while setting up tournament raids?"
//        );
//    }
//
//    @SuppressWarnings("unused")
//    public static void main(String[] args) {
//        Config config = new Config(null, new File("C:\\Users\\Jake\\Desktop\\config.yml"));
//        config.save();
//    }
//}

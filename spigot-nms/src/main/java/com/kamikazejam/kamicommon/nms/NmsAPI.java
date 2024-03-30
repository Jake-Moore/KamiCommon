package com.kamikazejam.kamicommon.nms;

import com.kamikazejam.kamicommon.nms.provider.ChatColorProvider;
import org.bukkit.ChatColor;

import java.awt.Color;

@SuppressWarnings("unused")
public class NmsAPI {
    // ---------------------------------------------------------------------------------- //
    //                                    PROVIDERS                                       //
    // ---------------------------------------------------------------------------------- //
    private static final ChatColorProvider chatColorProvider = new ChatColorProvider();


    // ---------------------------------------------------------------------------------- //
    //                                   API METHODS                                      //
    // ---------------------------------------------------------------------------------- //
    public static Color getJavaColor(ChatColor chatColor) {
        return chatColorProvider.get().getColor(chatColor);
    }
}

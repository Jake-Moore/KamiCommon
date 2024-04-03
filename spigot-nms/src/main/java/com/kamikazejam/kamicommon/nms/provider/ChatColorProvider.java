package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.IChatColorNMS;
import com.kamikazejam.kamicommon.nms.chatcolor.ChatColor1_16_R1;
import com.kamikazejam.kamicommon.nms.chatcolor.ChatColor1_8_R3;
import org.jetbrains.annotations.NotNull;

public class ChatColorProvider extends Provider<IChatColorNMS> {
    @Override
    protected @NotNull IChatColorNMS provide(double formattedNmsDouble, String ignored) {
        // If we are 1.16+ we can use the getColor() method introduced when hex codes were added
        if (formattedNmsDouble >= 1160) {
            return new ChatColor1_16_R1();
        }
        // Otherwise we have to approximate
        return new ChatColor1_8_R3();
    }
}

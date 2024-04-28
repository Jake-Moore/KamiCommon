package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
import com.kamikazejam.kamicommon.nms.chat.MessageManager_1_17_R1;
import com.kamikazejam.kamicommon.nms.chat.MessageManager_1_8_R1;
import org.jetbrains.annotations.NotNull;

/**
 * !!! Gradle Compatability Requires this module to be set to Java16 !!!
 * WE ARE BUILDING FOR Java 8, do not use any Java 9+ features
 */
public class MessageManagerProvider extends Provider<AbstractMessageManager> {
    @Override
    protected @NotNull AbstractMessageManager provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Use md5 BaseComponent pre 1.17
        if (ver < f("1.17")) {
            AbstractItemTextPre_1_17 itemText = NmsAPI.getItemTextProviderPre_1_17().get();
            return new MessageManager_1_8_R1(itemText);
        }

        // Use kyori adventure Component post 1.17
        return new MessageManager_1_17_R1();
    }
}

package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.enchantid.AbstractEnchantID;
import com.kamikazejam.kamicommon.nms.enchantid.EnchantID_1_13_R1;
import com.kamikazejam.kamicommon.nms.enchantid.EnchantID_1_8_R1;
import org.jetbrains.annotations.NotNull;

public class EnchantIDProvider extends Provider<AbstractEnchantID> {
    @Override
    protected @NotNull AbstractEnchantID provide(int ver) {
        // up to 1.16.1 uses BaseComponent[] as the second parameter of HoverEvent
        if (ver < 1130) {
            return new EnchantID_1_8_R1();
        }else {
            return new EnchantID_1_13_R1();
        }
    }
}

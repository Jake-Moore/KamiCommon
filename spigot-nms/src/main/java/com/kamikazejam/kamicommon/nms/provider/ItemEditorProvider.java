package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemEditor;
import com.kamikazejam.kamicommon.nms.item.ItemEditor_1_11_R1;
import com.kamikazejam.kamicommon.nms.item.ItemEditor_1_8_R1;
import org.jetbrains.annotations.NotNull;

public class ItemEditorProvider extends Provider<AbstractItemEditor> {
    @Override
    protected @NotNull AbstractItemEditor provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver < f("1.11")) {
            return new ItemEditor_1_8_R1();
        }

        return new ItemEditor_1_11_R1();
    }
}

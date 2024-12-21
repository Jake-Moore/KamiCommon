package com.kamikazejam.kamicommon.gui.items.interfaces;

import com.kamikazejam.kamicommon.item.IBuilder;
import org.jetbrains.annotations.NotNull;

public interface IBuilderModifier {
    void modify(@NotNull IBuilder builder);
}

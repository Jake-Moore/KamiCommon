package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public abstract class Provider<T> {
    private @Nullable T value = null;
    public final @NotNull T get() {
        if (value == null) {
            value = provide(NmsVersion.getFormattedNmsDouble());
        }
        return value;
    }

    /**
     * @param formattedNmsDouble Specified by {@link NmsVersion#getFormattedNmsDouble()}
     */
    protected abstract @NotNull T provide(double formattedNmsDouble);

    public final double f(String mcVersion) {
        return NmsVersionParser.getFormattedNmsDouble(mcVersion);
    }
}

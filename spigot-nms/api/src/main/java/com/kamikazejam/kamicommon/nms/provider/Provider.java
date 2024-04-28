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
            value = provide(NmsVersion.getFormattedNmsInteger());
        }
        return value;
    }

    /**
     * @param formattedNmsInteger Specified by {@link NmsVersion#getFormattedNmsInteger()}
     */
    protected abstract @NotNull T provide(int formattedNmsInteger);

    public final int f(String mcVersion) {
        return NmsVersionParser.getFormattedNmsInteger(mcVersion);
    }
}

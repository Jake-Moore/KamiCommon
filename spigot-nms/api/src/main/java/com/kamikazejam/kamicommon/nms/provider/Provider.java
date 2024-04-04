package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Provider<T> {
    private @Nullable T value = null;
    public final @NotNull T get() {
        if (value == null) {
            value = provide(NmsVersion.getFormattedNmsDouble(), NmsVersion.getNMSVersion());
        }
        return value;
    }

    /**
     * @param formattedNmsDouble Specified by {@link NmsVersion#getFormattedNmsDouble()}
     * @param nmsVersion Specified by {@link NmsVersion#getNMSVersion()}
     */
    protected abstract @NotNull T provide(double formattedNmsDouble, String nmsVersion);
}

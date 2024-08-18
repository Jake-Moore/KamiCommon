package com.kamikazejam.kamicommon.nms.wrappers;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public abstract class NMSWrapper<A, B> {

    private @Nullable A value = null;
    public final @NotNull A get(@NotNull B b) {
        if (value == null) {
            value = provide(NmsVersion.getFormattedNmsInteger(), b);
        }
        return value;
    }

    /**
     * @param formattedNmsInteger Specified by {@link NmsVersion#getFormattedNmsInteger()}
     */
    protected abstract @NotNull A provide(int formattedNmsInteger, @NotNull B b);

    public final int f(String mcVersion) {
        return NmsVersionParser.getFormattedNmsInteger(mcVersion);
    }
}

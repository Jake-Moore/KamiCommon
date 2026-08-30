package com.kamikazejam.kamicommon.util.teleport.ps;

import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PSFormat {
    @NotNull
    VersionedComponent format(@Nullable PS ps);
}

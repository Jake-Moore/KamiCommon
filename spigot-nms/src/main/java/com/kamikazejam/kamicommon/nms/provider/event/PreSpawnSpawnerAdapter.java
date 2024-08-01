package com.kamikazejam.kamicommon.nms.provider.event;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.event.PreSpawnSpawnerAdapter_1_12_R1;
import com.kamikazejam.kamicommon.nms.event.PreSpawnSpawnerAdapter_1_21_R1;
import com.kamikazejam.kamicommon.nms.event.PreSpawnSpawnerAdapter_1_8_R3;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PreSpawnSpawnerAdapter {
    @NotNull
    public static Listener getSpawnerAdapter() {
        int nmsVersion = NmsVersion.getFormattedNmsInteger();

        // 1.8.8      -> Assume TacoSpigot fork and use SpawnerPreSpawnEvent
        if (nmsVersion == f("1.8.8")) {
            return new PreSpawnSpawnerAdapter_1_8_R3();
        }

        // 1.8.8      -> Assume TacoSpigot fork and use SpawnerPreSpawnEvent
        if (nmsVersion <= f("1.12.2")) {
            return (nmsVersion == f("1.8.8"))
                    ? new PreSpawnSpawnerAdapter_1_8_R3()
                    : new PreSpawnSpawnerAdapter_1_12_R1();
        }

        // 1.13+      -> Assume PaperSpigot and use PreSpawnerSpawnEvent
        return new PreSpawnSpawnerAdapter_1_21_R1();
    }

    private static int f(@NotNull String mcVersion) {
        return NmsVersionParser.getFormattedNmsInteger(mcVersion);
    }
}

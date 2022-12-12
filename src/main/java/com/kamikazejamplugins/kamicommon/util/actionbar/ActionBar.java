package com.kamikazejamplugins.kamicommon.util.actionbar;

import com.kamikazejamplugins.kamicommon.nms.NmsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public interface ActionBar {

    void sendToPlayer(Player p, String text);

    static @Nonnull ActionBar getInstance() {
        String version = NmsManager.getNMSVersion();
        switch(version) {
            case "v1_8_R1":
                return new ActionBar_1_8_R1();
            case "v1_8_R2":
                return new ActionBar_1_8_R2();
            case "v1_8_R3":
                return new ActionBar_1_8_R3();
            case "v1_9_R1":
                return new ActionBar_1_9_R1();
            case "v1_9_R2":
                return new ActionBar_1_9_R2();
            case "v1_10_R1":
                return new ActionBar_1_10_R1();
            case "v1_11_R1":
                return new ActionBar_1_11_R1();
            case "v1_12_R1":
                return new ActionBar_1_12_R1();
            case "v1_13_R1":
                return new ActionBar_1_13_R1();
            case "v1_13_R2":
                return new ActionBar_1_13_R2();
            case "v1_14_R1":
                return new ActionBar_1_14_R1();
            case "v1_15_R1":
                return new ActionBar_1_15_R1();
            case "v1_16_R1":
                return new ActionBar_1_16_R1();
            case "v1_16_R2":
                return new ActionBar_1_16_R2();
            case "v1_16_R3":
                return new ActionBar_1_16_R3();
            case "v1_17_R1":
                return new ActionBar_1_17_R1();
            case "v1_18_R1":
                return new ActionBar_1_18_R1();
            case "v1_18_R2":
                return new ActionBar_1_18_R2();
            case "v1_19_R1":
                return new ActionBar_1_19_R1();
            default:
                Bukkit.getLogger().severe("Failed to grab Action Bar! Could not determine version!");
                return new ActionBar_1_8_R1();
        }
    }

    void sendToAll(String text);
}

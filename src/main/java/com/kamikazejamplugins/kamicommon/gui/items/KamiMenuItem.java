package com.kamikazejamplugins.kamicommon.gui.items;

import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClick;
import com.kamikazejamplugins.kamicommon.gui.page.PageItem;
import com.kamikazejamplugins.kamicommon.item.IAItemBuilder;
import com.kamikazejamplugins.kamicommon.item.IBuilder;
import com.kamikazejamplugins.kamicommon.yaml.ConfigurationSection;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class KamiMenuItem extends PageItem {

    private boolean enabled;
    private List<Integer> slots;

    // For like KamiMenuItem(config, "menus.menu1")
    public KamiMenuItem(ConfigurationSection section, String key) {
        this(section.getConfigurationSection(key));
    }
    public KamiMenuItem(ConfigurationSection section, String key, OfflinePlayer player) {
        this(section.getConfigurationSection(key));
    }


    // For calls like KamiMenuItem(config.getConfigurationSection("menus.menu1"))
    public KamiMenuItem(ConfigurationSection section) {
        this(section, (OfflinePlayer) null);
    }

    /**
     * @param player Only required for PLAYER_HEAD ItemStacks, player is used as the skullOwner
     */
    public KamiMenuItem(ConfigurationSection section, @Nullable OfflinePlayer player) {
        super(new IAItemBuilder(section, player), (MenuClick) null); // Null safe for player arg
        enabled = section.getBoolean("enabled", true);

        slots = new ArrayList<>();
        if (section.isInt("slot")) {
            slots.add(section.getInt("slot"));
        }else if (section.isList("slot")) {
            slots.addAll(section.getIntegerList("slot"));
        }else if (section.isList("slots")) {
            slots.addAll(section.getIntegerList("slots"));
        }else if (section.isInt("slots")) {
            slots.add(section.getInt("slots"));
        }
    }

    public KamiMenuItem(boolean enabled, IBuilder builder, List<Integer> slots) {
        super(builder, (MenuClick) null);
        this.enabled = enabled;
        this.slots = slots;
    }

    public KamiMenuItem(boolean enabled, IBuilder builder, int slot) {
        super(builder, (MenuClick) null);
        this.enabled = enabled;
        this.slots = new ArrayList<>();
        this.slots.add(slot);
    }

    public KamiMenuItem setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public KamiMenuItem setIBuilder(@NotNull IBuilder iBuilder) {
        this.iBuilder = iBuilder;
        return this;
    }

    public KamiMenuItem setSlot(int slot) {
        this.slots = new ArrayList<>();
        this.slots.add(slot);
        return this;
    }

    public KamiMenuItem setSlots(List<Integer> slots) {
        this.slots = slots;
        return this;
    }


    @Override
    public KamiMenuItem setMenuClick(MenuClick menuClick) {
        this.menuClick = menuClick;
        return this;
    }
}



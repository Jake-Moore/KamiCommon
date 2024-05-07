package com.kamikazejam.kamicommon.gui.items;

import com.kamikazejam.kamicommon.gui.interfaces.MenuClick;
import com.kamikazejam.kamicommon.gui.page.PageItem;
import com.kamikazejam.kamicommon.item.IAItemBuilder;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class KamiMenuItem extends PageItem {

    private boolean enabled;
    private List<Integer> slots;
    private final @NotNull List<XMaterial> materialCycle = new ArrayList<>();

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
        super((MenuClick) null); // Null safe for player arg
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

        // Load the IBuilders
        loadIBuilders(section, player);

        // Apply additional settings
        if (section.isSet("materialCycleTicks")) {
            this.setLoopTicks(section.getInt("materialCycleTicks"));
        }
        boolean hideAttributes = section.getBoolean("hideAttributes", true);
        if (hideAttributes) { iBuilders.forEach(IBuilder::hideAttributes); }
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
        super.setIBuilder(iBuilder);
        return this;
    }

    @Override
    public KamiMenuItem addIBuilder(IBuilder iBuilder) {
        super.addIBuilder(iBuilder);
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

    private void loadIBuilders(ConfigurationSection section, @Nullable OfflinePlayer player) {
        // Method1: Try to Load multiple materials/types
        boolean m = section.isList("materials");
        boolean t = section.isList("types");
        if (m || t) {
            List<String> mats = (m) ? section.getStringList("materials") : section.getStringList("types");
            mats.forEach(mat -> iBuilders.add(new IAItemBuilder(mat, section, player)));
            return;
        }

        // Method2: Default to single item logic
        this.iBuilders.add(new IAItemBuilder(section, player));
    }
}



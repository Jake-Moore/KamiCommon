package com.kamikazejam.kamicommon.gui.items;

import com.kamikazejam.kamicommon.gui.interfaces.MenuClick;
import com.kamikazejam.kamicommon.gui.items.slots.ItemSlot;
import com.kamikazejam.kamicommon.gui.items.slots.LastRowItemSlot;
import com.kamikazejam.kamicommon.gui.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.gui.page.PageBuilder;
import com.kamikazejam.kamicommon.gui.page.PageItem;
import com.kamikazejam.kamicommon.item.IAItemBuilder;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.AccessLevel;
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
    @Getter(AccessLevel.NONE) private @Nullable ItemSlot slots = null;

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

        loadSlots(section);

        // Load the IBuilders
        loadIBuilders(section, player);

        // Apply additional settings
        if (section.isSet("typeCycleTicks")) {
            this.setLoopTicks(section.getInt("typeCycleTicks"));
        }
        boolean hideAttributes = section.getBoolean("hideAttributes", true);
        if (hideAttributes) { iBuilders.forEach(IBuilder::hideAttributes); }
    }

    public KamiMenuItem(boolean enabled, IBuilder builder, List<Integer> slots) {
        super(builder, (MenuClick) null);
        this.enabled = enabled;
        this.slots = new StaticItemSlot(slots);
    }

    public KamiMenuItem(boolean enabled, IBuilder builder, int slot) {
        super(builder, (MenuClick) null);
        this.enabled = enabled;
        this.slots = new StaticItemSlot(slot);
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
        this.slots = new StaticItemSlot(slot);
        return this;
    }

    public KamiMenuItem setSlots(List<Integer> slots) {
        this.slots = new StaticItemSlot(slots);
        return this;
    }

    @Override
    public KamiMenuItem setMenuClick(MenuClick menuClick) {
        this.menuClick = menuClick;
        return this;
    }

    private void loadIBuilders(ConfigurationSection section, @Nullable OfflinePlayer player) {
        // Method1: Try to Load multiple materials/types (from any of the 4 allowed keys)
        boolean m1 = section.isList("materials");
        boolean m2 = section.isList("material");
        if (m1 || m2) {
            List<String> mats = (m1) ? section.getStringList("materials") : section.getStringList("material");
            mats.forEach(mat -> iBuilders.add(new IAItemBuilder(mat, section, player)));
            return;
        }
        boolean t1 = section.isList("types");
        boolean t2 = section.isList("type");
        if (t1 || t2) {
            List<String> mats = (t1) ? section.getStringList("types") : section.getStringList("type");
            mats.forEach(mat -> iBuilders.add(new IAItemBuilder(mat, section, player)));
            return;
        }

        // Method2: Default to single item logic
        this.iBuilders.add(new IAItemBuilder(section, player));
    }

    private void loadSlots(ConfigurationSection section) {
        this.slots = null;
        // Load Static Slots (if found)
        List<Integer> staticSlots = new ArrayList<>();
        if (section.isInt("slot")) {
            staticSlots.add(section.getInt("slot"));
        }else if (section.isList("slot")) {
            staticSlots.addAll(section.getIntegerList("slot"));
        }else if (section.isList("slots")) {
            staticSlots.addAll(section.getIntegerList("slots"));
        }else if (section.isInt("slots")) {
            staticSlots.add(section.getInt("slots"));
        }
        if (!staticSlots.isEmpty()) {
            this.slots = new StaticItemSlot(staticSlots);
            return;
        }

        // Try to load a relative slot
        if (section.isInt("slotInLastRow")) {
            this.slots = new LastRowItemSlot(section.getInt("slotInLastRow"));
        }
    }

    @Override
    public List<Integer> getSlots(PageBuilder<?> pageBuilder) {
        if (slots != null) {
            return slots.get(pageBuilder);
        }
        return List.of();
    }
}



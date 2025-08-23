package com.kamikazejam.kamicommon.menu.api.struct.icons;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.PrioritizedMenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.StaticIconSlot;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class PrioritizedMenuIconMap<M extends Menu<M>> {
    private final Map<String, PrioritizedMenuIcon<M>> menuIcons = new ConcurrentHashMap<>();
    private final AtomicInteger priorityCounter = new AtomicInteger(0);

    public PrioritizedMenuIconMap() {}

    public PrioritizedMenuIconMap(@NotNull Map<String, PrioritizedMenuIcon<M>> menuIcons, int priorityCounter) {
        this.menuIcons.putAll(menuIcons);
        this.priorityCounter.set(priorityCounter);
    }

    @NotNull
    public Map<String, MenuIcon<M>> getMenuIcons() {
        return menuIcons.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getIcon()));
    }

    public void add(@NotNull PrioritizedMenuIcon<M> icon) {
        this.add(icon.getIcon(), icon.getSlot(), icon.getPriority());
    }

    public void add(@NotNull MenuIcon<M> icon, @Nullable IconSlot slot) {
        this.add(icon, slot, this.priorityCounter.incrementAndGet());
    }

    public void add(@NotNull MenuIcon<M> icon, @Nullable IconSlot slot, int priority) {
        this.menuIcons.put(icon.getId(), new PrioritizedMenuIcon<>(icon, slot, priority));
    }

    @Nullable
    public MenuIcon<M> remove(@NotNull String id) {
        return Optional.ofNullable(this.menuIcons.remove(id)).map(PrioritizedMenuIcon::getIcon).orElse(null);
    }

    @NotNull
    public Set<MenuIcon<M>> remove(int slot, @NotNull MenuSize size) {
        Set<MenuIcon<M>> removed = new HashSet<>();
        Map<String, PrioritizedMenuIcon<M>> insertions = new HashMap<>();

        for (Map.Entry<String, PrioritizedMenuIcon<M>> entry : this.menuIcons.entrySet()) {
            @Nullable IconSlot iconSlot = entry.getValue().getSlot();
            @Nullable Set<Integer> slots = iconSlot != null ? iconSlot.get(size) : null;
            if (slots == null || !slots.contains(slot)) continue;

            // Indicate this MenuIcon<M> had a slot removed
            removed.add(entry.getValue().getIcon());

            // Remove JUST this slot from the icon
            Set<Integer> newSlots = slots.stream().filter(s -> s != slot).collect(Collectors.toSet());
            PrioritizedMenuIcon<M> newIcon = entry.getValue().copy(new StaticIconSlot(newSlots));
            insertions.put(entry.getKey(), newIcon);
        }

        this.menuIcons.putAll(insertions);
        return removed;
    }

    public void clear() {
        this.menuIcons.clear();
        this.priorityCounter.set(0);
    }

    @NotNull
    public Optional<MenuIcon<M>> get(@NotNull String id) {
        return Optional.ofNullable(this.menuIcons.get(id)).map(PrioritizedMenuIcon::getIcon);
    }

    public boolean contains(@NotNull String id) {
        return this.menuIcons.containsKey(id);
    }

    @NotNull
    public Set<String> keySet() {
        return this.menuIcons.keySet();
    }

    @Contract("_, !null -> !null")
    public @Nullable MenuIcon<M> getOrDefault(@NotNull String id, MenuIcon<M> def) {
        return this.get(id).orElse(def);
    }

    // forEach
    public void forEach(@NotNull BiConsumer<String, MenuIcon<M>> action) {
        this.menuIcons.forEach((id, val) -> action.accept(id, val.getIcon()));
    }

    @NotNull
    public Collection<PrioritizedMenuIcon<M>> values() {
        return this.menuIcons.values();
    }

    @Nullable
    public MenuIcon<M> getActiveIconForSlot(@NotNull MenuSize size, int slot) {
        // Gather all MenuIcon<M> that want this slot
        Set<PrioritizedMenuIcon<M>> icons = getActiveIconsForSlot(size, slot);

        // Find the highest priority icon
        return icons.stream()
                .max(Comparator.comparingInt(PrioritizedMenuIcon::getPriority))
                .map(PrioritizedMenuIcon::getIcon)
                .orElse(null);
    }

    public boolean containsActiveIconForSlot(@NotNull MenuSize size, int slot) {
        return !getActiveIconsForSlot(size, slot).isEmpty();
    }

    @NotNull
    private Set<PrioritizedMenuIcon<M>> getActiveIconsForSlot(@NotNull MenuSize size, int slot) {
        return this.menuIcons.values().stream()
                .filter(icon -> icon.getSlot() != null && icon.getIcon().isEnabled())
                .filter(icon -> icon.getSlot().get(size).contains(slot))
                .collect(Collectors.toSet());
    }

    @NotNull
    public List<MenuIcon<M>> getAllByAscendingPriority(boolean filterEnabled) {
        return this.menuIcons.values().stream()
                .filter(icon -> !filterEnabled || icon.getIcon().isEnabled())
                .sorted(Comparator.comparingInt(PrioritizedMenuIcon::getPriority))
                .map(PrioritizedMenuIcon::getIcon)
                .collect(Collectors.toList());
    }

    public int size() {
        return this.menuIcons.size();
    }

    public @NotNull PrioritizedMenuIconMap<M> copy() {
        Map<String, PrioritizedMenuIcon<M>> menuIcons = new HashMap<>();
        for (Map.Entry<String, PrioritizedMenuIcon<M>> entry : this.menuIcons.entrySet()) {
            menuIcons.put(entry.getKey(), entry.getValue().copy());
        }
        return new PrioritizedMenuIconMap<>(new HashMap<>(menuIcons), this.priorityCounter.get());
    }
}

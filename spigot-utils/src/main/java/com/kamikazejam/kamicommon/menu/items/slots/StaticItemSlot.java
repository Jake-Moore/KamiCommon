package com.kamikazejam.kamicommon.menu.items.slots;

import com.kamikazejam.kamicommon.menu.Menu;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class StaticItemSlot implements ItemSlot {
    private final @NotNull Set<Integer> slots;
    public StaticItemSlot(@NotNull List<Integer> slots) {
        this.slots = new HashSet<>(slots);
    }
    public StaticItemSlot(int slot) {
        this.slots = Set.of(slot);
    }
    public StaticItemSlot(@NotNull Integer... slots) {
        this(Arrays.asList(slots));
    }

    @Override
    public Set<Integer> get(@NotNull Menu menu) {
        return slots;
    }

    @Override
    public @NotNull ItemSlot copy() {
        return new StaticItemSlot(new ArrayList<>(slots));
    }
}

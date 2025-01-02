package com.kamikazejam.kamicommon.menu.api.icons.slots;

import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
@SuppressWarnings("unused")
public class StaticIconSlot implements IconSlot {
    private final @NotNull Set<Integer> slots;
    public StaticIconSlot(@NotNull List<Integer> slots) {
        this.slots = new HashSet<>(slots);
    }
    public StaticIconSlot(int slot) {
        this.slots = Set.of(slot);
    }
    public StaticIconSlot(@NotNull Integer... slots) {
        this(Arrays.asList(slots));
    }

    @Override
    public Set<Integer> get(@NotNull MenuSize size) {
        return slots;
    }

    @Override
    public @NotNull IconSlot copy() {
        return new StaticIconSlot(new ArrayList<>(slots));
    }
}

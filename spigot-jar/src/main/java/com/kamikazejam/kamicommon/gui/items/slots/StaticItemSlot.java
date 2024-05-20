package com.kamikazejam.kamicommon.gui.items.slots;

import com.kamikazejam.kamicommon.gui.page.PageBuilder;

import java.util.List;

public class StaticItemSlot implements ItemSlot {
    private final List<Integer> slots;
    public StaticItemSlot(List<Integer> slots) {
        this.slots = slots;
    }
    public StaticItemSlot(Integer slot) {
        this.slots = List.of(slot);
    }

    @Override
    public List<Integer> get(PageBuilder<?> builder) {
        return slots;
    }
}

package com.kamikazejam.kamicommon.menu.api.struct;

import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
public class SlotData {
    // We hold onto the original reference to the IconSlot, so that we may use it to resize the menu later
    // and recalculate specific slot types that are relative to the menu size.
    private final @NotNull IconSlot slot;
    private final @NotNull String id;

    // Constructor
    public SlotData(@NotNull IconSlot slot, @NotNull String id) {
        this.slot = slot;
        this.id = id;
    }
    // Copy Constructor
    public SlotData(@NotNull SlotData other) {
        this.slot = other.slot.copy();
        this.id = other.id;
    }

    @NotNull
    public SlotData copy() {
        return new SlotData(this);
    }
}

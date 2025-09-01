package com.kamikazejam.kamicommon.item.patch;

import lombok.Getter;

public final class PatchAdd<T> implements Patch<T> {
    @Getter
    private final T value;
    public PatchAdd(T value) {
        this.value = value;
    }
}

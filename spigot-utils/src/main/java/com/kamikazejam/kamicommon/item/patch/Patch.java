package com.kamikazejam.kamicommon.item.patch;

@SuppressWarnings("unused")
public sealed interface Patch<T> permits PatchAdd, PatchRemove {
    // nothing here for now
}

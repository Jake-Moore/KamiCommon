package com.kamikazejam.kamicommon.nms.entity;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

// 1.14 added Bukkit api support for fetching the size
@SuppressWarnings("unused")
public abstract class EntityMethodsPost_1_14 extends EntityMethodsPost_1_13 {
    @Override
    public double getEntityHeight(@NotNull Entity entity) {
        return entity.getHeight();
    }

    @Override
    public double getEntityWidth(@NotNull Entity entity) {
        return entity.getWidth();
    }
}

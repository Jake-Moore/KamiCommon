package com.kamikazejam.kamicommon.nms.abstraction.entity;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class AbstractEntityMethods {
    public abstract double getEntityHeight(@NotNull Entity entity);
    public abstract double getEntityWidth(@NotNull Entity entity);
}

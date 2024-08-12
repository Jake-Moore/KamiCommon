package com.kamikazejam.kamicommon.nms.entity;

import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class EntityMethods_1_14_R1 extends AbstractEntityMethods {
    @Override
    public double getEntityHeight(@NotNull Entity entity) {
        return entity.getHeight();
    }

    @Override
    public double getEntityWidth(@NotNull Entity entity) {
        return entity.getWidth();
    }
}

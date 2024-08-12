package com.kamikazejam.kamicommon.nms.entity;

import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class EntityMethods_1_12_R1 extends AbstractEntityMethods {
    @Override
    public double getEntityHeight(@NotNull Entity entity) {
        net.minecraft.server.v1_12_R1.Entity craft = ((CraftEntity) entity).getHandle();
        // length property is the height. See .getHeadHeight() for justification of this
        return craft.length;
    }

    @Override
    public double getEntityWidth(@NotNull Entity entity) {
        net.minecraft.server.v1_12_R1.Entity craft = ((CraftEntity) entity).getHandle();
        return craft.width;
    }
}

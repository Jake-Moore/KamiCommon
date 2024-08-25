package com.kamikazejam.kamicommon.nms.entity;

import com.kamikazejam.kamicommon.nms.abstraction.entity.EntityMethodsPre_1_13;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class EntityMethods_1_8_R3 extends EntityMethodsPre_1_13 {
    @Override
    public double getEntityHeight(@NotNull Entity entity) {
        net.minecraft.server.v1_8_R3.Entity craft = ((CraftEntity) entity).getHandle();
        // length property is the height. See .getHeadHeight() for justification of this
        return craft.length;
    }

    @Override
    public double getEntityWidth(@NotNull Entity entity) {
        net.minecraft.server.v1_8_R3.Entity craft = ((CraftEntity) entity).getHandle();
        return craft.width;
    }

    @Override
    public void setPersists(@NotNull Creature creature, boolean value) {
        ((CraftCreature) creature).getHandle().persistent = value;
    }

    @Override
    public void setFromSpawner(@NotNull Entity entity, boolean value) {
        ((CraftEntity) entity).getHandle().fromMobSpawner = value;
    }

    @Override
    public void setMobAI(@NotNull Entity entity, boolean value) {
        this.setFromSpawner(entity, !value);
    }
}

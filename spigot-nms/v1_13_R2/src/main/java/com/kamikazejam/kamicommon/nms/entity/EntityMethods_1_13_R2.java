package com.kamikazejam.kamicommon.nms.entity;

import org.bukkit.craftbukkit.v1_13_R2.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class EntityMethods_1_13_R2 extends EntityMethodsPost_1_13 {
    @Override
    public double getEntityHeight(@NotNull Entity entity) {
        net.minecraft.server.v1_13_R2.Entity craft = ((CraftEntity) entity).getHandle();
        // length property is the height. See .getHeadHeight() for justification of this
        return craft.length;
    }

    @Override
    public double getEntityWidth(@NotNull Entity entity) {
        net.minecraft.server.v1_13_R2.Entity craft = ((CraftEntity) entity).getHandle();
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
        ((CraftLivingEntity) entity).setAI(value);
    }
}

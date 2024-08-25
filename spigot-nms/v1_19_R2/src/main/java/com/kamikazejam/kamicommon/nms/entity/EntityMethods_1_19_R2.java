package com.kamikazejam.kamicommon.nms.entity;

import net.minecraft.world.entity.Mob;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class EntityMethods_1_19_R2 extends EntityMethodsPost_1_14 {

    @Override
    public void setPersists(@NotNull Creature creature, boolean value) {
        ((CraftCreature) creature).getHandle().persist = value;
    }

    @Override
    public void setFromSpawner(@NotNull Entity entity, boolean value) {
        ((CraftEntity) entity).getHandle().spawnedViaMobSpawner = value;
    }

    @Override
    public void setMobAI(@NotNull Entity entity, boolean value) {
        if (((CraftLivingEntity) entity).getHandle() instanceof Mob mob) {
            mob.aware = value;
        }
        ((CraftLivingEntity) entity).setAI(value);
    }
}

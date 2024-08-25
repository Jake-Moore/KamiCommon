package com.kamikazejam.kamicommon.nms.entity;

import net.minecraft.server.v1_16_R2.EntityInsentient;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class EntityMethods_1_16_R2 extends EntityMethodsPost_1_14 {

    @Override
    public void setPersists(@NotNull Creature creature, boolean value) {
        ((CraftCreature) creature).getHandle().persistent = value;
    }

    @Override
    public void setFromSpawner(@NotNull Entity entity, boolean value) {
        this.setMobAI(entity, !value);
    }

    @Override
    public void setMobAI(@NotNull Entity entity, boolean value) {
        if (((CraftLivingEntity) entity).getHandle() instanceof EntityInsentient insentient) {
            insentient.aware = value;
        }
        ((CraftLivingEntity) entity).setAI(value);
    }
}

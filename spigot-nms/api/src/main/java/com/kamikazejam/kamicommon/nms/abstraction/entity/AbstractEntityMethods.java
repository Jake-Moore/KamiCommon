package com.kamikazejam.kamicommon.nms.abstraction.entity;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("unused")
public abstract class AbstractEntityMethods {
    public abstract double getEntityHeight(@NotNull Entity entity);
    public abstract double getEntityWidth(@NotNull Entity entity);
    public abstract void setPersists(@NotNull Creature creature, boolean value);
    public abstract void setFromSpawner(@NotNull Entity entity, boolean value);
    public abstract void setMobAI(@NotNull Entity entity, boolean value);
    public abstract @NotNull Optional<EntityType> getSpawnerType(@Nullable ItemStack stack);
    public abstract @NotNull ItemStack setSpawnerType(@NotNull ItemStack stack, @NotNull EntityType type);

}

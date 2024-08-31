package com.kamikazejam.kamicommon.nms.entity;

import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class EntityMethodsPost_1_13 extends AbstractEntityMethods {
    @Override
    public @NotNull ItemStack setSpawnerType(@NotNull ItemStack stack, @NotNull EntityType type) {
        if (stack.getType() != XMaterial.SPAWNER.parseMaterial() || !stack.hasItemMeta()) {
            return stack;
        }

        BlockStateMeta meta = (BlockStateMeta) stack.getItemMeta();
        BlockState state = meta.getBlockState();
        ((CreatureSpawner) state).setSpawnedType(type);
        meta.setBlockState(state);
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public @NotNull Optional<EntityType> getSpawnerType(@Nullable ItemStack stack) {
        if (stack == null || stack.getType() != XMaterial.SPAWNER.parseMaterial() || !stack.hasItemMeta()) {
            return Optional.empty();
        }
        BlockStateMeta meta = (BlockStateMeta)stack.getItemMeta();
        BlockState state = meta.getBlockState();
        return Optional.ofNullable(((CreatureSpawner) state).getSpawnedType());
    }
}

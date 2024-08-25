package com.kamikazejam.kamicommon.nms.abstraction.entity;

import com.cryptomorin.xseries.XEntityType;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("unused")
public abstract class EntityMethodsPre_1_13 extends AbstractEntityMethods {
    @Override
    public @NotNull Optional<EntityType> getSpawnerType(@Nullable ItemStack stack) {
        return this.getSpawnerTypeByNBT(stack);
    }

    @Override
    public @NotNull ItemStack setSpawnerType(@NotNull ItemStack stack, @NotNull EntityType type) {
        return this.setSpawnerTypeByNBT(stack, type);
    }

    @NotNull
    protected Optional<EntityType> getSpawnerTypeByNBT(@Nullable ItemStack stack) {
        // Handle invalid stacks
        if (stack == null || !stack.hasItemMeta()) { return Optional.empty(); }
        // Or stacks that aren't a spawner
        XMaterial xMaterial = XMaterial.matchXMaterial(stack);
        if (xMaterial != XMaterial.SPAWNER) { return Optional.empty(); }

        ReadableNBT nbt = NBT.readNbt(stack);
        ReadableNBT nbtCompound = nbt.getCompound("BlockEntityTag");
        if (nbtCompound == null) {
            if (nbt.hasTag("type")) {
                return Optional.ofNullable(translateNMSTypes(nbt.getString("type").toUpperCase()));
            }
            return Optional.empty();
        }

        if (nbtCompound.getString("EntityId") == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(translateNMSTypes(nbtCompound.getString("EntityId").toUpperCase()));
    }

    @Nullable
    @SuppressWarnings("SpellCheckingInspection")
    private EntityType translateNMSTypes(@Nullable String nmsType) {
        if (nmsType == null) { return null; }

        EntityType type = switch (nmsType.toLowerCase()) {
            case "pigzombie" -> EntityType.PIG_ZOMBIE;
            case "complexpart" -> EntityType.COMPLEX_PART;
            case "cavespider" -> EntityType.CAVE_SPIDER;
            case "irongolem", "villagergolem" -> EntityType.IRON_GOLEM;
            case "magmacube", "lavaslime" -> EntityType.MAGMA_CUBE;
            case "mushroomcow" -> EntityType.MUSHROOM_COW;
            case "ozelot" -> EntityType.OCELOT;
            case "entityhorse" -> EntityType.HORSE;
            default -> {
                try {
                    yield EntityType.valueOf(nmsType.toUpperCase());
                } catch (final IllegalArgumentException exc) {
                    yield null;
                }
            }
        };
        if (type != null) { return type; }

        // Try XEntityType
        Optional<XEntityType> o = XEntityType.of(nmsType.toUpperCase());
        return o.map(XEntityType::get).orElse(null);

        // TODO extract more entity types from NMS and add to the switch statement
    }

    @SuppressWarnings("deprecation")
    protected ItemStack setSpawnerTypeByNBT(ItemStack stack, @NotNull EntityType type) {
        if (stack == null || XMaterial.matchXMaterial(stack.getType()) != XMaterial.SPAWNER) { return null; }

        NBT.modify(stack, (nbt) -> {
            ReadWriteNBT compound = nbt.getOrCreateCompound("BlockEntityTag");
            compound.setString("EntityId", type.getName().toLowerCase());
        });
        return stack;
    }
}

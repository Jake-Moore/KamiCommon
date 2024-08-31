package com.kamikazejam.kamicommon.command.internal;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.item.NbtType;
import com.kamikazejam.kamicommon.nbtapi.NBT;
import com.kamikazejam.kamicommon.nbtapi.NBTType;
import com.kamikazejam.kamicommon.nbtapi.iface.ReadableNBT;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.logging.Logger;

@SuppressWarnings("SpellCheckingInspection")
public class CmdItemDump extends KamiCommand {

    public CmdItemDump() {
        addAliases("itemdump", "dump");

        addRequirements(RequirementHasPerm.get("kamicommon.command.itemdump"));
        addRequirements(RequirementIsPlayer.get());

        setDesc("Dumps NBT data for the item in your hand.");
    }

    @Override
    public void perform() throws KamiCommonException {
        Logger logger = PluginSource.get().getLogger();
        Player plr = (Player) sender;

        ItemStack item = NmsAPI.getItemInMainHand(plr);
        if (item == null || item.getType() == Material.AIR) {
            plr.sendMessage(StringUtil.t("&cYou must be holding an item to dump it's NBT data!"));
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) { plr.sendMessage(StringUtil.t("&cThis item has no meta!")); return; }

        plr.sendMessage(StringUtil.t("&aDumping data to console for current item in hand..."));
        if (meta.hasDisplayName()) {
            logger.info("Name: " + item.getItemMeta().getDisplayName());
        }
        if (item.getItemMeta().hasLore() && meta.getLore() != null) {
            logger.info("Lore: " + meta.getLore().toString());
        }

        logger.info("Type: " + item.getType());
        logger.info("Enchants: " + item.getItemMeta().getEnchants());
        logger.info("Durability: " + item.getDurability());
        logger.info("Max Durability: " + item.getType().getMaxDurability());

        ReadableNBT nbtRead = NBT.readNbt(item);
        logger.info("NBT Keys: " + Arrays.toString(nbtRead.getKeys().toArray()));

        for (String key : nbtRead.getKeys()) {
            NBTType nbtType = nbtRead.getType(key);
            NbtType type = NbtType.matchNBTAPI(nbtType);
            if (type == null) {
                logger.info("  " + key + " - UNKNOWN: " + nbtType.name());
                continue;
            }
            Object o = type.read(nbtRead, key);
            logger.info("  " + key + "(" + nbtType.name() + ":" + type.name() + "): " + o.getClass().getName());
            logger.info("    " + o);
        }
    }
}

package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.util.Txt;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class RequirementHasItemInHand extends RequirementAbstract {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final RequirementHasItemInHand i = new RequirementHasItemInHand();
	public static RequirementHasItemInHand get() { return i; }

	@Contract("_ -> new")
	public static @NotNull RequirementHasItemInHand get(@NotNull Material... materialWhitelist) {
		return new RequirementHasItemInHand(materialWhitelist);
	}

	private final @NotNull List<Material> materialWhitelist;
	public RequirementHasItemInHand(@NotNull Material... materialWhitelist) {
		this.materialWhitelist = List.of(materialWhitelist);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(CommandSender sender, KamiCommand command) {
		if (!(sender instanceof Player player)) return false;
		ItemStack inHand = NmsAPI.getItemInMainHand(player);
		if (this.materialWhitelist.isEmpty()) {
			// If no whitelist is set, any item in hand is fine.
			return inHand != null;
		}
        return inHand != null && inHand.getType() != Material.AIR && this.materialWhitelist.contains(inHand.getType());
	}

	@Override
	public String createErrorMessage(CommandSender sender, KamiCommand command) {
		if (!(sender instanceof Player player)) {
			return RequirementIsPlayer.get().createErrorMessage(sender, command);
		}
		ItemStack inHand = NmsAPI.getItemInMainHand(player);
		if (inHand == null) {
			return KamiCommand.Config.getErrorColor() + "You must be holding an item in your hand.";
		}
		return KamiCommand.Config.getErrorColor() + "Invalid Item: " + Txt.getNicedEnum(inHand.getType());
	}

}

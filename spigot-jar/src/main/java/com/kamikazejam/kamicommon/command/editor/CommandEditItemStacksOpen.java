package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.command.requirement.RequirementIsPlayer;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CommandEditItemStacksOpen<O> extends CommandEditItemStacksAbstract<O> implements Listener {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditItemStacksOpen(@NotNull EditSettings<O> settings, @NotNull Property<O, List<ItemStack>> property) {
		// Super	
		super(settings, property);

		// Requirements
		this.addRequirements(RequirementIsPlayer.get());

		// Listener
		Bukkit.getPluginManager().registerEvents(this, KamiCommon.get());
	}

	// -------------------------------------------- //
	// EDITING
	// -------------------------------------------- //

	protected Set<UUID> playerIds = new KamiSet<>();

	public void setEditing(@NotNull Player player, boolean editing) {
		UUID playerId = player.getUniqueId();

		if (editing) {
			this.playerIds.add(playerId);
		} else {
			this.playerIds.remove(playerId);
		}
	}

	public boolean isEditing(@NotNull Player player) {
		UUID playerId = player.getUniqueId();

		return this.playerIds.contains(playerId);
	}

	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //

	// Not Cancellable
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClose(@NotNull InventoryCloseEvent event) {
		// If a player closes an inventory ...
		if (KUtil.isntPlayer(event.getPlayer())) return;
		Player player = (Player) event.getPlayer();

		// ... and that player is editing ...
		if (!this.isEditing(player)) return;

		// ... set the player as not editing ...
		this.setEditing(player, false);

		// ... load the item stacks into a list ...
		List<ItemStack> after = asList(event.getInventory());

		// ... attempt set.
		this.senderFieldsOuter(player);
		this.attemptSet(after);
		this.senderFieldsOuter(null);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform() throws KamiCommonException {
		// Get Before
		List<ItemStack> before = this.getProperty().getRaw(this.getObject());

		// Open Chest
		Inventory chest = asChest(before);
		this.setEditing(me, true);
		me.openInventory(chest);
	}

	// -------------------------------------------- //
	// CONVERT LIST <--> CHEST
	// -------------------------------------------- //

	public Inventory asChest(List<ItemStack> itemStacks) {
		// Dodge Null
		if (itemStacks == null) return null;

		// Create Ret
		Inventory ret = Bukkit.createInventory(me, 54, this.getProperty().getName());

		// Fill Ret
		for (int i = 0; i < itemStacks.size(); i++) {
			ItemStack itemStack = itemStacks.get(i);
			if (KUtil.isNothing(itemStack)) continue;
			itemStack = new ItemStack(itemStack);

			ret.setItem(i, itemStack);
		}

		// Return Ret
		return ret;
	}

	public List<ItemStack> asList(Inventory inventory) {
		// Dodge Null
		if (inventory == null) return null;

		// Create Ret
		List<ItemStack> ret = new KamiList<>();

		// Fill Ret
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack itemStack = inventory.getItem(i);
			if (KUtil.isNothing(itemStack)) continue;
			itemStack = new ItemStack(itemStack);

			ret.add(itemStack);
		}

		// Return Ret
		return ret;
	}

}

package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
public class RequirementHasPerm extends RequirementAbstract {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	public static @NotNull RequirementHasPerm get(String permission) {
		return new RequirementHasPerm(permission);
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public RequirementHasPerm(String permission) {
		this.permissionId = permission;
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final String permissionId;

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(@NotNull CommandSender sender, KamiCommand command) {
		return sender.hasPermission(this.permissionId);
	}

	@Override
	public String createErrorMessage(CommandSender sender, KamiCommand command) {
		return getPermissionDeniedMessage();
	}

	public static String getPermissionDeniedMessage() {
		return KamiCommand.Lang.getRequirementPermissionDenied();
	}
}

package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Lang;
import com.kamikazejam.kamicommon.util.Txt;
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
		String deniedFormat = Lang.PERM_DEFAULT_DENIED_FORMAT;
		String action = Lang.PERM_DEFAULT_DESCRIPTION;
		return Txt.parse(deniedFormat, action);
	}
}

package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Requirement} that passes when the sender holds a permission node, obtained with
 * {@link #get(String)} and attached with {@link KamiCommand#addRequirements(Requirement...)}. The refusal
 * message is the shared one from {@link KamiCommand.Config} rather than one owned by this class. On a root
 * command with no explicit {@link KamiCommand#setBukkitCommandPermission(String)}, the Bukkit permission is
 * derived from the first instance in the requirement list, so a command carrying several should set it.
 *
 * @see <a href="https://github.com/Jake-Moore/KamiCommon/wiki/v5-KamiCommand#requirements">Requirements (wiki)</a>
 */
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
	public @NotNull VersionedComponent createErrorMessage(CommandSender sender, KamiCommand command) {
		return getPermissionDeniedMessage();
	}

	public static @NotNull VersionedComponent getPermissionDeniedMessage() {
        return NmsAPI.getVersionedComponentSerializer().fromMiniMessage(
                KamiCommand.Config.getRequirementPermissionDeniedMini()
        );
	}
}

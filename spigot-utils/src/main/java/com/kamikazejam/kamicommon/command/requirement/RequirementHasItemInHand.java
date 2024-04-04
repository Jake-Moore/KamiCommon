package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.type.TypeItemStack;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class RequirementHasItemInHand extends RequirementAbstract {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final RequirementHasItemInHand i = new RequirementHasItemInHand(TypeItemStack.get());

	public static RequirementHasItemInHand get() {
		return i;
	}

	@Contract("_ -> new")
	public static @NotNull RequirementHasItemInHand get(TypeItemStack innerType) {
		return new RequirementHasItemInHand(innerType);
	}

	@Contract("_ -> new")
	public static @NotNull RequirementHasItemInHand get(Material... materialWhitelist) {
		return get(TypeItemStack.get(materialWhitelist));
	}

	public RequirementHasItemInHand(@NotNull TypeItemStack innerType) {
		this.innerType = innerType;
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final TypeItemStack innerType;

	public @NotNull TypeItemStack getInnerType() {
		return this.innerType;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(CommandSender sender, KamiCommand command) {
		return this.getInnerType().isValid(null, sender);
	}

	@Override
	public String createErrorMessage(CommandSender sender, KamiCommand command) {
		try {
			this.getInnerType().read(sender);
		} catch (KamiCommonException e) {
			return e.getMessages().toPlain(true);
		}
		return null;
	}

}

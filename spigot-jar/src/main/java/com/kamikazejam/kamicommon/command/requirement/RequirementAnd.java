package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class RequirementAnd extends RequirementAbstract {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	public static @NotNull RequirementAnd get(Requirement @NotNull ... requirements) {
		return new RequirementAnd(requirements);
	}

	public RequirementAnd(Requirement @NotNull ... requirements) {
		this(Arrays.asList(requirements));
	}

	@Contract("_ -> new")
	public static @NotNull RequirementAnd get(Collection<Requirement> requirements) {
		return new RequirementAnd(requirements);
	}

	public RequirementAnd(Collection<Requirement> requirements) {
		this.requirements = new ArrayList<>(requirements);
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final List<Requirement> requirements;

	public @NotNull List<Requirement> getRequirements() {
		return this.requirements;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(CommandSender sender, KamiCommand command) {
		return this.getFirstFailedSubreq(sender, command) == null;
	}

	@Override
	public String createErrorMessage(CommandSender sender, KamiCommand command) {
		return this.getFirstFailedSubreq(sender, command).createErrorMessage(sender, command);
	}

	public Requirement getFirstFailedSubreq(CommandSender sender, KamiCommand command) {
		for (Requirement requirement : this.getRequirements()) {
			if (!requirement.apply(sender, command)) return requirement;
		}
		return null;
	}

}

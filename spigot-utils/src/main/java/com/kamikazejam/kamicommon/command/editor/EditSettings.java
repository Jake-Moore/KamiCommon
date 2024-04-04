package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.command.requirement.Requirement;
import com.kamikazejam.kamicommon.command.type.Type;
import com.kamikazejam.kamicommon.command.type.sender.TypeSender;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class EditSettings<O> {
	// -------------------------------------------- //
	// PARENTS
	// -------------------------------------------- //

	@Getter
	private EditSettings<?> parent = null;

	@Contract(mutates = "this")
	public void setParent(EditSettings<?> parent) {
		this.parent = parent;
	}

	public boolean hasParent() {
		return this.getParent() != null;
	}

	public List<EditSettings<?>> getParents() {
		// Create
		List<EditSettings<?>> ret = new KamiList<>();

		// Fill
		EditSettings<?> parent = this;
		while (parent.hasParent()) {
			parent = parent.getParent();
			ret.add(parent);
		}

		// Return
		return ret;
	}

	public boolean isRoot() {
		return !this.hasParent();
	}

	public EditSettings<?> getRoot() {
		EditSettings<?> ret = this;
		while (ret.hasParent()) {
			ret = ret.getParent();
		}
		return ret;
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	// This is the type of the object we are editing.
	@Getter
	protected final Type<O> objectType;

	// This property is used to get the object we are editing from the sender.
	@Getter
	protected Property<CommandSender, O> usedProperty;

	@Contract(mutates = "this")
	public void setUsedProperty(Property<CommandSender, O> usedProperty) {
		this.usedProperty = usedProperty;
	}

	// The Internal EditSettings<CommandSender> for setting the used.
	protected EditSettings<CommandSender> usedSettings = null;

	public EditSettings<CommandSender> getUsedSettings() {
		if (this.usedSettings == null) {
			this.usedSettings = this.createUsedSettings();
		}
		return this.usedSettings;
	}

	protected EditSettings<CommandSender> createUsedSettings() {
		return new EditSettings<>(TypeSender.get(), new PropertyThis<>(TypeSender.get()));
	}

	// Requirements to edit the used object.
	@Getter
	private List<Requirement> usedRequirements = new KamiList<>();

	@Contract(mutates = "this")
	public void setUsedRequirements(List<Requirement> requirements) {
		this.usedRequirements = requirements;
	}

	@Contract(mutates = "this")
	public void addUsedRequirements(Collection<Requirement> requirements) {
		this.usedRequirements.addAll(requirements);
	}

	@Contract(mutates = "this")
	public void addUsedRequirements(Requirement @NotNull ... requirements) {
		this.addUsedRequirements(Arrays.asList(requirements));
	}

	// Requirements to edit properties. Common stuff shared by all properties.
	@Getter
	private List<Requirement> propertyRequirements = new KamiList<>();

	@Contract(mutates = "this")
	public void setPropertyRequirements(List<Requirement> requirements) {
		this.propertyRequirements = requirements;
	}

	@Contract(mutates = "this")
	public void addPropertyRequirements(Collection<Requirement> requirements) {
		this.propertyRequirements.addAll(requirements);
	}

	@Contract(mutates = "this")
	public void addPropertyRequirements(Requirement @NotNull ... requirements) {
		this.addPropertyRequirements(Arrays.asList(requirements));
	}

	// -------------------------------------------- //
	// CONSTRUCT > NORMAL
	// -------------------------------------------- //

	public EditSettings(Type<O> objectType, Property<CommandSender, O> usedProperty) {
		this.objectType = objectType;
		this.usedProperty = usedProperty;
	}

	public EditSettings(Type<O> objectType) {
		this(objectType, null);
	}

	// -------------------------------------------- //
	// CONSTRUCT > DELEGATE
	// -------------------------------------------- //

	public <P> EditSettings(final EditSettings<P> parentSettings, final @NotNull Property<P, O> childProperty) {
		this(childProperty.getValueType());

		this.setParent(parentSettings);

		PropertyUsed<O> usedProperty = new PropertyUsed<O>(this) {
			@Override
			public O getRaw(CommandSender sender) {
				P parentUsed = parentSettings.getUsed(sender);
				return childProperty.getRaw(parentUsed);
			}

			@Override
			public CommandSender setRaw(CommandSender sender, O used) {
				P parentUsed = parentSettings.getUsed(sender);
				childProperty.setRaw(parentUsed, used);
				return sender;
			}

		};
		this.setUsedProperty(usedProperty);

		this.addUsedRequirements(parentSettings.getPropertyRequirements());
		this.addUsedRequirements(childProperty.getRequirements());
	}

	// -------------------------------------------- //
	// OBJECT
	// -------------------------------------------- //

	public O getUsed(CommandSender sender) {
		return this.getUsedProperty().getValue(sender);
	}

	public void setUsed(CommandSender sender, O used) {
		this.getUsedProperty().setValue(sender, sender, used);
	}

	// -------------------------------------------- //
	// COMMAND CREATION
	// -------------------------------------------- //

	public CommandEditUsed<O> createCommandUsed() {
		return new CommandEditUsed<>(this);
	}

	public CommandEditShow<O, O> createCommandShow() {
		return new CommandEditShow<>(this, new PropertyThis<>(this.getObjectType()));
	}

	public CommandEditAbstract<O, O> createCommandEdit() {
		// TODO: Where does command creation belong? Inside the type or the edit settings.
		// TODO: Resolve and research asymmetry between createCommandShow() and createCommandEdit().
		// TODO: Where should the permission nodes and requirements be set?
		CommandEditAbstract<O, O> ret = this.getObjectType().createEditCommand(this, new PropertyThis<>(this.getObjectType()));
		ret.setAliases("edit");
		return ret;
	}

	// -------------------------------------------- //
	// TYPE READ UTILITY
	// -------------------------------------------- //

	public static final Set<String> ALIASES_USED = KUtil.treeset("used", "selected", "chosen");

	public O getUsedOrCommandException(String arg, CommandSender sender) throws KamiCommonException {
		if (arg == null) {
			O ret = this.getUsed(sender);
			if (ret != null) return ret;
			String noun = this.getObjectType().getName();
			String aan = Txt.aan(noun);
			throw new KamiCommonException().addMsg("<b>You must select %s %s for use to skip the optional argument.", aan, noun);
		}
		if (ALIASES_USED.contains(arg)) {
			O ret = this.getUsed(sender);
			if (ret == null)
				throw new KamiCommonException().addMsg("<b>You have no selected %s.", this.getObjectType().getName());
			return ret;
		}

		return null;
	}

}

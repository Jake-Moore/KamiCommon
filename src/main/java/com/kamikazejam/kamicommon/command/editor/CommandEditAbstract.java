package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Visibility;
import com.kamikazejam.kamicommon.command.requirement.RequirementEditorUse;
import com.kamikazejam.kamicommon.command.type.Type;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.mson.Mson;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map.Entry;

public class CommandEditAbstract<O, V> extends KamiCommand {
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	@Getter
	protected final EditSettings<O> settings;

	@Getter
	protected final Property<O, V> property;

	// Null is allowed and means unknown/either/neither.
	protected final Boolean write;

	public Boolean isWrite() {
		return this.write;
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditAbstract(@NotNull EditSettings<O> settings, @NotNull Property<O, V> property, Boolean write) {
		// Fields
		this.settings = settings;
		this.property = property;
		this.write = write;

		// Aliases
		this.setAliases(this.getProperty().getNames());

		// Desc
		String descAction = property.isEditable() ? "edit " : "show ";
		this.setDesc(descAction + this.getProperty().getName());

		// Requirements
		this.addRequirements(RequirementEditorUse.get());
		this.addRequirements(settings.getPropertyRequirements());
		this.addRequirements(property.getRequirements());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public Visibility getVisibility() {
		if (!this.getProperty().isVisible()) return Visibility.INVISIBLE;
		return super.getVisibility();
	}

	@Override
	public void perform() throws KamiCommonException {
		if (this.isParent()) {
			// If there is only one visible child, and it is a show command ....
			// Note: We use the visible children because HelpCommand is always present, but often invisible.
			List<KamiCommand> children = this.getVisibleChildren(this.sender);
			if (children.size() == 1 && children.get(0) instanceof CommandEditShow<?, ?>) {
				CommandEditShow<?, ?> cmd = (CommandEditShow<?, ?>) children.get(0);
				// ... skip directly to it.
				cmd.execute(this.sender, this.args);
			} else {
				super.perform();
			}
		} else {
			msg("<b>Not yet implemented.");
		}
	}

	// -------------------------------------------- //
	// ATTEMPT SET
	// -------------------------------------------- //

	public void attemptSet(V after) {
		// Editable
		if (!this.getProperty().isEditable()) {
			msg("<b>The property <h>%s<b> is not editable.", this.getProperty().getDisplayName());
			return;
		}

		// Inherited / Source / Before
		Entry<O, V> inherited = this.getInheritedEntry();
		O source = inherited.getKey();
		V before = inherited.getValue();

		// NoChange
		// We check, inform and cancel on equality.
		// For this to count the source must be the object itself.
		if (KUtil.equals(this.getObject(), source) && this.getValueType().equals(before, after)) {
			message(this.attemptSetNochangeMessage());
			return;
		}

		this.attemptSetPerform(after);
	}

	protected Mson attemptSetNochangeMessage() {
		return mson(
				this.getProperty().getDisplayNameMson(),
				" for ",
				this.getObjectVisual(),
				" already: ",
				this.getInheritedVisual()
		).color(ChatColor.GRAY);
	}

	protected void attemptSetPerform(V after) {
		String descProperty = this.getProperty().getDisplayName();
		Mson descObject;
		Mson descValue = this.getInheritedVisual();

		// Create messages
		List<Mson> messages = new KamiList<>();

		// Before
		// We inform what the value was before.
		messages.add(mson(
				mson("Before: ").color(ChatColor.AQUA),
				descValue
		));

		// Apply
		// We set the new property value.
		this.setValue(after);

		// After
		// We inform what the value is after.
		descValue = this.getInheritedVisual();
		messages.add(mson(
				mson("After: ").color(ChatColor.AQUA),
				descValue
		));

		// Startup
		// We inform what property and object the edit is taking place on.
		// The visual might change after modification, so this should be added after we have made the change.
		descObject = this.getObjectVisual();
		messages.add(0, mson(
				descProperty,
				" for ",
				descObject,
				" edited:"
		).color(ChatColor.GRAY));

		message(messages);
	}

	// -------------------------------------------- //
	// SHORTCUTS > EDITORSETTINGS
	// -------------------------------------------- //

	public Type<O> getObjectType() {
		return this.getSettings().getObjectType();
	}

	public O getObject(CommandSender sender) {
		return this.getSettings().getUsed(sender);
	}

	public void setObject(CommandSender sender, O object) {
		this.getSettings().setUsed(sender, object);
	}

	public O getObject() {
		return this.getSettings().getUsed(sender);
	}

	public void setObject(O object) {
		this.setObject(sender, object);
	}

	public Mson getObjectVisual() {
		return this.getObjectType().getVisualMson(this.getObject(), sender);
	}

	// -------------------------------------------- //
	// SHORTCUTS > PROPERTY
	// -------------------------------------------- //

	// Type
	public Type<V> getValueType() {
		return this.getProperty().getValueType();
	}

	// Name
	public String getPropertyName() {
		return this.getProperty().getName();
	}

	public List<String> getPropertyNames() {
		return this.getProperty().getNames();
	}

	// Access
	public V getValue() {
		return this.getProperty().getValue(this.getObject());
	}

	public O setValue(V value) {
		O object = this.getProperty().setValue(sender, this.getObject(), value);
		this.setObject(object);
		return object;
	}

	public Entry<O, V> getInheritedEntry() {
		return this.getProperty().getInheritedEntry(this.getObject());
	}

	public O getInheritedObject() {
		return this.getProperty().getInheritedObject(this.getObject());
	}

	public V getInheritedValue() {
		return this.getProperty().getInheritedValue(this.getObject());
	}

	public Mson getInheritedVisual(O source, V value) {
		return this.getProperty().getInheritedVisual(this.getObject(), source, value, sender);
	}

	public Mson getInheritedVisual() {
		return this.getProperty().getInheritedVisual(this.getObject(), sender);
	}

	// -------------------------------------------- //
	// SHORTCUTS > PROPERTY > TYPE
	// -------------------------------------------- //

	// Only to be used with collection type properties.
	public Type<Object> getValueInnerType() {
		return this.getProperty().getValueType().getInnerType();
	}

	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //

	// Simply picks the last word in the class name.
	public String createCommandAlias() {
		// Split at uppercase letters
		final List<String> words = Txt.camelsplit(this.getClass().getSimpleName());
		String alias = words.get(words.size() - 1);
		alias = alias.toLowerCase();
		return alias;
	}

	public void show(int page) {
		List<Mson> show = this.getValueType().getShow(this.getValue(), sender);

		Property<O, V> property = this.getProperty();
		Mson descProperty = property.getDisplayNameMson();
		Mson descObject = this.getObjectVisual();

		Mson title;
		if (property instanceof PropertyThis) {
			title = descObject;
		} else {
			title = mson(
					descProperty,
					" for ",
					descObject
			);
		}

		// For things with line breaks.
		if (show.size() > 1) {
			message(Txt.getPage(show, page, title, this));
		}
		// Others
		else {
			message(Mson.prepondfix(
					title.add(mson(":").color(ChatColor.GRAY)),
					show,
					null
			));
		}
	}

	public void requireNullable() throws KamiCommonException {
		if (this.getProperty().isNullable()) return;
		throw new KamiCommonException().addMsg("<h>%s<b> can not be null.", this.getPropertyName());
	}

}

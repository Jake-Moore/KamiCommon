package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.command.requirement.RequirementEditorPropertyCreated;
import com.kamikazejam.kamicommon.command.type.Type;
import com.kamikazejam.kamicommon.command.type.TypeNullable;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.ContainerUtil;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.mson.Mson;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class CommandEditContainerAbstract<O, V> extends CommandEditAbstract<O, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditContainerAbstract(@NotNull EditSettings<O> settings, @NotNull Property<O, V> property) {
		// Super
		super(settings, property, true);

		// Aliases
		String alias = this.createCommandAlias();
		this.setAliases(alias);

		// Desc
		this.setDesc(alias + " " + this.getPropertyName());

		// Requirements
		this.addRequirements(RequirementEditorPropertyCreated.get(true));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform() throws KamiCommonException {
		// Create
		V before = this.getProperty().getRaw(this.getObject());

		// We must use a container of the correct type.
		// Otherwise the elements list for maps,
		// could have two entries with the same key.
		// That obviously caused issues.
		V container = ContainerUtil.getCopy(before);

		// Alter
		try {
			this.alter(container);
		} catch (KamiCommonException e) {
			throw e;
		} catch (Exception e) {
			throw new KamiCommonException().addMsg("<b>%s", e.getMessage());
		}

		// After
		V after = this.getValueType().createNewInstance();
		List<Object> elements = this.getValueType().getContainerElementsOrdered(container);
		ContainerUtil.addElements(after, elements);

		// Apply
		this.attemptSet(after);
	}

	@Override
	public String createCommandAlias() {
		// Split at uppercase letters
		String alias = this.getClass().getSimpleName();
		alias = alias.substring("CommandEditContainer".length());
		alias = Txt.lowerCaseFirst(alias);
		return alias;
	}

	// -------------------------------------------- //
	// ATTEMPT SET
	// -------------------------------------------- //

	@Override
	public Mson attemptSetNochangeMessage() {
		return mson(
				this.getProperty().getDisplayNameMson(),
				" for ",
				this.getObjectVisual(),
				" not changed."
		).color(ChatColor.GRAY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void attemptSetPerform(V after) {
		V before = this.getInheritedValue();
		Mson descProperty = this.getProperty().getDisplayNameMson();

		// Apply
		// We set the new property value.
		this.setValue(after);

		// Create messages
		List<Mson> messages = new KamiList<>();

		messages.add(mson(
				descProperty,
				mson(" for ").color(ChatColor.GRAY),
				this.getObjectVisual(),
				mson(" edited:").color(ChatColor.GRAY)
		));

		// Note: The result of getAdditions is not actually V, but the implementation doesn't care.
		Collection<Object> additions = ContainerUtil.getAdditions(before, after);
		if (!additions.isEmpty()) {
			messages.add(Mson.prepondfix(mson("Additions:").color(ChatColor.AQUA), this.getValueType().getVisualMson((V) additions, sender), null));
		}

		// Note: The result of getDeletions is not actually V, but the implementation doesn't care.
		Collection<Object> deletions = ContainerUtil.getDeletions(before, after);
		if (!deletions.isEmpty()) {
			messages.add(Mson.prepondfix(mson("Deletions:").color(ChatColor.AQUA), this.getValueType().getVisualMson((V) deletions, sender), null));
		}

		message(messages);
	}

	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //
	// Not actually abstract, but one of these must be implemented;

	public void alter(V container) throws KamiCommonException {
		List<Object> elements = toElements(container);

		this.alterElements(elements);

		ContainerUtil.setElements(container, elements);
	}

	public void alterElements(List<Object> elements) throws KamiCommonException {
		throw new KamiCommonException().addMsg("<b>Not yet implemented.");
	}

	// -------------------------------------------- //
	// PARAMETER
	// -------------------------------------------- //

	public boolean isCollection() {
		Type<V> type = this.getValueType();
		if (type.isContainerCollection()) return true;
		if (type.isContainerMap()) return false;
		throw new RuntimeException("Neither Collection nor Map.");
	}

	public void addParametersElement(boolean strict) {
		Type<Object> elementType = this.getValueInnerType();

		if (this.isCollection()) {
			this.addParameter(elementType, true);
		} else {
			List<Integer> userOrder = elementType.getUserOrder();
			for (Iterator<Integer> iterator = userOrder.iterator(); iterator.hasNext(); ) {
				Integer indexTech = iterator.next();
				Type<?> innerType = elementType.getInnerType(indexTech);
				boolean concatFromHere = !iterator.hasNext();
				if (strict) {
					this.addParameter(innerType, concatFromHere);
				} else {
					this.addParameter(null, TypeNullable.get(innerType, "any", "all"), innerType.getName(), "any", concatFromHere);
				}
			}
		}
	}

	public Object readElement() throws KamiCommonException {
		if (this.isCollection()) {
			return this.readArg();
		} else {
			Object key = this.readElementInner(0);
			Object value = this.readElementInner(1);
			return new SimpleImmutableEntry<>(key, value);
		}
	}

	public Object readElementInner(int indexTech) throws KamiCommonException {
		Type<Object> elementType = this.getValueInnerType();
		Integer indexUser = elementType.getIndexUser(indexTech);
		if (indexUser != null) return this.readArgAt(indexUser);
		Type<Object> innerType = elementType.getInnerType(indexTech);
		return innerType.read(sender);
	}

	// -------------------------------------------- //
	// ELEMENTS UTIL
	// -------------------------------------------- //

	public List<Object> toElements(V container) {
		return this.getValueType().getContainerElementsOrdered(container);
	}
}

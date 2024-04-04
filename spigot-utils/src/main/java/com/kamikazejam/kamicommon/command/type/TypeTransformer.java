package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.command.editor.CommandEditAbstract;
import com.kamikazejam.kamicommon.command.editor.EditSettings;
import com.kamikazejam.kamicommon.command.editor.Property;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.mson.Mson;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

// The INNER type controls all ways the type behaves and seems.
// It is used for visuals, names, ids and when reading from a command argument.
// 
// The OUTER type is how the type interfaces in source code.
// It is used for instance creation, editor command creation, and as an optional fallback.
// It should be noted that the OUTER type is kind of optional.
public abstract class TypeTransformer<I, O> extends TypeAbstract<O> {
	// -------------------------------------------- //
	// PREFIX
	// -------------------------------------------- //

	public static final String PREFIX = UNKNOWN + " ";
	public static final String MSON_PREFIX = UNKNOWN + " ";

	@Contract(pure = true)
	public static @NotNull String prefix(String string) {
		return PREFIX + string;
	}

	public static @NotNull Mson prefix(Mson mson) {
		return Mson.mson(MSON_PREFIX, mson);
	}

	public static @NotNull Set<String> prefix(@NotNull Set<String> strings) {
		// Create
		Set<String> ret = new KamiSet<>(strings.size());

		// Fill
		for (String string : strings) {
			ret.add(prefix(string));
		}

		// Return
		return ret;
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	public final Type<I> INNER;

	public Type<I> getInner() {
		return INNER;
	}

	public final Type<O> OUTER;

	public Type<O> getOuter() {
		return OUTER;
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public TypeTransformer(Type<I> typeInner, @NotNull Type<O> typeOuter) {
		super(typeOuter.getClazz());
		this.setInnerTypes(typeInner, typeOuter);
		this.INNER = typeInner;
		this.OUTER = typeOuter;
	}

	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //
	// NOTE: These should return null on null input. 

	public abstract O innerToOuter(I inner, CommandSender sender) throws KamiCommonException;

	public abstract I outerToInner(O outer);

	// -------------------------------------------- //
	// META
	// -------------------------------------------- //

	@Override
	public String getName() {
		return this.getInner().getName();
	}

	// -------------------------------------------- //
	// INNER PROPERTIES
	// -------------------------------------------- //

	@Override
	public <U extends Property<O, ?>> List<U> getInnerProperties() {
		return this.getOuter().getInnerProperties();
	}

	// -------------------------------------------- //
	// WRITE VISUAL COLOR
	// -------------------------------------------- //

	@Override
	public ChatColor getVisualColor(O outer, CommandSender sender) {
		I inner = outerToInner(outer);
		// NOTE: Inner type must NPE evade.
		return this.getInner().getVisualColor(inner, sender);
	}

	@Override
	public ChatColor getVisualColor(O outer) {
		I inner = outerToInner(outer);
		// NOTE: Inner type must NPE evade.
		return this.getInner().getVisualColor(inner);
	}

	@Override
	public void setVisualColor(ChatColor color) {
		this.getInner().setVisualColor(color);
	}

	// -------------------------------------------- //
	// WRITE VISUAL MSON
	// -------------------------------------------- //

	@Override
	public Mson getVisualMsonInner(O outer, CommandSender sender) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) return prefix(this.getOuter().getVisualMsonInner(outer, sender));
		return this.getInner().getVisualMsonInner(inner, sender);
	}

	@Override
	public Mson getVisualMson(O outer, CommandSender sender) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) return prefix(this.getOuter().getVisualMson(outer, sender));
		return this.getInner().getVisualMson(inner, sender);
	}

	@Override
	public Mson getVisualMson(O outer) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) return prefix(this.getOuter().getVisualMson(outer));
		return this.getInner().getVisualMson(inner);
	}

	// -------------------------------------------- //
	// WRITE VISUAL
	// -------------------------------------------- //

	@Override
	public String getVisualInner(O outer, CommandSender sender) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) return prefix(this.getOuter().getVisualInner(outer, sender));
		return this.getInner().getVisualInner(inner, sender);
	}

	@Override
	public String getVisual(O outer, CommandSender sender) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) return prefix(this.getOuter().getVisual(outer, sender));
		return this.getInner().getVisual(inner, sender);
	}

	@Override
	public String getVisual(O outer) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) return prefix(this.getOuter().getVisual(outer));
		return this.getInner().getVisual(inner);
	}

	// -------------------------------------------- //
	// WRITE NAME
	// -------------------------------------------- //

	@Override
	public String getNameInner(O outer) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) throw new NullPointerException("transformer");
		return this.getInner().getNameInner(inner);
	}

	@Override
	public String getName(O outer) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) throw new NullPointerException("transformer");
		return this.getInner().getName(inner);
	}

	@Override
	public Set<String> getNamesInner(O outer) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) throw new NullPointerException("transformer");
		return this.getInner().getNamesInner(inner);
	}

	@Override
	public Set<String> getNames(O outer) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) throw new NullPointerException("transformer");
		return this.getInner().getNames(inner);
	}

	// -------------------------------------------- //
	// WRITE ID
	// -------------------------------------------- //

	@Override
	public String getIdInner(O outer) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) throw new NullPointerException("transformer");
		return this.getInner().getIdInner(inner);
	}

	@Override
	public String getId(O outer) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) throw new NullPointerException("transformer");
		return this.getInner().getId(inner);
	}

	@Override
	public Set<String> getIdsInner(O outer) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) throw new NullPointerException("transformer");
		return this.getInner().getIdsInner(inner);
	}

	@Override
	public Set<String> getIds(O outer) {
		I inner = outerToInner(outer);
		if (outer != null && inner == null) throw new NullPointerException("transformer");
		return this.getInner().getIds(inner);
	}

	// -------------------------------------------- //
	// READ
	// -------------------------------------------- //

	@Override
	public O read(String arg, CommandSender sender) throws KamiCommonException {
		I inner = this.getInner().read(arg, sender);
		return innerToOuter(inner, sender);
	}

	// -------------------------------------------- //
	// VALID
	// -------------------------------------------- //

	@Override
	public boolean isValid(String arg, CommandSender sender) {
		return this.getInner().isValid(arg, sender);
	}

	// -------------------------------------------- //
	// TAB LIST
	// -------------------------------------------- //

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg) {
		return this.getInner().getTabList(sender, arg);
	}

	@Override
	public List<String> getTabListFiltered(CommandSender sender, String arg) {
		return this.getInner().getTabListFiltered(sender, arg);
	}

	@Override
	public boolean allowSpaceAfterTab() {
		return this.getInner().allowSpaceAfterTab();
	}

	// -------------------------------------------- //
	// EDITOR
	// -------------------------------------------- //

	@Override
	public <S> CommandEditAbstract<S, O> createEditCommand(EditSettings<S> settings, Property<S, O> property) {
		return this.getOuter().createEditCommand(settings, property);
	}

	@Override
	public O createNewInstance() {
		return this.getOuter().createNewInstance();
	}

}

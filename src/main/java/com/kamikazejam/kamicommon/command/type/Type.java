package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.command.editor.CommandEditAbstract;
import com.kamikazejam.kamicommon.command.editor.EditSettings;
import com.kamikazejam.kamicommon.command.editor.Property;
import com.kamikazejam.kamicommon.util.interfaces.Named;
import com.kamikazejam.kamicommon.util.mson.Mson;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public interface Type<T> extends Named {
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //

	// Human friendly name
	String getName();

	Class<T> getClazz();

	// -------------------------------------------- //
	// INNER TYPE
	// -------------------------------------------- //

	<I extends Type<?>> List<I> getInnerTypes();

	<I extends Type<?>> I getInnerType(int index);

	<I extends Type<?>> I getInnerType();

	void setInnerTypes(Collection<Type<?>> innerTypes);

	void setInnerTypes(Type<?>... innerTypes);

	void setInnerType(Type<?> innerType);

	void setUserOrder(List<Integer> userOrder);

	void setUserOrder(Integer... userOrder);

	List<Integer> getUserOrder();

	Integer getIndexUser(int indexTechy);

	Integer getIndexTech(int indexUser);

	// -------------------------------------------- //
	// INNER PROPERTY
	// -------------------------------------------- //

	boolean hasInnerProperties();

	<I extends Property<T, ?>> List<I> getInnerProperties();

	<I extends Property<T, ?>> I getInnerProperty(int index);

	<I extends Property<T, ?>> void setInnerProperties(Collection<I> innerTypes);

	@SuppressWarnings("unchecked")
	<I extends Property<T, ?>> void setInnerProperties(I... innerTypes);

	// -------------------------------------------- //
	// WRITE VISUAL COLOR
	// -------------------------------------------- //

	ChatColor getVisualColor(T value, CommandSender sender);

	ChatColor getVisualColor(T value);

	void setVisualColor(ChatColor color);

	// -------------------------------------------- //
	// WRITE SHOW
	// -------------------------------------------- //
	// A list of property values.

	List<Mson> getShowInner(T value, CommandSender sender);

	List<Mson> getShow(T value, CommandSender sender);

	List<Mson> getShow(T value);

	// -------------------------------------------- //
	// WRITE VISUAL MSON
	// -------------------------------------------- //
	// A visual mson.

	Mson getVisualMsonInner(T value, CommandSender sender);

	Mson getVisualMson(T value, CommandSender sender);

	Mson getVisualMson(T value);

	// -------------------------------------------- //
	// WRITE VISUAL
	// -------------------------------------------- //
	// A visual and colorful representation. Possibly with added detail such as simple ASCII art.

	String getVisualInner(T value, CommandSender sender);

	String getVisual(T value, CommandSender sender);

	String getVisual(T value);

	// -------------------------------------------- //
	// WRITE NAME
	// -------------------------------------------- //
	// A human friendly but simple representation without color and clutter.

	String getNameInner(T value);

	String getName(T value);

	Set<String> getNamesInner(T value);

	Set<String> getNames(T value);

	// -------------------------------------------- //
	// WRITE ID
	// -------------------------------------------- //
	// System identification string. Most times unsuitable for humans. 

	String getIdInner(T value);

	String getId(T value);

	Set<String> getIdsInner(T value);

	Set<String> getIds(T value);

	// -------------------------------------------- //
	// READ
	// -------------------------------------------- //

	T read(String arg, CommandSender sender) throws KamiCommonException;

	T read(CommandSender sender) throws KamiCommonException;

	T read(String arg) throws KamiCommonException;

	T read() throws KamiCommonException;

	// -------------------------------------------- //
	// VALID
	// -------------------------------------------- //
	// Used for arbitrary argument order

	boolean isValid(String arg, CommandSender sender);

	// -------------------------------------------- //
	// TAB LIST
	// -------------------------------------------- //

	// The sender is the one that tried to tab complete.
	// The arg is beginning the word they are trying to tab complete.
	Collection<String> getTabList(CommandSender sender, String arg);

	List<String> getTabListFiltered(CommandSender sender, String arg);

	// Sometimes we put a space after a tab completion.
	// That would however not make sense with all Types.
	// Default is true;
	boolean allowSpaceAfterTab();

	// -------------------------------------------- //
	// CONTAINER > IS
	// -------------------------------------------- //

	boolean isContainer();

	boolean isContainerMap();

	boolean isContainerCollection();

	boolean isContainerIndexed();

	boolean isContainerOrdered();

	boolean isContainerSorted();

	// -------------------------------------------- //
	// CONTAINER > COMPARATOR
	// -------------------------------------------- //

	<E> Comparator<E> getContainerComparator();

	void setContainerComparator(Comparator<?> container);

	<E> List<E> getContainerElementsOrdered(Iterable<E> elements);

	<E> List<E> getContainerElementsOrdered(T container);

	// -------------------------------------------- //
	// EQUALS
	// -------------------------------------------- //

	boolean equals(T type1, T type2);

	boolean equalsInner(T type1, T type2);

	// -------------------------------------------- //
	// EDITOR
	// -------------------------------------------- //

	<O> CommandEditAbstract<O, T> createEditCommand(EditSettings<O> settings, Property<O, T> property);

	T createNewInstance();

}

package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.command.requirement.Requirement;
import com.kamikazejam.kamicommon.command.type.Type;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.interfaces.Named;
import com.kamikazejam.kamicommon.util.mson.Mson;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("unused")
@Getter
public abstract class Property<O, V> implements Named {
    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //

    public static final String SHOW_INDENT = "  "; // Two spaces

    // -------------------------------------------- //
    // TYPE
    // -------------------------------------------- //

    protected Type<O> objectType;

    @Contract(mutates = "this")
    public void setObjectType(Type<O> objectType) {
        this.objectType = objectType;
    }

    protected Type<V> valueType;

    @Contract(mutates = "this")
    public void setValueType(Type<V> valueType) {
        this.valueType = valueType;
    }

    // -------------------------------------------- //
    // SETTINGS
    // -------------------------------------------- //

    protected boolean visible = true;

    @Contract(mutates = "this")
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected boolean inheritable = true;

    @Contract(mutates = "this")
    public void setInheritable(boolean inheritable) {
        this.inheritable = inheritable;
    }

    protected boolean editable = true;

    @Contract(mutates = "this")
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    protected boolean nullable = true;

    @Contract(mutates = "this")
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    // -------------------------------------------- //
    // NAME
    // -------------------------------------------- //

    protected List<String> names;

    @Override
    public String getName() {
        return this.getNames().isEmpty() ? null : this.getNames().get(0);
    }

    @Contract(mutates = "this")
    public void setName(String name) {
        this.names = new KamiList<>(name);
    }

    @Contract(mutates = "this")
    public void setNames(String... names) {
        this.names = new KamiList<>(names);
    }

    // -------------------------------------------- //
    // REQUIREMENTS
    // -------------------------------------------- //

    protected List<Requirement> requirements = new ArrayList<>();

    @Contract(mutates = "this")
    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    @Contract(mutates = "this")
    public void addRequirements(Collection<Requirement> requirements) {
        this.requirements.addAll(requirements);
    }

    @Contract(mutates = "this")
    public void addRequirements(Requirement @NotNull ... requirements) {
        this.addRequirements(Arrays.asList(requirements));
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public Property(Type<O> objectType, Type<V> valueType, Collection<String> names) {
        this.objectType = objectType;
        this.valueType = valueType;
        this.names = new KamiList<>(names);
    }

    public Property(Type<O> objectType, Type<V> valueType, String @NotNull ... names) {
        this(objectType, valueType, Arrays.asList(names));
    }

    // -------------------------------------------- //
    // ACCESS
    // -------------------------------------------- //

    public abstract V getRaw(O object);

    public abstract O setRaw(O object, V value);

    public V getValue(O object) {
        return this.getRaw(object);
    }

    public O setValue(CommandSender sender, O object, V value) {
        // Get Before
        V before = this.getRaw(object);

        // NoChange
        if (KUtil.equals(before, value)) return object;

        // Apply
        object = this.setRaw(object, value);

        // On Change
        this.onChange(sender, object, before, value);

        // Return Before
        return object;
    }

    // -------------------------------------------- //
    // ON CHANGE
    // -------------------------------------------- //

    public void onChange(CommandSender sender, O object, V before, V after) {

    }

    // -------------------------------------------- //
    // INHERITED
    // -------------------------------------------- //

    public Entry<O, V> getInheritedEntry(O object) {
        if (object == null) return new SimpleEntry<>(null, null);
        V value = this.getValue(object);
        return new SimpleEntry<>(object, value);
    }

    public O getInheritedObject(O object) {
        return this.getInheritedEntry(object).getKey();
    }

    public V getInheritedValue(O object) {
        return this.getInheritedEntry(object).getValue();
    }

    // -------------------------------------------- //
    // SHORTCUTS
    // -------------------------------------------- //

    public CommandEditAbstract<O, V> createEditCommand(EditSettings<O> settings) {
        return this.getValueType().createEditCommand(settings, this);
    }

    public Mson getInheritedVisual(O object, O source, V value, CommandSender sender) {
        Mson mson = this.getValueType().getVisualMson(value, sender);
        return Mson.prepondfix(null, mson, this.getInheritanceSuffix(object, source));
    }

    public Mson getInheritedVisual(O object, CommandSender sender) {
        Entry<O, V> inherited = this.getInheritedEntry(object);
        O source = inherited.getKey();
        V value = inherited.getValue();
        return this.getInheritedVisual(object, source, value, sender);
    }

    public Mson getInheritanceSuffix(O object, O source) {
        Mson ret = null;
        if (source != null && !source.equals(object)) {
            ret = Mson.mson(
                    "[",
                    this.getObjectType().getVisualMson(source),
                    "]"
            ).color(ChatColor.GRAY);
        }
        return ret;
    }

    public Mson getInheritanceSuffix(O object) {
        return this.getInheritanceSuffix(object, this.getInheritedObject(object));
    }

    // -------------------------------------------- //
    // VISUAL
    // -------------------------------------------- //

    public Mson getDisplayNameMson() {
        return Mson.mson(this.getName()).color(ChatColor.AQUA);
    }

    public String getDisplayName() {
        return ChatColor.AQUA + this.getName();
    }

    public List<Mson> getShowLines(O object, CommandSender sender) {
        Mson prefix = Mson.mson(
                this.getDisplayNameMson(),
                Mson.mson(":").color(ChatColor.GRAY)
        );
        List<Mson> ret = Mson.prepondfix(prefix, this.getValueType().getShow(this.getInheritedValue(object), sender), this.getInheritanceSuffix(object));

        for (ListIterator<Mson> it = ret.listIterator(1); it.hasNext(); ) {
            Mson mson = it.next();
            it.set(mson.text(SHOW_INDENT + mson.getText()));
        }

        return ret;
    }

    public static <O> @NotNull List<Mson> getShowLines(O object, CommandSender sender, @NotNull Collection<? extends Property<O, ?>> properties) {
        // Create
        List<Mson> ret = new KamiList<>();

        // Fill
        for (Property<O, ?> property : properties) {
            if (!property.isVisible()) continue;
            ret.addAll(property.getShowLines(object, sender));
        }

        // Return
        return ret;
    }

}
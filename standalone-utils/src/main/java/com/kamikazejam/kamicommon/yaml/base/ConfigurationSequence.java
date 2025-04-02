package com.kamikazejam.kamicommon.yaml.base;

import com.kamikazejam.kamicommon.util.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.SequenceNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public abstract class ConfigurationSequence<T extends ConfigurationMethods<?>> implements Iterable<T> {
    protected final ConfigurationMethods<?> parent;
    private final List<T> sections;

    public ConfigurationSequence(ConfigurationMethods<?> parent, @Nullable SequenceNode sequenceNode, String newPath) {
        this.parent = parent;

        // Load Sections
        this.sections = loadSections(sequenceNode, newPath);
    }

    @NotNull
    protected abstract List<T> loadSections(@Nullable SequenceNode sequenceNode, String newPath);

    // Basic collection operations
    public int size() {
        return sections.size();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    @NotNull
    public T get(int index) throws IndexOutOfBoundsException {
        return sections.get(index);
    }

    @NotNull
    public List<T> toList() {
        return new ArrayList<>(sections);
    }

    // Searching and filtering
    @NotNull
    public Optional<T> find(@NotNull Predicate<T> predicate) {
        Preconditions.checkNotNull(predicate);
        return sections.stream()
                .filter(predicate)
                .findFirst();
    }

    @NotNull
    public List<T> filter(@NotNull Predicate<T> predicate) {
        Preconditions.checkNotNull(predicate);
        return sections.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    // Stream operations
    @NotNull
    public Stream<T> stream() {
        return sections.stream();
    }

    // Iteration
    @Override
    public @NotNull Iterator<T> iterator() {
        return sections.iterator();
    }

    public void forEach(@NotNull Consumer<? super T> action) {
        Preconditions.checkNotNull(action);
        sections.forEach(action);
    }
}

package com.kamikazejam.kamicommon.menu.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utility class to paginate a list of objects. Supports generic types to paginate any object type.
 * @param <E> the object type to paginate
 */
@SuppressWarnings({"unused", "UnusedReturnValue", "DuplicatedCode"})
public class Pagination<E> extends ArrayList<E> {

    private final int pageSize;

    public Pagination(int pageSize) {
        this(pageSize, new ArrayList<>());
    }

    @SafeVarargs
    public Pagination(int pageSize, E... objects) {
        this(pageSize, Arrays.asList(objects));
    }

    public Pagination(int pageSize, List<E> objects) {
        this.pageSize = pageSize;
        addAll(objects);
    }

    public Pagination(int pageSize, Collection<E> objects) {
        this.pageSize = pageSize;
        addAll(objects);
    }

    public int pageSize() {
        return pageSize;
    }

    public int totalPages() {
        return (int) Math.ceil((double) size() / (double) pageSize);
    }


    /**
     * @param page (0 indexed)
     */
    public boolean pageExist(int page) {
        // [0, totalPages-1] from 0 indexing
        return page >= 0 && page < totalPages();
    }

    public boolean isNextPage(int page) {
        return page + 1 < totalPages();
    }

    public boolean isPrevious(int page) {
        return page >= 1;
    }

    public List<E> getPage(int page) {
        List<E> objects = new ArrayList<>();

        if (page < 0 || page >= totalPages()) {
            throw new IndexOutOfBoundsException("Index: " + page + ", Size: " + totalPages());
        }

        int min = page * pageSize;
        int max = page * pageSize + pageSize;

        if (max > size()) {
            max = size();
        }

        for (int i = min; max > i; i++) {
            objects.add(get(i));
        }

        return objects;
    }

    public List<Integer> getPageSlots(int page) {
        List<Integer> objects = new ArrayList<>();

        if (page < 0 || page >= totalPages()) {
            throw new IndexOutOfBoundsException("Index: " + page + ", Size: " + totalPages());
        }

        int min = page * pageSize;
        int max = page * pageSize + pageSize;

        if (max > size()) {
            max = size();
        }

        for (int i = min; max > i; i++) {
            objects.add(i);
        }

        return objects;
    }

    public int fixPage(int page) {
        if (page <= 0) {
            page = 1;
        }

        if (page > totalPages()) {
            page = totalPages();
        }

        return page;
    }
}

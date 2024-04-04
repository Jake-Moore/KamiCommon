package com.kamikazejam.kamicommon.util.pager;

public interface Stringifier<T> {
	String toString(T item, int index);
}

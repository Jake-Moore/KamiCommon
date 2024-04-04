package com.kamikazejam.kamicommon.util.comparator;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@SuppressWarnings("unused")
public class ComparatorWithList<T> extends ComparatorAbstract<T> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	public static <T> @NotNull ComparatorWithList<T> get(List<T> list) {
		return new ComparatorWithList<>(list);
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private List<T> list;

	public ComparatorWithList<T> setList(List<T> list) {
		this.list = list;
		return this;
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public ComparatorWithList(List<T> list) {
		this.list = list;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public int compareInner(T object1, T object2) {
		int index1 = this.getList().indexOf(object1);
		int index2 = this.getList().indexOf(object2);

		return Integer.compare(index1, index2);
	}

}

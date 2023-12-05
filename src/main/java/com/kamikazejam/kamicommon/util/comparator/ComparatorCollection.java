package com.kamikazejam.kamicommon.util.comparator;

import com.kamikazejam.kamicommon.util.collections.KamiList;
import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ComparatorCollection extends ComparatorAbstract<Object> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final ComparatorCollection i = new ComparatorCollection();

	@Contract(pure = true)
	public static ComparatorCollection get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@SuppressWarnings("unchecked")
	@Override
	public int compareInner(Object object1, Object object2) {
		// Create
		int ret;

		// Instance Of
		Collection<Object> collection1 = null;
		Collection<Object> collection2 = null;
		if (object1 instanceof Collection<?>) collection1 = (Collection<Object>) object1;
		if (object2 instanceof Collection<?>) collection2 = (Collection<Object>) object2;
		ret = ComparatorNull.get().compare(collection1, collection2);
		if (ret != 0) return ret;
		if (collection1 == null && collection2 == null) return ret;

		// Size
		int size1 = collection1.size();
		int size2 = collection2.size();
		ret = Integer.compare(size1, size2);
		if (ret != 0) return ret;

		// Elements
		List<Object> elements1 = new KamiList<>(collection1);
		elements1.sort(ComparatorSmart.get());

		List<Object> elements2 = new KamiList<>(collection2);
		elements2.sort(ComparatorSmart.get());

		Iterator<Object> iterator1 = elements1.iterator();
		Iterator<Object> iterator2 = elements2.iterator();

		while (iterator1.hasNext()) {
			Object element1 = iterator1.next();
			Object element2 = iterator2.next();

			ret = ComparatorSmart.get().compare(element1, element2);
			if (ret != 0) return ret;
		}

		// Return
		return ret;
	}

}

package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.KUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CommandEditContainerRemove<O, V> extends CommandEditContainerAbstract<O, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditContainerRemove(@NotNull EditSettings<O> settings, @NotNull Property<O, V> property) {
		// Super	
		super(settings, property);

		// Parameters
		this.addParametersElement(false);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void alter(V container) throws KamiCommonException {
		if (this.isCollection()) {
			this.alterCollection((Collection<?>) container);
		} else {
			this.alterMap((Map<?, ?>) container);
		}
	}

	// -------------------------------------------- //
	// OVERRIDE > COLLECTION
	// -------------------------------------------- //

	public void alterCollection(Collection<?> elements) throws KamiCommonException {
		// Args
		Object element = this.readElement();

		// Alter
		for (Iterator<?> it = elements.iterator(); it.hasNext(); ) {
			Object other = it.next();
			if (!this.getValueInnerType().equals(other, element)) continue;
			it.remove();
		}
	}

	// -------------------------------------------- //
	// OVERRIDE > MAP
	// -------------------------------------------- //

	@SuppressWarnings("unchecked")
	public void alterMap(Map<?, ?> elements) throws KamiCommonException {
		// Args
		Object element = this.readElement();
		Entry<?, ?> entry = (Entry<?, ?>) element;
		Object key = entry.getKey();
		Object value = entry.getValue();

		// Validate
		if (key == null && value == null) throw new KamiCommonException().addMsg("<b>Please supply key and/or value.");

		// Alter
		for (Iterator<?> it = elements.entrySet().iterator(); it.hasNext(); ) {
			Entry<Object, Object> other = (Entry<Object, Object>) it.next();

			if (key != null && !KUtil.equals(key, other.getKey())) continue;
			if (value != null && !KUtil.equals(value, other.getValue())) continue;

			it.remove();
		}
	}

}

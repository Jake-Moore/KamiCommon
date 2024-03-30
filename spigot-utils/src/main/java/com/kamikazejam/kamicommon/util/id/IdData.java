package com.kamikazejam.kamicommon.util.id;

import com.kamikazejam.kamicommon.util.interfaces.Identified;
import com.kamikazejam.kamicommon.util.interfaces.Named;
import lombok.Getter;
import org.jetbrains.annotations.Contract;

@SuppressWarnings("unused")
public class IdData implements Identified, Named {
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	protected final String id;

	@Override
	public String getId() {
		return this.id;
	}

	protected final String name;

	@Override
	public String getName() {
		return this.name;
	}

	@Getter
	protected final long millis;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	// It's always good to have a no-arg constructor
	// In our case it will be used by GSON.
	public IdData() {
		id = null;
		name = null;
		millis = 0;
	}

	@Contract("null, null -> fail")
	public IdData(String id, String name) {
		this(id, name, System.currentTimeMillis());
	}

	@Contract(value = "null, null, _ -> fail")
	public IdData(String id, String name, long millis) {
		if (id == null && name == null)
			throw new NullPointerException("Either id or name must be set. They can't both be null!");

		this.id = id;
		this.name = name;
		this.millis = millis;
	}

	// -------------------------------------------- //
	// HASH CODE & EQUALS
	// -------------------------------------------- //

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (int) (millis ^ (millis >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Contract(value = "null -> false", pure = true)
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof IdData)) return false;
		IdData other = (IdData) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		if (millis != other.millis) return false;
		if (name == null) {
            return other.name == null;
		} else return name.equals(other.name);
    }

}

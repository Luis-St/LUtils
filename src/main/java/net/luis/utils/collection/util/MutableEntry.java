/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.collection.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A mutable implementation of the {@link java.util.Map.Entry} interface.<br>
 *
 * @see SimpleEntry
 *
 * @author Luis-St
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 */
public class MutableEntry<K, V> extends SimpleEntry<K, V> {
	
	/**
	 * Constructs a new mutable entry with the specified key and value.<br>
	 * @param key The key of the entry
	 * @param value The value of the entry
	 * @throws NullPointerException If the key is null
	 */
	public MutableEntry(@NonNull K key, @Nullable V value) {
		super(key, value);
	}
	
	/**
	 * Sets the value of this entry to the specified value.<br>
	 * @param value The new value of this entry
	 * @return The old value of this entry
	 */
	@Override
	public @Nullable V setValue(@Nullable V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.getKey() + "=" + this.getValue();
	}
	//endregion
}

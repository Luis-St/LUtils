/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

import net.luis.utils.exception.ModificationException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * A simple immutable implementation of the {@link Map.Entry} interface.<br>
 *
 * @see MutableEntry
 *
 * @author Luis-St
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 */
@Unmodifiable
public class SimpleEntry<K, V> implements Map.Entry<K, V> {
	
	/**
	 * The key of the entry.<br>
	 */
	private final K key;
	/**
	 * The value of the entry.<br>
	 */
	protected V value;
	
	/**
	 * Constructs a new simple entry with the specified key and value.<br>
	 *
	 * @param key The key of the entry
	 * @param value The value of the entry
	 * @throws NullPointerException If the key is null
	 */
	public SimpleEntry(@NonNull K key, @Nullable V value) {
		this.key = Objects.requireNonNull(key, "Key must not be null");
		this.value = value;
	}
	
	/**
	 * @return The key of the entry
	 */
	@Override
	public @NonNull K getKey() {
		return this.key;
	}
	
	/**
	 * @return The value of the entry
	 */
	@Override
	public @Nullable V getValue() {
		return this.value;
	}
	
	/**
	 * Throws a {@link ModificationException} because the entry is immutable.<br>
	 *
	 * @param value The new value of the entry (ignored)
	 * @return Nothing, because an exception is thrown
	 * @throws ModificationException Always
	 */
	@Override
	public @Nullable V setValue(@Nullable V value) {
		throw new ModificationException("Value of the entry cannot be set to " + value + ", because the entry is muted");
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SimpleEntry<?, ?> that)) return false;
		
		if (!this.key.equals(that.key)) return false;
		return Objects.equals(this.value, that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key);
	}
	
	@Override
	public String toString() {
		return this.key + "=" + this.value;
	}
	//endregion
}

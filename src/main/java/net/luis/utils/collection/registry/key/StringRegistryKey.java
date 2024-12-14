/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.collection.registry.key;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A registry key that is represented by a string.<br>
 *
 * @author Luis-St
 */
public final class StringRegistryKey implements RegistryKey<String> {
	
	/**
	 * The default string registry key with an empty string as a key.<br>
	 */
	public static final StringRegistryKey DEFAULT = of("");
	
	/**
	 * The key of the registry key.<br>
	 */
	private final String key;
	
	/**
	 * Constructs a new string registry key.<br>
	 * @param key The key of the registry key
	 * @throws NullPointerException If the key is null
	 */
	private StringRegistryKey(@NotNull String key) {
		this.key = Objects.requireNonNull(key, "Key must not be null");
	}
	
	//region Static factory methods
	
	/**
	 * Creates a new string registry key.<br>
	 * @param key The key of the registry key
	 * @return The created string registry key
	 * @throws NullPointerException If the key is null
	 */
	public static @NotNull StringRegistryKey of(@NotNull String key) {
		return new StringRegistryKey(key);
	}
	//endregion
	
	@Override
	public @NotNull String getKey() {
		return this.key;
	}
	
	@Override
	public int compareTo(@NotNull RegistryKey<String> otherKey) {
		return this.key.compareTo(otherKey.getKey());
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof StringRegistryKey that)) return false;
		
		return this.key.equals(that.key);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key);
	}
	
	@Override
	public String toString() {
		return this.key;
	}
	//endregion
}

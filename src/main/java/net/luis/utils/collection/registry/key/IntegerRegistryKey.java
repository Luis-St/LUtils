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
 * A registry key that is represented by an integer value.<br>
 *
 * @author Luis-St
 */
public final class IntegerRegistryKey implements RegistryKey<Integer> {
	
	/**
	 * The default integer registry key with the value 0.<br>
	 */
	public static final IntegerRegistryKey DEFAULT = of(0);
	
	/**
	 * The integer value of the key.<br>
	 */
	private final int key;
	
	/**
	 * Constructs a new integer registry key with the given integer value.<br>
	 * @param key The integer value of the key
	 */
	private IntegerRegistryKey(int key) {
		this.key = key;
	}
	
	//region Static factory methods
	
	/**
	 * Creates a new integer registry key with the given integer value.<br>
	 * @param key The integer value of the key
	 * @return The created integer registry key
	 */
	public static @NotNull IntegerRegistryKey of(int key) {
		return new IntegerRegistryKey(key);
	}
	//endregion
	
	@Override
	public @NotNull Integer getKey() {
		return this.key;
	}
	
	@Override
	public int compareTo(@NotNull RegistryKey<Integer> otherKey) {
		return Integer.compare(this.key, otherKey.getKey());
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof IntegerRegistryKey that)) return false;
		
		return this.key == that.key;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key);
	}
	
	@Override
	public String toString() {
		return Integer.toString(this.key);
	}
	//endregion
}

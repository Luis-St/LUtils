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

package net.luis.utils.io.data.property;

import net.luis.utils.util.getter.DefaultValueGetter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a property with a key and a value.<br>
 * The key and the value are both strings.<br>
 * The value can be parsed to different types using the methods provided by this class.<br>
 *
 * @author Luis-St
 */
public class Property implements DefaultValueGetter {
	
	/**
	 * The key of the property.<br>
	 */
	private final String key;
	/**
	 * The value of the property.<br>
	 */
	private final String value;
	
	/**
	 * Constructs a new property with the given key and value.<br>
	 *
	 * @param key The key of the property
	 * @param value The value of the property
	 * @throws NullPointerException If the key or the value is null
	 */
	protected Property(@NotNull String key, @NotNull String value) { // protected to prevent instantiation from outside but allow inheritance
		this.key = Objects.requireNonNull(key, "Key must not be null");
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Creates a new property with the given key and value.<br>
	 *
	 * @param key The key of the property
	 * @param value The value of the property
	 * @return A new property
	 * @throws NullPointerException If the key or the value is null
	 */
	public static @NotNull Property of(@NotNull String key, @NotNull String value) {
		return new Property(key, value);
	}
	
	/**
	 * Returns the key of the property as a string.<br>
	 * @return The key of the property
	 */
	public @NotNull String getKey() {
		return this.key;
	}
	
	/**
	 * Returns the value of the property as a string.<br>
	 * @return The value of the property
	 */
	public @NotNull String getRawValue() {
		return this.value;
	}
	
	/**
	 * {@inheritDoc}
	 * This method is equivalent to {@link #getRawValue()}.<br>
	 * @return The value of the property
	 */
	@Override
	public @NotNull String getAsString() {
		return this.value;
	}
	
	/**
	 * Returns whether the key of the property is part of the given group.<br>
	 * A group is a string that ends optionally with a dot.<br>
	 * If the group is empty, the key is always part of the group.<br>
	 * Null will be treated as an empty string.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * Property property = Property.of("this.is.an.example.key", "value");
	 * property.isPartOfGroup(null); // true
	 * property.isPartOfGroup(""); // true
	 * property.isPartOfGroup("this"); // true
	 * property.isPartOfGroup("this.is"); // true
	 * property.isPartOfGroup("this.is.an.example.key"); // false -> 'key' is not part of the group, it is the key itself
	 * property.isPartOfGroup("some.other.group"); // false
	 * }</pre>
	 *
	 * @param group The group to check
	 * @return Whether the key is part of the group or not
	 * @throws IllegalArgumentException If the group is blank or starts with a dot
	 */
	public boolean isPartOfGroup(@Nullable String group) {
		if (StringUtils.isEmpty(group)) {
			return true;
		}
		if (group.isBlank()) {
			throw new IllegalArgumentException("Group must not be blank");
		}
		if (group.startsWith(".")) {
			throw new IllegalArgumentException("Group must not start with a dot");
		}
		if (!group.endsWith(".")) {
			group += ".";
		}
		return this.key.startsWith(group);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof Property property)) return false;
		
		if (!this.key.equals(property.key)) return false;
		return this.value.equals(property.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key, this.value);
	}
	
	@Override
	public String toString() {
		return this.key + "=" + this.value;
	}
	
	/**
	 * Returns the property as a string with the given configuration.<br>
	 * @param config The configuration to use
	 * @return The property as a string
	 * @throws NullPointerException If the configuration is null
	 */
	public @NotNull String toString(@NotNull PropertyConfig config) {
		Objects.requireNonNull(config, "Property config must not be null");
		String alignment = " ".repeat(config.alignment());
		return this.key + alignment + config.separator() + alignment + this.value;
	}
	//endregion
}

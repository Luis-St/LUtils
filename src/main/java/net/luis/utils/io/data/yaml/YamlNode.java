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

package net.luis.utils.io.data.yaml;

import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import net.luis.utils.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public interface YamlNode {
	
	/**
	 * Returns the name of the class in a human-readable format.<br>
	 * The name is the class name with spaces between the words and all letters in lower-case.<br>
	 * Used for debugging and error messages.<br>
	 * @return The name of the class in a human-readable format
	 */
	private @NotNull String getName() {
		return StringUtils.getReadableString(this.getClass().getSimpleName(), Character::isUpperCase).toLowerCase();
	}
	
	boolean hasAnchor();
	
	@Nullable String getAnchor();
	
	void setAnchor(@Nullable String anchor);
	
	default boolean isYamlStruct() {
		return this instanceof YamlStruct;
	}
	
	default boolean isYamlAnchor() {
		return this instanceof YamlAnchor;
	}
	
	default boolean isYamlNull() {
		return this instanceof YamlNull;
	}
	
	default boolean isYamlMapping() {
		return this instanceof YamlMapping;
	}
	
	default boolean isYamlSequence() {
		return this instanceof YamlSequence;
	}
	
	default boolean isYamlScalar() {
		return this instanceof YamlScalar;
	}
	
	default @NotNull YamlStruct getAsYamlStruct() {
		if (this instanceof YamlStruct struct) {
			return struct;
		}
		throw new YamlTypeException("Expected a yaml struct, but got a " + this.getName());
	}
	
	default @NotNull YamlAnchor getAsYamlAnchor() {
		if (this instanceof YamlAnchor anchor) {
			return anchor;
		}
		throw new YamlTypeException("Expected a yaml anchor, but got a " + this.getName());
	}
	
	default @NotNull YamlMapping getAsYamlMapping() {
		if (this instanceof YamlMapping mapping) {
			return mapping;
		}
		throw new YamlTypeException("Expected a yaml mapping, but got a " + this.getName());
	}
	
	default @NotNull YamlSequence getAsYamlSequence() {
		if (this instanceof YamlSequence sequence) {
			return sequence;
		}
		throw new YamlTypeException("Expected a yaml sequence, but got a " + this.getName());
	}
	
	default @NotNull YamlScalar getAsYamlScalar() {
		if (this instanceof YamlScalar scalar) {
			return scalar;
		}
		throw new YamlTypeException("Expected a yaml scalar, but got a " + this.getName());
	}
	
	default @NotNull YamlStruct createStruct(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (this instanceof YamlStruct) {
			throw new YamlTypeException("Cannot create a struct from a struct");
		}
		return new YamlStruct(key, this);
	}
	
	@NotNull String toString(@NotNull YamlConfig config);
}

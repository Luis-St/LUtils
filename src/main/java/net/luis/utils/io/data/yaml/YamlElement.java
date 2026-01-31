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

package net.luis.utils.io.data.yaml;

import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import net.luis.utils.lang.StringUtils;
import org.jspecify.annotations.NonNull;

/**
 * A generic class representing a yaml element.<br>
 * A yaml element can be a yaml mapping, a yaml sequence, a yaml scalar, a yaml null, a yaml anchor, or a yaml alias.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface YamlElement {
	
	/**
	 * Returns the name of the class in a human-readable format.<br>
	 * The name is the class name with spaces between the words and all letters in lower-case.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of the class in a human-readable format
	 */
	private @NonNull String getName() {
		return StringUtils.getReadableString(this.getClass().getSimpleName(), Character::isUpperCase).toLowerCase();
	}
	
	/**
	 * Checks if this yaml element is a yaml null.<br>
	 * @return True if this yaml element is a yaml null, false otherwise
	 */
	default boolean isYamlNull() {
		return this instanceof YamlNull;
	}
	
	/**
	 * Checks if this yaml element is a yaml mapping.<br>
	 * @return True if this yaml element is a yaml mapping, false otherwise
	 */
	default boolean isYamlMapping() {
		return this instanceof YamlMapping;
	}
	
	/**
	 * Checks if this yaml element is a yaml sequence.<br>
	 * @return True if this yaml element is a yaml sequence, false otherwise
	 */
	default boolean isYamlSequence() {
		return this instanceof YamlSequence;
	}
	
	/**
	 * Checks if this yaml element is a yaml scalar.<br>
	 * @return True if this yaml element is a yaml scalar, false otherwise
	 */
	default boolean isYamlScalar() {
		return this instanceof YamlScalar;
	}
	
	/**
	 * Checks if this yaml element is a yaml anchor.<br>
	 * @return True if this yaml element is a yaml anchor, false otherwise
	 */
	default boolean isYamlAnchor() {
		return this instanceof YamlAnchor;
	}
	
	/**
	 * Checks if this yaml element is a yaml alias.<br>
	 * @return True if this yaml element is a yaml alias, false otherwise
	 */
	default boolean isYamlAlias() {
		return this instanceof YamlAlias;
	}
	
	/**
	 * Checks if this yaml scalar is a boolean.<br>
	 * @return True if this yaml scalar is a boolean, false otherwise
	 */
	default boolean isYamlBoolean() {
		return this.isYamlScalar() && this.getAsYamlScalar().isYamlBoolean();
	}
	
	/**
	 * Checks if this yaml scalar is a number.<br>
	 * @return True if this yaml scalar is a number, false otherwise
	 */
	default boolean isYamlNumber() {
		return this.isYamlScalar() && this.getAsYamlScalar().isYamlNumber();
	}
	
	/**
	 * Checks if this yaml scalar is a byte.<br>
	 * @return True if this yaml scalar is a byte, false otherwise
	 */
	default boolean isYamlByte() {
		return this.isYamlScalar() && this.getAsYamlScalar().isYamlByte();
	}
	
	/**
	 * Checks if this yaml scalar is a short.<br>
	 * @return True if this yaml scalar is a short, false otherwise
	 */
	default boolean isYamlShort() {
		return this.isYamlScalar() && this.getAsYamlScalar().isYamlShort();
	}
	
	/**
	 * Checks if this yaml scalar is an integer.<br>
	 * @return True if this yaml scalar is an integer, false otherwise
	 */
	default boolean isYamlInteger() {
		return this.isYamlScalar() && this.getAsYamlScalar().isYamlInteger();
	}
	
	/**
	 * Checks if this yaml scalar is a long.<br>
	 * @return True if this yaml scalar is a long, false otherwise
	 */
	default boolean isYamlLong() {
		return this.isYamlScalar() && this.getAsYamlScalar().isYamlLong();
	}
	
	/**
	 * Checks if this yaml scalar is a float.<br>
	 * @return True if this yaml scalar is a float, false otherwise
	 */
	default boolean isYamlFloat() {
		return this.isYamlScalar() && this.getAsYamlScalar().isYamlFloat();
	}
	
	/**
	 * Checks if this yaml scalar is a double.<br>
	 * @return True if this yaml scalar is a double, false otherwise
	 */
	default boolean isYamlDouble() {
		return this.isYamlScalar() && this.getAsYamlScalar().isYamlDouble();
	}
	
	/**
	 * Checks if this yaml scalar is a string.<br>
	 * @return True if this yaml scalar is a string, false otherwise
	 */
	default boolean isYamlString() {
		return this.isYamlScalar() && this.getAsYamlScalar().isYamlString();
	}
	
	/**
	 * Converts this yaml element to a yaml mapping.<br>
	 * If this element is a yaml anchor containing a mapping, the mapping is returned.<br>
	 *
	 * @return This yaml element as a yaml mapping
	 * @throws YamlTypeException If this yaml element is not a yaml mapping
	 */
	default @NonNull YamlMapping getAsYamlMapping() {
		if (this instanceof YamlMapping mapping) {
			return mapping;
		}
		if (this instanceof YamlAnchor anchor && anchor.element() instanceof YamlMapping mapping) {
			return mapping;
		}
		throw new YamlTypeException("Expected a yaml mapping, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml element to a yaml sequence.<br>
	 * If this element is a yaml anchor containing a sequence, the sequence is returned.<br>
	 *
	 * @return This yaml element as a yaml sequence
	 * @throws YamlTypeException If this yaml element is not a yaml sequence
	 */
	default @NonNull YamlSequence getAsYamlSequence() {
		if (this instanceof YamlSequence sequence) {
			return sequence;
		}
		if (this instanceof YamlAnchor anchor && anchor.element() instanceof YamlSequence sequence) {
			return sequence;
		}
		throw new YamlTypeException("Expected a yaml sequence, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml element to a yaml scalar.<br>
	 * If this element is a yaml anchor containing a scalar, the scalar is returned.<br>
	 *
	 * @return This yaml element as a yaml scalar
	 * @throws YamlTypeException If this yaml element is not a yaml scalar
	 */
	default @NonNull YamlScalar getAsYamlScalar() {
		if (this instanceof YamlScalar scalar) {
			return scalar;
		}
		if (this instanceof YamlAnchor anchor && anchor.element() instanceof YamlScalar scalar) {
			return scalar;
		}
		throw new YamlTypeException("Expected a yaml scalar, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml element to a yaml anchor.<br>
	 *
	 * @return This yaml element as a yaml anchor
	 * @throws YamlTypeException If this yaml element is not a yaml anchor
	 */
	default @NonNull YamlAnchor getAsYamlAnchor() {
		if (this instanceof YamlAnchor anchor) {
			return anchor;
		}
		throw new YamlTypeException("Expected a yaml anchor, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml element to a yaml alias.<br>
	 *
	 * @return This yaml element as a yaml alias
	 * @throws YamlTypeException If this yaml element is not a yaml alias
	 */
	default @NonNull YamlAlias getAsYamlAlias() {
		if (this instanceof YamlAlias alias) {
			return alias;
		}
		throw new YamlTypeException("Expected a yaml alias, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml element to a boolean.<br>
	 *
	 * @return This yaml value as a boolean
	 * @throws YamlTypeException If this yaml element is not a yaml scalar or not a boolean
	 */
	default boolean getAsBoolean() {
		return this.getAsYamlScalar().getAsBoolean();
	}
	
	/**
	 * Converts this yaml element to a number.<br>
	 *
	 * @return This yaml value as a number
	 * @throws YamlTypeException If this yaml element is not a yaml scalar or not a number
	 */
	default @NonNull Number getAsNumber() {
		return this.getAsYamlScalar().getAsNumber();
	}
	
	/**
	 * Converts this yaml element to a byte.<br>
	 *
	 * @return This yaml value as a byte
	 * @throws YamlTypeException If this yaml element is not a yaml scalar or not a number
	 */
	default byte getAsByte() {
		return this.getAsYamlScalar().getAsByte();
	}
	
	/**
	 * Converts this yaml element to a short.<br>
	 *
	 * @return This yaml value as a short
	 * @throws YamlTypeException If this yaml element is not a yaml scalar or not a number
	 */
	default short getAsShort() {
		return this.getAsYamlScalar().getAsShort();
	}
	
	/**
	 * Converts this yaml element to an integer.<br>
	 *
	 * @return This yaml value as an integer
	 * @throws YamlTypeException If this yaml element is not a yaml scalar or not a number
	 */
	default int getAsInteger() {
		return this.getAsYamlScalar().getAsInteger();
	}
	
	/**
	 * Converts this yaml element to a long.<br>
	 *
	 * @return This yaml value as a long
	 * @throws YamlTypeException If this yaml element is not a yaml scalar or not a number
	 */
	default long getAsLong() {
		return this.getAsYamlScalar().getAsLong();
	}
	
	/**
	 * Converts this yaml element to a float.<br>
	 *
	 * @return This yaml value as a float
	 * @throws YamlTypeException If this yaml element is not a yaml scalar or not a number
	 */
	default float getAsFloat() {
		return this.getAsYamlScalar().getAsFloat();
	}
	
	/**
	 * Converts this yaml element to a double.<br>
	 *
	 * @return This yaml value as a double
	 * @throws YamlTypeException If this yaml element is not a yaml scalar or not a number
	 */
	default double getAsDouble() {
		return this.getAsYamlScalar().getAsDouble();
	}
	
	/**
	 * Converts this yaml element to a string.<br>
	 *
	 * @return This yaml value as a string
	 * @throws YamlTypeException If this yaml element is not a yaml scalar
	 */
	default @NonNull String getAsString() {
		return this.getAsYamlScalar().getAsString();
	}
	
	/**
	 * Unwraps this element if it's an anchor, otherwise returns itself.<br>
	 * Useful for getting the actual content regardless of anchor wrapping.<br>
	 *
	 * @return The unwrapped element
	 */
	default @NonNull YamlElement unwrap() {
		if (this instanceof YamlAnchor anchor) {
			return anchor.unwrap();
		}
		return this;
	}
	
	/**
	 * Returns a string representation of this yaml element based on the given yaml config.<br>
	 * The yaml config specifies how the yaml element should be formatted.<br>
	 *
	 * @param config The yaml config to use
	 * @return The string representation of this yaml element
	 */
	@NonNull String toString(@NonNull YamlConfig config);
}

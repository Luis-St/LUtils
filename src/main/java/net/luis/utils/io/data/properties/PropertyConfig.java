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

package net.luis.utils.io.data.properties;

import net.luis.utils.io.data.config.ReadOnly;
import net.luis.utils.io.data.config.WriteOnly;
import net.luis.utils.io.data.properties.exception.IllegalPropertyKeyException;
import net.luis.utils.util.ErrorAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author Luis-St
 *
 */

public record PropertyConfig(
	char separator,
	@WriteOnly int alignment,
	@ReadOnly @NotNull Set<Character> commentCharacters,
	@NotNull Pattern keyPattern,
	@NotNull Pattern valuePattern,
	@ReadOnly boolean advancedParsing,
	@NotNull Charset charset,
	@NotNull ErrorAction errorAction
) {
	
	public static final PropertyConfig DEFAULT = new PropertyConfig('=', 1, Set.of('#'), Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile(".*"), false, StandardCharsets.UTF_8, ErrorAction.IGNORE);
	
	public PropertyConfig {
		Objects.requireNonNull(commentCharacters, "Comment characters must not be null");
		Objects.requireNonNull(keyPattern, "Key pattern must not be null");
		Objects.requireNonNull(valuePattern, "Value pattern must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		Objects.requireNonNull(errorAction, "Error action must not be null");
		
		if (separator == '\0' || Character.isWhitespace(separator)) {
			throw new IllegalArgumentException("Separator must not be a whitespace character");
		}
		if (System.lineSeparator().equals("" + separator)) {
			throw new IllegalArgumentException("Separator must not be a newline character");
		}
		if (commentCharacters.contains(separator)) {
			throw new IllegalArgumentException("Separator must not be a comment character");
		}
	}
	
	public void ensureKeyMatches(@Nullable String key) throws IllegalPropertyKeyException {
		Objects.requireNonNull(key, "Key must not be null");
		if (!this.keyPattern.matcher(key).matches()) {
			throw new IllegalPropertyKeyException("Property key '" + key + "' does not match the pattern '" + this.keyPattern.pattern() + "' defined in property config");
		}
	}
	
	public void ensureValueMatches(@Nullable String value) throws IllegalPropertyKeyException {
		Objects.requireNonNull(value, "Value must not be null");
		if (!this.valuePattern.matcher(value).matches()) {
			throw new IllegalPropertyKeyException("Property value '" + value + "' does not match the pattern '" + this.valuePattern.pattern() + "' defined in property config");
		}
	}
}

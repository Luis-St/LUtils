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

package net.luis.utils.io.data.yaml.tmp;

import net.luis.utils.io.data.yaml.YamlConfig;
import net.luis.utils.util.getter.ValueGetter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class YamlScalar extends AbstractYamlNode implements ValueGetter {
	
	private final String value;
	
	public YamlScalar(boolean value) {
		this(String.valueOf(value));
	}
	
	public YamlScalar(@NotNull Number value) {
		this(String.valueOf(Objects.requireNonNull(value, "Value must not be null")));
	}
	
	public YamlScalar(@NotNull String value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	//region Getters
	@Override
	public @NotNull String getAsString() {
		return this.value;
	}
	
	public @NotNull LocalDateTime getAsTimestamp() {
		try {
			return LocalDateTime.parse(this.value);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Value '" + this.value + "' could not be parsed as a timestamp: " + e.getMessage(), e.getCause());
		}
	}
	
	public byte @NotNull [] getAsBinary() {
		try {
			return Base64.getDecoder().decode(this.value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Value '" + this.value + "' could not be parsed as a binary: " + e.getMessage(), e.getCause());
		}
	}
	
	public <T> @NotNull T getAsParsedBinary(@NotNull Function<byte[], T> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		try {
			return parser.apply(this.getAsBinary());
		} catch (Exception e) {
			throw new IllegalArgumentException("Value '" + this.value + "' could not be parsed as a binary: " + e.getMessage(), e.getCause());
		}
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof YamlScalar scalar)) return false;
		if (!super.equals(o)) return false;
		
		return this.value.equals(scalar.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.value);
	}
	
	@Override
	public String toString() {
		return this.toString(YamlConfig.DEFAULT);
	}
	
	@Override
	public @NotNull String toString(@NotNull YamlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		if (StringUtils.containsAny(this.value, "\n", "\r")) {
			return "|" + System.lineSeparator() + this.value.replaceAll("\\r\\n|\\r|\\n", System.lineSeparator());
		};
		if (config.quoteSingleLineStrings()) {
			return config.quoteCharacter() + this.value + config.quoteCharacter();
		}
		return this.getBaseString(config) + this.value;
	}
	//endregion
}

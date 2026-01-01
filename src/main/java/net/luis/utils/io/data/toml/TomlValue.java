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

package net.luis.utils.io.data.toml;

import net.luis.utils.io.data.toml.exception.TomlTypeException;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a TOML scalar value.<br>
 * A TOML value can be a boolean, number, string, or date/time type.<br>
 * Supported date/time types: LocalDate, LocalTime, LocalDateTime, OffsetDateTime.<br>
 *
 * @author Luis-St
 */
public class TomlValue implements TomlElement {
	
	/**
	 * RFC 3339 formatter for offset date-times.<br>
	 */
	private static final DateTimeFormatter RFC_3339_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
	
	/**
	 * The value of this TOML value.<br>
	 */
	private final Object value;
	
	/**
	 * Constructs a new TOML value with the given boolean value.<br>
	 * @param value The boolean value
	 */
	public TomlValue(boolean value) {
		this.value = value;
	}
	
	/**
	 * Constructs a new TOML value with the given number value.<br>
	 *
	 * @param value The number value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull Number value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new TOML value with the given character value.<br>
	 * @param value The character value
	 */
	public TomlValue(char value) {
		this.value = String.valueOf(value);
	}
	
	/**
	 * Constructs a new TOML value with the given string value.<br>
	 *
	 * @param value The string value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull String value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new TOML value with the given local date.<br>
	 *
	 * @param value The local date value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull LocalDate value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new TOML value with the given local time.<br>
	 *
	 * @param value The local time value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull LocalTime value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new TOML value with the given local date-time.<br>
	 *
	 * @param value The local date-time value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull LocalDateTime value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new TOML value with the given offset date-time.<br>
	 *
	 * @param value The offset date-time value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull OffsetDateTime value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	//region String helper methods
	
	/**
	 * Checks if the given string needs to be quoted in TOML output.<br>
	 *
	 * @param str The string to check
	 * @return True if the string needs quoting, false otherwise
	 */
	private static boolean needsQuoting(@NonNull String str) {
		if (str.isEmpty()) {
			return true;
		}
		
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c < 0x20 || c == '"' || c == '\\') {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Escapes special characters in a string for TOML output.<br>
	 *
	 * @param str The string to escape
	 * @return The escaped string
	 */
	private static @NonNull String escapeString(@NonNull String str) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
				case '"' -> result.append("\\\"");
				case '\\' -> result.append("\\\\");
				case '\b' -> result.append("\\b");
				case '\f' -> result.append("\\f");
				case '\n' -> result.append("\\n");
				case '\r' -> result.append("\\r");
				case '\t' -> result.append("\\t");
				default -> {
					if (c < 0x20) {
						result.append(String.format("\\u%04X", (int) c));
					} else {
						result.append(c);
					}
				}
			}
		}
		return result.toString();
	}
	//endregion
	
	/**
	 * Returns the name of the type of this TOML value in a human-readable format.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of the type of this TOML value in a human-readable format
	 * @throws IllegalStateException If the type of this TOML value is unknown
	 */
	private @NonNull String getName() {
		if (this.isBoolean()) {
			return "toml boolean";
		} else if (this.isNumber()) {
			return "toml number";
		} else if (this.isString()) {
			return "toml string";
		} else if (this.isLocalDate()) {
			return "toml local date";
		} else if (this.isLocalTime()) {
			return "toml local time";
		} else if (this.isLocalDateTime()) {
			return "toml local date-time";
		} else if (this.isOffsetDateTime()) {
			return "toml offset date-time";
		}
		throw new IllegalStateException("Unknown TOML value type");
	}
	
	//region Boolean
	
	@Override
	public boolean isBoolean() {
		return this.value instanceof Boolean;
	}
	
	@Override
	public boolean getAsBoolean() {
		if (this.isBoolean()) {
			return (boolean) this.value;
		}
		throw new TomlTypeException("Expected a TOML boolean, but found: " + this.getName());
	}
	
	//endregion
	
	//region Number
	
	@Override
	public boolean isNumber() {
		return this.value instanceof Number;
	}
	
	@Override
	public @NonNull Number getAsNumber() {
		if (this.isNumber()) {
			return (Number) this.value;
		}
		throw new TomlTypeException("Expected a TOML number, but found: " + this.getName());
	}
	
	@Override
	public boolean isByte() {
		return this.value instanceof Byte;
	}
	
	@Override
	public byte getAsByte() {
		if (this.isByte()) {
			return (byte) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().byteValue();
		}
		throw new TomlTypeException("Expected a TOML byte, but found: " + this.getName());
	}
	
	@Override
	public boolean isShort() {
		return this.value instanceof Short;
	}
	
	@Override
	public short getAsShort() {
		if (this.isShort()) {
			return (short) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().shortValue();
		}
		throw new TomlTypeException("Expected a TOML short, but found: " + this.getName());
	}
	
	@Override
	public boolean isInteger() {
		return this.value instanceof Integer;
	}
	
	@Override
	public int getAsInteger() {
		if (this.isInteger()) {
			return (int) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().intValue();
		}
		throw new TomlTypeException("Expected a TOML integer, but found: " + this.getName());
	}
	
	@Override
	public boolean isLong() {
		return this.value instanceof Long;
	}
	
	@Override
	public long getAsLong() {
		if (this.isLong()) {
			return (long) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().longValue();
		}
		throw new TomlTypeException("Expected a TOML long, but found: " + this.getName());
	}
	
	@Override
	public boolean isFloat() {
		return this.value instanceof Float;
	}
	
	@Override
	public float getAsFloat() {
		if (this.isFloat()) {
			return (float) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().floatValue();
		}
		throw new TomlTypeException("Expected a TOML float, but found: " + this.getName());
	}
	
	@Override
	public boolean isDouble() {
		return this.value instanceof Double;
	}
	
	@Override
	public double getAsDouble() {
		if (this.isDouble()) {
			return (double) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().doubleValue();
		}
		throw new TomlTypeException("Expected a TOML double, but found: " + this.getName());
	}
	
	//endregion
	
	//region String
	
	@Override
	public boolean isString() {
		return this.value instanceof String;
	}
	
	@Override
	public @NonNull String getAsString() {
		if (this.isString()) {
			return (String) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().toString();
		} else if (this.isBoolean()) {
			return String.valueOf(this.getAsBoolean());
		} else if (this.isDateTime()) {
			return this.formatDateTime(TomlConfig.DEFAULT);
		}
		throw new TomlTypeException("Expected a TOML string, but found: " + this.getName());
	}
	
	//endregion
	
	//region Date/Time
	
	@Override
	public boolean isLocalDate() {
		return this.value instanceof LocalDate;
	}
	
	@Override
	public @NonNull LocalDate getAsLocalDate() {
		if (this.isLocalDate()) {
			return (LocalDate) this.value;
		}
		throw new TomlTypeException("Expected a TOML local date, but found: " + this.getName());
	}
	
	@Override
	public boolean isLocalTime() {
		return this.value instanceof LocalTime;
	}
	
	@Override
	public @NonNull LocalTime getAsLocalTime() {
		if (this.isLocalTime()) {
			return (LocalTime) this.value;
		}
		throw new TomlTypeException("Expected a TOML local time, but found: " + this.getName());
	}
	
	@Override
	public boolean isLocalDateTime() {
		return this.value instanceof LocalDateTime;
	}
	
	@Override
	public @NonNull LocalDateTime getAsLocalDateTime() {
		if (this.isLocalDateTime()) {
			return (LocalDateTime) this.value;
		}
		throw new TomlTypeException("Expected a TOML local date-time, but found: " + this.getName());
	}
	
	@Override
	public boolean isOffsetDateTime() {
		return this.value instanceof OffsetDateTime;
	}
	
	@Override
	public @NonNull OffsetDateTime getAsOffsetDateTime() {
		if (this.isOffsetDateTime()) {
			return (OffsetDateTime) this.value;
		}
		throw new TomlTypeException("Expected a TOML offset date-time, but found: " + this.getName());
	}
	
	@Override
	public boolean isDateTime() {
		return this.isLocalDate() || this.isLocalTime() || this.isLocalDateTime() || this.isOffsetDateTime();
	}
	
	/**
	 * Formats the date/time value according to the given config.<br>
	 *
	 * @param config The TOML config
	 * @return The formatted date/time string
	 */
	private @NonNull String formatDateTime(@NonNull TomlConfig config) {
		if (this.isLocalDate()) {
			return this.getAsLocalDate().toString();
		} else if (this.isLocalTime()) {
			return this.getAsLocalTime().toString();
		} else if (this.isLocalDateTime()) {
			LocalDateTime dt = this.getAsLocalDateTime();
			return switch (config.dateTimeStyle()) {
				case RFC_3339, ISO_8601 -> dt.toString();
				case SPACE_SEPARATED -> dt.toLocalDate() + " " + dt.toLocalTime();
			};
		} else if (this.isOffsetDateTime()) {
			OffsetDateTime dt = this.getAsOffsetDateTime();
			return switch (config.dateTimeStyle()) {
				case RFC_3339 -> dt.format(RFC_3339_FORMATTER);
				case ISO_8601 -> dt.toLocalDateTime().toString();
				case SPACE_SEPARATED -> dt.toLocalDate() + " " + dt.toLocalTime();
			};
		}
		throw new IllegalStateException("Not a date/time value");
	}
	
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TomlValue that)) return false;
		
		return this.value.equals(that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.value);
	}
	
	@Override
	public String toString() {
		return this.toString(TomlConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull TomlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		
		if (this.isBoolean()) {
			return this.getAsBoolean() ? "true" : "false";
		} else if (this.isNumber()) {
			Number num = this.getAsNumber();
			if (num instanceof Double d) {
				if (d.isInfinite()) {
					return d > 0 ? "inf" : "-inf";
				} else if (d.isNaN()) {
					return "nan";
				}
			} else if (num instanceof Float f) {
				if (f.isInfinite()) {
					return f > 0 ? "inf" : "-inf";
				} else if (f.isNaN()) {
					return "nan";
				}
			}
			return num.toString();
		} else if (this.isString()) {
			String str = (String) this.value;
			if (config.useMultiLineStrings() && str.length() > config.multiLineStringThreshold() && str.contains("\n")) {
				return "\"\"\"" + str + "\"\"\"";
			}
			return "\"" + escapeString(str) + "\"";
		} else if (this.isDateTime()) {
			return this.formatDateTime(config);
		}
		
		return String.valueOf(this.value);
	}
	//endregion
}

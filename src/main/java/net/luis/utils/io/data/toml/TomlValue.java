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
 * Represents a toml scalar value.<br>
 * A toml value can be a boolean, number, string, or date/time type.<br>
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
	 * The value of this toml value.<br>
	 */
	private final Object value;
	
	/**
	 * Constructs a new toml value with the given boolean value.<br>
	 * @param value The boolean value
	 */
	public TomlValue(boolean value) {
		this.value = value;
	}
	
	/**
	 * Constructs a new toml value with the given number value.<br>
	 *
	 * @param value The number value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull Number value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new toml value with the given character value.<br>
	 * @param value The character value
	 */
	public TomlValue(char value) {
		this.value = String.valueOf(value);
	}
	
	/**
	 * Constructs a new toml value with the given string value.<br>
	 *
	 * @param value The string value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull String value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new toml value with the given local date.<br>
	 *
	 * @param value The local date value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull LocalDate value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new toml value with the given local time.<br>
	 *
	 * @param value The local time value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull LocalTime value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new toml value with the given local date-time.<br>
	 *
	 * @param value The local date-time value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull LocalDateTime value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new toml value with the given offset date-time.<br>
	 *
	 * @param value The offset date-time value
	 * @throws NullPointerException If the value is null
	 */
	public TomlValue(@NonNull OffsetDateTime value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Returns the name of the type of this toml value in a human-readable format.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of the type of this toml value in a human-readable format
	 * @throws IllegalStateException If the type of this toml value is unknown
	 */
	private @NonNull String getName() {
		if (this.isTomlBoolean()) {
			return "toml boolean";
		} else if (this.isTomlNumber()) {
			return "toml number";
		} else if (this.isTomlString()) {
			return "toml string";
		} else if (this.isTomlLocalDate()) {
			return "toml local date";
		} else if (this.isTomlLocalTime()) {
			return "toml local time";
		} else if (this.isTomlLocalDateTime()) {
			return "toml local date-time";
		} else if (this.isTomlOffsetDateTime()) {
			return "toml offset date-time";
		}
		throw new IllegalStateException("Unknown toml value type");
	}
	
	@Override
	public boolean isTomlBoolean() {
		return this.value instanceof Boolean;
	}
	
	@Override
	public boolean getAsBoolean() {
		if (this.isTomlBoolean()) {
			return (boolean) this.value;
		}
		throw new TomlTypeException("Expected a toml boolean, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlNumber() {
		return this.value instanceof Number;
	}
	
	@Override
	public @NonNull Number getAsNumber() {
		if (this.isTomlNumber()) {
			return (Number) this.value;
		}
		throw new TomlTypeException("Expected a toml number, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlByte() {
		return this.value instanceof Byte;
	}
	
	@Override
	public byte getAsByte() {
		if (this.isTomlByte()) {
			return (byte) this.value;
		} else if (this.isTomlNumber()) {
			return this.getAsNumber().byteValue();
		}
		throw new TomlTypeException("Expected a toml byte, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlShort() {
		return this.value instanceof Short;
	}
	
	@Override
	public short getAsShort() {
		if (this.isTomlShort()) {
			return (short) this.value;
		} else if (this.isTomlNumber()) {
			return this.getAsNumber().shortValue();
		}
		throw new TomlTypeException("Expected a toml short, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlInteger() {
		return this.value instanceof Integer;
	}
	
	@Override
	public int getAsInteger() {
		if (this.isTomlInteger()) {
			return (int) this.value;
		} else if (this.isTomlNumber()) {
			return this.getAsNumber().intValue();
		}
		throw new TomlTypeException("Expected a toml integer, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlLong() {
		return this.value instanceof Long;
	}
	
	@Override
	public long getAsLong() {
		if (this.isTomlLong()) {
			return (long) this.value;
		} else if (this.isTomlNumber()) {
			return this.getAsNumber().longValue();
		}
		throw new TomlTypeException("Expected a toml long, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlFloat() {
		return this.value instanceof Float;
	}
	
	@Override
	public float getAsFloat() {
		if (this.isTomlFloat()) {
			return (float) this.value;
		} else if (this.isTomlNumber()) {
			return this.getAsNumber().floatValue();
		}
		throw new TomlTypeException("Expected a toml float, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlDouble() {
		return this.value instanceof Double;
	}
	
	@Override
	public double getAsDouble() {
		if (this.isTomlDouble()) {
			return (double) this.value;
		} else if (this.isTomlNumber()) {
			return this.getAsNumber().doubleValue();
		}
		throw new TomlTypeException("Expected a toml double, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlString() {
		return this.value instanceof String;
	}
	
	@Override
	public @NonNull String getAsString() {
		if (this.isTomlString()) {
			return (String) this.value;
		} else if (this.isTomlNumber()) {
			return this.getAsNumber().toString();
		} else if (this.isTomlBoolean()) {
			return String.valueOf(this.getAsBoolean());
		} else if (this.isTomlDateTime()) {
			return this.formatDateTime(TomlConfig.DEFAULT);
		}
		throw new TomlTypeException("Expected a toml string, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlLocalDate() {
		return this.value instanceof LocalDate;
	}
	
	@Override
	public @NonNull LocalDate getAsLocalDate() {
		if (this.isTomlLocalDate()) {
			return (LocalDate) this.value;
		}
		throw new TomlTypeException("Expected a toml local date, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlLocalTime() {
		return this.value instanceof LocalTime;
	}
	
	@Override
	public @NonNull LocalTime getAsLocalTime() {
		if (this.isTomlLocalTime()) {
			return (LocalTime) this.value;
		}
		throw new TomlTypeException("Expected a toml local time, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlLocalDateTime() {
		return this.value instanceof LocalDateTime;
	}
	
	@Override
	public @NonNull LocalDateTime getAsLocalDateTime() {
		if (this.isTomlLocalDateTime()) {
			return (LocalDateTime) this.value;
		}
		throw new TomlTypeException("Expected a toml local date-time, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlOffsetDateTime() {
		return this.value instanceof OffsetDateTime;
	}
	
	@Override
	public @NonNull OffsetDateTime getAsOffsetDateTime() {
		if (this.isTomlOffsetDateTime()) {
			return (OffsetDateTime) this.value;
		}
		throw new TomlTypeException("Expected a toml offset date-time, but found: " + this.getName());
	}
	
	@Override
	public boolean isTomlDateTime() {
		return this.isTomlLocalDate() || this.isTomlLocalTime() || this.isTomlLocalDateTime() || this.isTomlOffsetDateTime();
	}
	
	/**
	 * Formats the date/time value according to the given config.<br>
	 *
	 * @param config The toml config
	 * @return The formatted date/time string
	 * @throws NullPointerException If the config is null
	 * @throws IllegalStateException If this is not a date/time value
	 */
	private @NonNull String formatDateTime(@NonNull TomlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		if (this.isTomlLocalDate()) {
			return this.getAsLocalDate().toString();
		} else if (this.isTomlLocalTime()) {
			return this.getAsLocalTime().toString();
		} else if (this.isTomlLocalDateTime()) {
			LocalDateTime dt = this.getAsLocalDateTime();
			
			return switch (config.dateTimeStyle()) {
				case RFC_3339, ISO_8601 -> dt.toString();
				case SPACE_SEPARATED -> dt.toLocalDate() + " " + dt.toLocalTime();
			};
		} else if (this.isTomlOffsetDateTime()) {
			OffsetDateTime dt = this.getAsOffsetDateTime();
			
			return switch (config.dateTimeStyle()) {
				case RFC_3339 -> dt.format(RFC_3339_FORMATTER);
				case ISO_8601 -> dt.toLocalDateTime().toString();
				case SPACE_SEPARATED -> dt.toLocalDate() + " " + dt.toLocalTime();
			};
		}
		throw new IllegalStateException("Not a date/time value");
	}
	
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
		
		if (this.isTomlBoolean()) {
			return this.getAsBoolean() ? "true" : "false";
		} else if (this.isTomlNumber()) {
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
		} else if (this.isTomlString()) {
			String str = (String) this.value;
			
			if (config.useMultiLineStrings() && str.length() > config.multiLineStringThreshold() && str.contains("\n")) {
				return "\"\"\"" + str + "\"\"\"";
			}
			return "\"" + TomlHelper.escapeString(str) + "\"";
		} else if (this.isTomlDateTime()) {
			return this.formatDateTime(config);
		}
		
		return String.valueOf(this.value);
	}
	//endregion
}

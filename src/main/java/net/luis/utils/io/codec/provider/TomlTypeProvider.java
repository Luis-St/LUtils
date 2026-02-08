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

package net.luis.utils.io.codec.provider;

import com.google.common.collect.Maps;
import net.luis.utils.annotation.type.Singleton;
import net.luis.utils.io.data.toml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * Type provider implementation for toml elements.<br>
 * This class is a singleton and should be accessed through the {@link #INSTANCE} constant.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class TomlTypeProvider implements TypeProvider<TomlElement> {
	
	/**
	 * An empty toml element instance.<br>
	 * Used for internal purposes only.<br>
	 * The toml element has no string representation and will throw an exception if {@link TomlElement#toString(TomlConfig)} is called.<br>
	 */
	private static final TomlElement EMPTY_ELEMENT = _ -> "Empty toml element has no string representation";
	
	/*
	 * The singleton instance of this class.<br>
	 */
	public static final TomlTypeProvider INSTANCE = new TomlTypeProvider();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private TomlTypeProvider() {}
	
	@Override
	public @NonNull TomlElement empty() {
		return EMPTY_ELEMENT;
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createNull(@NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return TomlNull.INSTANCE;
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createBoolean(boolean value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new TomlValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createByte(byte value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new TomlValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createShort(short value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new TomlValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createInteger(int value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new TomlValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createLong(long value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new TomlValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createFloat(float value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new TomlValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createDouble(double value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new TomlValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createString(@Nullable String value, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid string");
		}
		return new TomlValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createList(@Nullable List<? extends TomlElement> values, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (values == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid list");
		}
		return new TomlArray(values);
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createMap(@NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new TomlTable();
	}
	
	@Override
	public <X extends Exception> @NonNull TomlElement createMap(@Nullable Map<String, ? extends TomlElement> values, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (values == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		return new TomlTable(values);
	}
	
	@Override
	public <X extends Exception> boolean isEmpty(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not empty");
		}
		return type == EMPTY_ELEMENT;
	}
	
	@Override
	public <X extends Exception> boolean isNull(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not toml null");
		}
		return type.isTomlNull();
	}
	
	@Override
	public <X extends Exception> @NonNull Boolean getBoolean(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a boolean");
		}
		if (!type.isTomlValue()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isTomlBoolean()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml boolean");
		}
		return value.getAsBoolean();
	}
	
	@Override
	public <X extends Exception> @NonNull Byte getByte(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a byte");
		}
		if (!type.isTomlValue()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isTomlNumber()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml byte");
		}
		return value.getAsByte();
	}
	
	@Override
	public <X extends Exception> @NonNull Short getShort(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a short");
		}
		if (!type.isTomlValue()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isTomlNumber()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml short");
		}
		return value.getAsShort();
	}
	
	@Override
	public <X extends Exception> @NonNull Integer getInteger(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not an integer");
		}
		if (!type.isTomlValue()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isTomlNumber()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml integer");
		}
		return value.getAsInteger();
	}
	
	@Override
	public <X extends Exception> @NonNull Long getLong(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a long");
		}
		if (!type.isTomlValue()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isTomlNumber()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml long");
		}
		return value.getAsLong();
	}
	
	@Override
	public <X extends Exception> @NonNull Float getFloat(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a float");
		}
		if (!type.isTomlValue()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isTomlNumber()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is a toml string, not a toml float");
		}
		return value.getAsFloat();
	}
	
	@Override
	public <X extends Exception> @NonNull Double getDouble(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a double");
		}
		if (!type.isTomlValue()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isTomlNumber()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml double");
		}
		return value.getAsDouble();
	}
	
	@Override
	public <X extends Exception> @NonNull String getString(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a string");
		}
		if (!type.isTomlValue()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isTomlString()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml string");
		}
		return value.getAsString();
	}
	
	@Override
	public <X extends Exception> @NonNull List<TomlElement> getList(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid list");
		}
		
		if (!type.isTomlArray()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml array");
		}
		return type.getAsTomlArray().getElements();
	}
	
	@Override
	public <X extends Exception> @NonNull Map<String, TomlElement> getMap(@Nullable TomlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (!type.isTomlTable()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml table");
		}
		
		Map<String, TomlElement> map = Maps.newLinkedHashMap();
		type.getAsTomlTable().forEach(map::put);
		return map;
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> boolean has(@Nullable TomlElement type, @Nullable String key, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		if (!type.isTomlTable()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml table");
		}
		return type.getAsTomlTable().containsKey(key);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> @Nullable TomlElement get(@Nullable TomlElement type, @Nullable String key, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		if (!type.isTomlTable()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml table");
		}
		return type.getAsTomlTable().get(key);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> void set(@Nullable TomlElement type, @Nullable String key, @Nullable TomlElement value, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		if (!type.isTomlTable()) {
			throw exceptionConstructor.apply("Toml element '" + type + "' is not a toml table");
		}
		type.getAsTomlTable().add(key, value);
	}
	
	@Override
	public <X extends Exception> @UnknownNullability TomlElement merge(@Nullable TomlElement current, @Nullable TomlElement value, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (current == null) {
			return value;
		}
		if (value == null) {
			return current;
		}
		
		if (current == EMPTY_ELEMENT || current.isTomlNull()) {
			return value;
		}
		if (value == EMPTY_ELEMENT || value.isTomlNull()) {
			return current;
		}
		
		if (current.isTomlArray() && value.isTomlArray()) {
			TomlArray array = current.getAsTomlArray();
			array.addAll(value.getAsTomlArray());
			return array;
		}
		
		if (current.isTomlTable() && value.isTomlTable()) {
			TomlTable table = current.getAsTomlTable();
			table.addAll(value.getAsTomlTable());
			return table;
		}
		throw exceptionConstructor.apply("Unable to merge '" + current + "' with '" + value + "'");
	}
}

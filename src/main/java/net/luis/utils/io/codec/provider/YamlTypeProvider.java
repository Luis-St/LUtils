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
import net.luis.utils.io.data.yaml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * Type provider implementation for yaml elements.<br>
 * This class is a singleton and should be accessed through the {@link #INSTANCE} constant.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class YamlTypeProvider implements TypeProvider<YamlElement> {
	
	/**
	 * An empty yaml element instance.<br>
	 * Used for internal purposes only.<br>
	 * The yaml element has no string representation and will throw an exception if {@link YamlElement#toString(YamlConfig)} is called.<br>
	 */
	private static final YamlElement EMPTY_ELEMENT = _ -> "Empty yaml element has no string representation";
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final YamlTypeProvider INSTANCE = new YamlTypeProvider();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private YamlTypeProvider() {}
	
	@Override
	public @NonNull YamlElement empty() {
		return EMPTY_ELEMENT;
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createNull(@NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return YamlNull.INSTANCE;
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createBoolean(boolean value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new YamlScalar(value);
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createByte(byte value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new YamlScalar(value);
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createShort(short value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new YamlScalar(value);
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createInteger(int value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new YamlScalar(value);
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createLong(long value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new YamlScalar(value);
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createFloat(float value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new YamlScalar(value);
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createDouble(double value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new YamlScalar(value);
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createString(@Nullable String value, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid string");
		}
		return new YamlScalar(value);
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createList(@Nullable List<? extends YamlElement> values, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (values == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid list");
		}
		return new YamlSequence(values);
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createMap(@NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new YamlMapping();
	}
	
	@Override
	public <X extends Exception> @NonNull YamlElement createMap(@Nullable Map<String, ? extends YamlElement> values, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (values == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		return new YamlMapping(values);
	}
	
	@Override
	public <X extends Exception> boolean isEmpty(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not empty");
		}
		return type == EMPTY_ELEMENT;
	}
	
	@Override
	public <X extends Exception> boolean isNull(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not yaml null");
		}
		return type.isYamlNull();
	}
	
	@Override
	public <X extends Exception> @NonNull Boolean getBoolean(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a boolean");
		}
		if (!type.isYamlScalar()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlBoolean()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml boolean");
		}
		return scalar.getAsBoolean();
	}
	
	@Override
	public <X extends Exception> @NonNull Byte getByte(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a byte");
		}
		if (!type.isYamlScalar()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml byte");
		}
		return scalar.getAsByte();
	}
	
	@Override
	public <X extends Exception> @NonNull Short getShort(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a short");
		}
		if (!type.isYamlScalar()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml short");
		}
		return scalar.getAsShort();
	}
	
	@Override
	public <X extends Exception> @NonNull Integer getInteger(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not an integer");
		}
		if (!type.isYamlScalar()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml integer");
		}
		return scalar.getAsInteger();
	}
	
	@Override
	public <X extends Exception> @NonNull Long getLong(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a long");
		}
		if (!type.isYamlScalar()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml long");
		}
		return scalar.getAsLong();
	}
	
	@Override
	public <X extends Exception> @NonNull Float getFloat(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a float");
		}
		if (!type.isYamlScalar()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (scalar.isYamlString()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is a yaml string, not a yaml float");
		}
		return scalar.getAsFloat();
	}
	
	@Override
	public <X extends Exception> @NonNull Double getDouble(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a double");
		}
		if (!type.isYamlScalar()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml double");
		}
		return scalar.getAsDouble();
	}
	
	@Override
	public <X extends Exception> @NonNull String getString(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a string");
		}
		if (!type.isYamlScalar()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlString()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml string");
		}
		return scalar.getAsString();
	}
	
	@Override
	public <X extends Exception> @NonNull List<YamlElement> getList(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid list");
		}
		
		if (!type.isYamlSequence()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml sequence");
		}
		return type.getAsYamlSequence().getElements();
	}
	
	@Override
	public <X extends Exception> @NonNull Map<String, YamlElement> getMap(@Nullable YamlElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (!type.isYamlMapping()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml mapping");
		}
		
		Map<String, YamlElement> map = Maps.newLinkedHashMap();
		type.getAsYamlMapping().forEach(map::put);
		return map;
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> boolean has(@Nullable YamlElement type, @Nullable String key, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		if (!type.isYamlMapping()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml mapping");
		}
		return type.getAsYamlMapping().containsKey(key);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> @Nullable YamlElement get(@Nullable YamlElement type, @Nullable String key, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		if (!type.isYamlMapping()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml mapping");
		}
		return type.getAsYamlMapping().get(key);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> void set(@Nullable YamlElement type, @Nullable String key, @Nullable YamlElement value, @NotNull Function<String, X> exceptionConstructor) throws X {
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
		
		if (!type.isYamlMapping()) {
			throw exceptionConstructor.apply("Yaml element '" + type + "' is not a yaml mapping");
		}
		type.getAsYamlMapping().add(key, value);
	}
	
	@Override
	public <X extends Exception> @UnknownNullability YamlElement merge(@Nullable YamlElement current, @Nullable YamlElement value, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (current == null) {
			return value;
		}
		if (value == null) {
			return current;
		}
		
		if (current == EMPTY_ELEMENT || current.isYamlNull()) {
			return value;
		}
		if (value == EMPTY_ELEMENT || value.isYamlNull()) {
			return current;
		}
		
		if (current.isYamlSequence() && value.isYamlSequence()) {
			YamlSequence sequence = current.getAsYamlSequence();
			sequence.addAll(value.getAsYamlSequence());
			return sequence;
		}
		
		if (current.isYamlMapping() && value.isYamlMapping()) {
			YamlMapping mapping = current.getAsYamlMapping();
			mapping.addAll(value.getAsYamlMapping());
			return mapping;
		}
		throw exceptionConstructor.apply("Unable to merge '" + current + "' with '" + value + "'");
	}
}

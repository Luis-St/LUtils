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
import net.luis.utils.io.data.toon.*;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * Type provider implementation for toon elements.<br>
 * This class is a singleton and should be accessed through the {@link #INSTANCE} constant.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class ToonTypeProvider implements TypeProvider<ToonElement> {
	
	/**
	 * An empty toon element instance.<br>
	 * Used for internal purposes only.<br>
	 * The toon element has no string representation and will throw an exception if {@link ToonElement#toString(ToonConfig)} is called.<br>
	 */
	private static final ToonElement EMPTY_ELEMENT = _ -> "Empty toon element has no string representation";
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final ToonTypeProvider INSTANCE = new ToonTypeProvider();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private ToonTypeProvider() {}
	
	@Override
	public @NonNull ToonElement empty() {
		return EMPTY_ELEMENT;
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createNull(@NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return ToonNull.INSTANCE;
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createBoolean(boolean value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new ToonValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createByte(byte value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new ToonValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createShort(short value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new ToonValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createInteger(int value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new ToonValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createLong(long value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new ToonValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createFloat(float value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new ToonValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createDouble(double value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new ToonValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createString(@Nullable String value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid string");
		}
		return new ToonValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createList(@Nullable List<? extends ToonElement> values, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (values == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid list");
		}
		return new ToonArray(values);
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createMap(@NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new ToonObject();
	}
	
	@Override
	public <X extends Exception> @NonNull ToonElement createMap(@Nullable Map<String, ? extends ToonElement> values, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (values == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		return new ToonObject(values);
	}
	
	@Override
	public <X extends Exception> boolean isEmpty(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not empty");
		}
		return type == EMPTY_ELEMENT;
	}
	
	@Override
	public <X extends Exception> boolean isNull(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not toon null");
		}
		return type.isToonNull();
	}
	
	@Override
	public <X extends Exception> @NonNull Boolean getBoolean(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a boolean");
		}
		if (!type.isToonValue()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon value");
		}
		
		ToonValue value = type.getAsToonValue();
		if (!value.isToonBoolean()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon boolean");
		}
		return value.getAsBoolean();
	}
	
	@Override
	public <X extends Exception> @NonNull Byte getByte(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a byte");
		}
		if (!type.isToonValue()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon value");
		}
		
		ToonValue value = type.getAsToonValue();
		if (!value.isToonNumber()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon byte");
		}
		return value.getAsByte();
	}
	
	@Override
	public <X extends Exception> @NonNull Short getShort(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a short");
		}
		if (!type.isToonValue()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon value");
		}
		
		ToonValue value = type.getAsToonValue();
		if (!value.isToonNumber()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon short");
		}
		return value.getAsShort();
	}
	
	@Override
	public <X extends Exception> @NonNull Integer getInteger(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not an integer");
		}
		if (!type.isToonValue()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon value");
		}
		
		ToonValue value = type.getAsToonValue();
		if (!value.isToonNumber()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon integer");
		}
		return value.getAsInteger();
	}
	
	@Override
	public <X extends Exception> @NonNull Long getLong(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a long");
		}
		if (!type.isToonValue()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon value");
		}
		
		ToonValue value = type.getAsToonValue();
		if (!value.isToonNumber()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon long");
		}
		return value.getAsLong();
	}
	
	@Override
	public <X extends Exception> @NonNull Float getFloat(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a float");
		}
		if (!type.isToonValue()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon value");
		}
		
		ToonValue value = type.getAsToonValue();
		if (!value.isToonNumber()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is a toon string, not a toon float");
		}
		return value.getAsFloat();
	}
	
	@Override
	public <X extends Exception> @NonNull Double getDouble(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a double");
		}
		if (!type.isToonValue()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon value");
		}
		
		ToonValue value = type.getAsToonValue();
		if (!value.isToonNumber()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon double");
		}
		return value.getAsDouble();
	}
	
	@Override
	public <X extends Exception> @NonNull String getString(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a string");
		}
		if (!type.isToonValue()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon value");
		}
		
		ToonValue value = type.getAsToonValue();
		if (!value.isToonString()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon string");
		}
		return value.getAsString();
	}
	
	@Override
	public <X extends Exception> @NonNull List<ToonElement> getList(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid list");
		}
		
		if (!type.isToonArray()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon array");
		}
		return type.getAsToonArray().getElements();
	}
	
	@Override
	public <X extends Exception> @NonNull Map<String, ToonElement> getMap(@Nullable ToonElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (!type.isToonObject()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon object");
		}
		
		Map<String, ToonElement> map = Maps.newLinkedHashMap();
		type.getAsToonObject().forEach(map::put);
		return map;
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> boolean has(@Nullable ToonElement type, @Nullable String key, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		if (!type.isToonObject()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon object");
		}
		return type.getAsToonObject().containsKey(key);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> @Nullable ToonElement get(@Nullable ToonElement type, @Nullable String key, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		if (!type.isToonObject()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon object");
		}
		return type.getAsToonObject().get(key);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> void set(@Nullable ToonElement type, @Nullable String key, @Nullable ToonElement value, @NonNull Function<String, X> exceptionConstructor) throws X {
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
		
		if (!type.isToonObject()) {
			throw exceptionConstructor.apply("Toon element '" + type + "' is not a toon object");
		}
		type.getAsToonObject().add(key, value);
	}
	
	@Override
	public <X extends Exception> @UnknownNullability ToonElement merge(@Nullable ToonElement current, @Nullable ToonElement value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (current == null) {
			return value;
		}
		if (value == null) {
			return current;
		}
		
		if (current == EMPTY_ELEMENT || current.isToonNull()) {
			return value;
		}
		if (value == EMPTY_ELEMENT || value.isToonNull()) {
			return current;
		}
		
		if (current.isToonArray() && value.isToonArray()) {
			ToonArray array = current.getAsToonArray();
			array.addAll(value.getAsToonArray());
			return array;
		}
		
		if (current.isToonObject() && value.isToonObject()) {
			ToonObject object = current.getAsToonObject();
			object.addAll(value.getAsToonObject());
			return object;
		}
		throw exceptionConstructor.apply("Unable to merge '" + current + "' with '" + value + "'");
	}
}

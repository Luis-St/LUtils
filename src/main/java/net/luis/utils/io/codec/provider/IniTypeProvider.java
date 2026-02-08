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
import net.luis.utils.io.data.ini.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Type provider implementation for ini elements.<br>
 * This class is a singleton and should be accessed through the {@link #INSTANCE} constant.<br>
 * <p>
 *     The INI format has the following limitations:
 * </p>
 * <ul>
 *     <li>No native list support - list operations will return errors</li>
 *     <li>Single level nesting only - values must be primitives or null</li>
 *     <li>Maps are represented as {@link IniSection IniSections}</li>
 * </ul>
 *
 * @author Luis-St
 */
@Singleton
public final class IniTypeProvider implements TypeProvider<IniElement> {
	
	/**
	 * An empty ini element instance.<br>
	 * Used for internal purposes only.<br>
	 * The ini element has no string representation and will throw an exception if {@link IniElement#toString(IniConfig)} is called.<br>
	 */
	private static final IniElement EMPTY_ELEMENT = _ -> "Empty ini element has no string representation";
	
	/**
	 * The prefix for generated section names.<br>
	 */
	private static final String GENERATED_SECTION = "_generated_section_";
	
	/**
	 * Counter for generating unique section names.<br>
	 */
	private static final AtomicInteger COUNTER = new AtomicInteger(0);
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final IniTypeProvider INSTANCE = new IniTypeProvider();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private IniTypeProvider() {}
	
	@Override
	public @NonNull IniElement empty() {
		return EMPTY_ELEMENT;
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createNull(@NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return IniNull.INSTANCE;
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createBoolean(boolean value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new IniValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createByte(byte value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new IniValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createShort(short value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new IniValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createInteger(int value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new IniValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createLong(long value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new IniValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createFloat(float value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new IniValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createDouble(double value, @NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new IniValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createString(@Nullable String value, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid string");
		}
		return new IniValue(value);
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createList(@Nullable List<? extends IniElement> values, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		throw exceptionConstructor.apply("Ini format does not support lists");
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createMap(@NotNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new IniSection(GENERATED_SECTION + COUNTER.getAndIncrement());
	}
	
	@Override
	public <X extends Exception> @NonNull IniElement createMap(@Nullable Map<String, ? extends IniElement> values, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (values == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		
		IniSection section = new IniSection(GENERATED_SECTION + COUNTER.getAndIncrement());
		section.addAll(values);
		return section;
	}
	
	@Override
	public <X extends Exception> boolean isEmpty(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not empty");
		}
		return type == EMPTY_ELEMENT;
	}
	
	@Override
	public <X extends Exception> boolean isNull(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not ini null");
		}
		return type.isIniNull();
	}
	
	@Override
	public <X extends Exception> @NonNull Boolean getBoolean(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a boolean");
		}
		if (!type.isIniValue()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniBoolean()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini boolean");
		}
		return value.getAsBoolean();
	}
	
	@Override
	public <X extends Exception> @NonNull Byte getByte(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a byte");
		}
		if (!type.isIniValue()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniNumber()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini byte");
		}
		return value.getAsByte();
	}
	
	@Override
	public <X extends Exception> @NonNull Short getShort(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a short");
		}
		if (!type.isIniValue()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniNumber()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini short");
		}
		return value.getAsShort();
	}
	
	@Override
	public <X extends Exception> @NonNull Integer getInteger(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not an integer");
		}
		if (!type.isIniValue()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniNumber()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini integer");
		}
		return value.getAsInteger();
	}
	
	@Override
	public <X extends Exception> @NonNull Long getLong(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a long");
		}
		if (!type.isIniValue()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniNumber()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini long");
		}
		return value.getAsLong();
	}
	
	@Override
	public <X extends Exception> @NonNull Float getFloat(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a float");
		}
		if (!type.isIniValue()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (value.isIniString()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is an ini string, not an ini float");
		}
		return value.getAsFloat();
	}
	
	@Override
	public <X extends Exception> @NonNull Double getDouble(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a double");
		}
		if (!type.isIniValue()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniNumber()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini double");
		}
		return value.getAsDouble();
	}
	
	@Override
	public <X extends Exception> @NonNull String getString(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a string");
		}
		if (!type.isIniValue()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniString()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini string");
		}
		return value.getAsString();
	}
	
	@Override
	public <X extends Exception> @NonNull List<IniElement> getList(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		throw exceptionConstructor.apply("Ini format does not support lists");
	}
	
	@Override
	public <X extends Exception> @NonNull Map<String, IniElement> getMap(@Nullable IniElement type, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (!type.isIniSection()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini section");
		}
		
		Map<String, IniElement> map = Maps.newLinkedHashMap();
		type.getAsIniSection().forEach(map::put);
		return map;
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> boolean has(@Nullable IniElement type, @Nullable String key, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		if (!type.isIniSection()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini section");
		}
		return type.getAsIniSection().containsKey(key);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> @Nullable IniElement get(@Nullable IniElement type, @Nullable String key, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		if (!type.isIniSection()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini section");
		}
		return type.getAsIniSection().get(key);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <X extends Exception> void set(@Nullable IniElement type, @Nullable String key, @Nullable IniElement value, @NotNull Function<String, X> exceptionConstructor) throws X {
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
		
		if (!type.isIniSection()) {
			throw exceptionConstructor.apply("Ini element '" + type + "' is not an ini section");
		}
		type.getAsIniSection().add(key, value);
	}
	
	@Override
	public <X extends Exception> @UnknownNullability IniElement merge(@Nullable IniElement current, @Nullable IniElement value, @NotNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		if (current == null) {
			return value;
		}
		if (value == null) {
			return current;
		}
		
		if (current == EMPTY_ELEMENT || current.isIniNull()) {
			return value;
		}
		if (value == EMPTY_ELEMENT || value.isIniNull()) {
			return current;
		}
		
		if (current.isIniSection() && value.isIniSection()) {
			IniSection section = current.getAsIniSection();
			section.addAll(value.getAsIniSection());
			return section;
		}
		throw exceptionConstructor.apply("Unable to merge '" + current + "' with '" + value + "'");
	}
}

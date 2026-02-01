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
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
	public @NonNull IniElement createNull() {
		return IniNull.INSTANCE;
	}
	
	@Override
	public @NonNull IniElement createBoolean(boolean value) {
		return new IniValue(value);
	}
	
	@Override
	public @NonNull IniElement createByte(byte value) {
		return new IniValue(value);
	}
	
	@Override
	public @NonNull IniElement createShort(short value) {
		return new IniValue(value);
	}
	
	@Override
	public @NonNull IniElement createInteger(int value) {
		return new IniValue(value);
	}
	
	@Override
	public @NonNull IniElement createLong(long value) {
		return new IniValue(value);
	}
	
	@Override
	public @NonNull IniElement createFloat(float value) {
		return new IniValue(value);
	}
	
	@Override
	public @NonNull IniElement createDouble(double value) {
		return new IniValue(value);
	}
	
	@Override
	public @NonNull IniElement createString(@Nullable String value) {
		if (value == null) {
			throw new TypeProviderException("Value 'null' is not a valid string");
		}
		return new IniValue(value);
	}
	
	@Override
	public @NonNull IniElement createList(@Nullable List<? extends IniElement> values) {
		throw new TypeProviderException("Ini format does not support lists");
	}
	
	@Override
	public @NonNull IniElement createMap() {
		return new IniSection(GENERATED_SECTION + COUNTER.getAndIncrement());
	}
	
	@Override
	public @NonNull IniElement createMap(@Nullable Map<String, ? extends IniElement> values) {
		if (values == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		
		IniSection section = new IniSection(GENERATED_SECTION + COUNTER.getAndIncrement());
		section.addAll(values);
		return section;
	}
	
	@Override
	public boolean isEmpty(@Nullable IniElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not empty");
		}
		return type == EMPTY_ELEMENT;
	}
	
	@Override
	public boolean isNull(@Nullable IniElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not ini null");
		}
		return type.isIniNull();
	}
	
	@Override
	public @NonNull Boolean getBoolean(@Nullable IniElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a boolean");
		}
		if (!type.isIniValue()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniBoolean()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini boolean");
		}
		return value.getAsBoolean();
	}
	
	@Override
	public @NonNull Byte getByte(@Nullable IniElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a byte");
		}
		if (!type.isIniValue()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniNumber()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini byte");
		}
		return value.getAsByte();
	}
	
	@Override
	public @NonNull Short getShort(@Nullable IniElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a short");
		}
		if (!type.isIniValue()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniNumber()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini short");
		}
		return value.getAsShort();
	}
	
	@Override
	public @NonNull Integer getInteger(@Nullable IniElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not an integer");
		}
		if (!type.isIniValue()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniNumber()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini integer");
		}
		return value.getAsInteger();
	}
	
	@Override
	public @NonNull Long getLong(@Nullable IniElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a long");
		}
		if (!type.isIniValue()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniNumber()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini long");
		}
		return value.getAsLong();
	}
	
	@Override
	public @NonNull Float getFloat(@Nullable IniElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a float");
		}
		if (!type.isIniValue()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (value.isIniString()) {
			throw new TypeProviderException("Ini element '" + type + "' is an ini string, not an ini float");
		}
		return value.getAsFloat();
	}
	
	@Override
	public @NonNull Double getDouble(@Nullable IniElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a double");
		}
		if (!type.isIniValue()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniNumber()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini double");
		}
		return value.getAsDouble();
	}
	
	@Override
	public @NonNull String getString(@Nullable IniElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a string");
		}
		if (!type.isIniValue()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isIniString()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini string");
		}
		return value.getAsString();
	}
	
	@Override
	public @NonNull List<IniElement> getList(@Nullable IniElement type) {
		throw new TypeProviderException("Ini format does not support lists");
	}
	
	@Override
	public @NonNull Map<String, IniElement> getMap(@Nullable IniElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (!type.isIniSection()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini section");
		}
		
		Map<String, IniElement> map = Maps.newLinkedHashMap();
		type.getAsIniSection().forEach(map::put);
		return map;
	}
	
	@Override
	public boolean has(@Nullable IniElement type, @Nullable String key) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		
		if (!type.isIniSection()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini section");
		}
		return type.getAsIniSection().containsKey(key);
	}
	
	@Override
	public @NonNull IniElement get(@Nullable IniElement type, @Nullable String key) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		if (!type.isIniSection()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini section");
		}
		
		IniElement element = type.getAsIniSection().get(key);
		if (element == null) {
			throw new TypeProviderException("Key '" + key + "' does not exist in ini section '" + type + "'");
		}
		return element;
	}
	
	@Override
	public void set(@Nullable IniElement type, @Nullable String key, @Nullable IniElement value) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		if (value == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		
		if (!type.isIniSection()) {
			throw new TypeProviderException("Ini element '" + type + "' is not an ini section");
		}
		type.getAsIniSection().add(key, value);
	}
	
	@Override
	public @UnknownNullability IniElement merge(@Nullable IniElement current, @Nullable IniElement value) {
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
		throw new TypeProviderException("Unable to merge '" + current + "' with '" + value + "'");
	}
}

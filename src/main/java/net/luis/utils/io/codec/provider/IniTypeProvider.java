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
import net.luis.utils.util.result.Result;
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
	private static final IniElement EMPTY_ELEMENT = new IniElement() {
		@Override
		public @NonNull String toString(@NonNull IniConfig config) {
			return "Empty ini element has no string representation";
		}
	};
	
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
	public @NonNull Result<IniElement> createNull() {
		return Result.success(IniNull.INSTANCE);
	}
	
	@Override
	public @NonNull Result<IniElement> createBoolean(boolean value) {
		return Result.success(new IniValue(value));
	}
	
	@Override
	public @NonNull Result<IniElement> createByte(byte value) {
		return Result.success(new IniValue(value));
	}
	
	@Override
	public @NonNull Result<IniElement> createShort(short value) {
		return Result.success(new IniValue(value));
	}
	
	@Override
	public @NonNull Result<IniElement> createInteger(int value) {
		return Result.success(new IniValue(value));
	}
	
	@Override
	public @NonNull Result<IniElement> createLong(long value) {
		return Result.success(new IniValue(value));
	}
	
	@Override
	public @NonNull Result<IniElement> createFloat(float value) {
		return Result.success(new IniValue(value));
	}
	
	@Override
	public @NonNull Result<IniElement> createDouble(double value) {
		return Result.success(new IniValue(value));
	}
	
	@Override
	public @NonNull Result<IniElement> createString(@Nullable String value) {
		if (value == null) {
			return Result.error("Value 'null' is not a valid string");
		}
		return Result.success(new IniValue(value));
	}
	
	@Override
	public @NonNull Result<IniElement> createList(@Nullable List<? extends IniElement> values) {
		return Result.error("Ini format does not support lists");
	}
	
	@Override
	public @NonNull Result<IniElement> createMap() {
		return Result.success(new IniSection(GENERATED_SECTION + COUNTER.getAndIncrement()));
	}
	
	@Override
	public @NonNull Result<IniElement> createMap(@Nullable Map<String, ? extends IniElement> values) {
		if (values == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		
		IniSection section = new IniSection(GENERATED_SECTION + COUNTER.getAndIncrement());
		section.addAll(values);
		return Result.success(section);
	}
	
	@Override
	public @NonNull Result<IniElement> getEmpty(@Nullable IniElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not empty");
		}
		
		if (type != EMPTY_ELEMENT) {
			return Result.error("Ini element '" + type + "' is not an ini null");
		}
		return Result.success(type);
	}
	
	@Override
	public @NonNull Result<Boolean> isNull(@Nullable IniElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not ini null");
		}
		return Result.success(type.isIniNull());
	}
	
	@Override
	public @NonNull Result<Boolean> getBoolean(@Nullable IniElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a boolean");
		}
		
		if (!type.isIniValue()) {
			return Result.error("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isBoolean()) {
			return Result.error("Ini element '" + type + "' is not an ini boolean");
		}
		return Result.success(value.getAsBoolean());
	}
	
	@Override
	public @NonNull Result<Byte> getByte(@Nullable IniElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a byte");
		}
		
		if (!type.isIniValue()) {
			return Result.error("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isNumber()) {
			return Result.error("Ini element '" + type + "' is not an ini byte");
		}
		return Result.success(value.getAsByte());
	}
	
	@Override
	public @NonNull Result<Short> getShort(@Nullable IniElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a short");
		}
		
		if (!type.isIniValue()) {
			return Result.error("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isNumber()) {
			return Result.error("Ini element '" + type + "' is not an ini short");
		}
		return Result.success(value.getAsShort());
	}
	
	@Override
	public @NonNull Result<Integer> getInteger(@Nullable IniElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not an integer");
		}
		
		if (!type.isIniValue()) {
			return Result.error("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isNumber()) {
			return Result.error("Ini element '" + type + "' is not an ini integer");
		}
		return Result.success(value.getAsInteger());
	}
	
	@Override
	public @NonNull Result<Long> getLong(@Nullable IniElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a long");
		}
		
		if (!type.isIniValue()) {
			return Result.error("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isNumber()) {
			return Result.error("Ini element '" + type + "' is not an ini long");
		}
		return Result.success(value.getAsLong());
	}
	
	@Override
	public @NonNull Result<Float> getFloat(@Nullable IniElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a float");
		}
		
		if (!type.isIniValue()) {
			return Result.error("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (value.isString()) {
			return Result.error("Ini element '" + type + "' is an ini string, not an ini float");
		}
		return Result.success(value.getAsFloat());
	}
	
	@Override
	public @NonNull Result<Double> getDouble(@Nullable IniElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a double");
		}
		
		if (!type.isIniValue()) {
			return Result.error("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isNumber()) {
			return Result.error("Ini element '" + type + "' is not an ini double");
		}
		return Result.success(value.getAsDouble());
	}
	
	@Override
	public @NonNull Result<String> getString(@Nullable IniElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a string");
		}
		
		if (!type.isIniValue()) {
			return Result.error("Ini element '" + type + "' is not an ini value");
		}
		
		IniValue value = type.getAsIniValue();
		if (!value.isString()) {
			return Result.error("Ini element '" + type + "' is not an ini string");
		}
		return Result.success(value.getAsString());
	}
	
	@Override
	public @NonNull Result<List<IniElement>> getList(@Nullable IniElement type) {
		return Result.error("Ini format does not support lists");
	}
	
	@Override
	public @NonNull Result<Map<String, IniElement>> getMap(@Nullable IniElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		
		if (!type.isIniSection()) {
			return Result.error("Ini element '" + type + "' is not an ini section");
		}
		
		Map<String, IniElement> map = Maps.newLinkedHashMap();
		type.getAsIniSection().forEach(map::put);
		return Result.success(map);
	}
	
	@Override
	public @NonNull Result<Boolean> has(@Nullable IniElement type, @Nullable String key) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		
		if (key == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (!type.isIniSection()) {
			return Result.error("Ini element '" + type + "' is not an ini section");
		}
		return Result.success(type.getAsIniSection().containsKey(key));
	}
	
	@Override
	public @NonNull Result<IniElement> get(@Nullable IniElement type, @Nullable String key) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		
		if (key == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (!type.isIniSection()) {
			return Result.error("Ini element '" + type + "' is not an ini section");
		}
		return Result.success(type.getAsIniSection().get(key));
	}
	
	@Override
	public @NonNull Result<IniElement> set(@Nullable IniElement type, @Nullable String key, @Nullable IniElement value) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		
		if (key == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (value == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (!type.isIniSection()) {
			return Result.error("Ini element '" + type + "' is not an ini section");
		}
		
		IniSection section = type.getAsIniSection();
		section.add(key, value);
		return Result.success(section);
	}
	
	@Override
	public @NonNull Result<IniElement> merge(@Nullable IniElement current, @Nullable IniElement value) {
		if (current == null) {
			return Result.success(value);
		}
		
		if (value == null) {
			return Result.success(current);
		}
		
		if (current == EMPTY_ELEMENT || current.isIniNull()) {
			return Result.success(value);
		}
		
		if (value == EMPTY_ELEMENT || value.isIniNull()) {
			return Result.success(current);
		}
		
		if (current.isIniSection() && value.isIniSection()) {
			IniSection section = current.getAsIniSection();
			section.addAll(value.getAsIniSection());
			return Result.success(section);
		}
		return Result.error("Unable to merge '" + current + "' with '" + value + "'");
	}
}

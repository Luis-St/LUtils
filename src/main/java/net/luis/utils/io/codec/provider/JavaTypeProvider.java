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

package net.luis.utils.io.codec.provider;

import com.google.common.collect.Maps;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Type provider that uses standard Java types.<br>
 * This class is a singleton and should be accessed via the {@link #INSTANCE} field.<br>
 *
 * @author Luis-St
 */
public final class JavaTypeProvider implements TypeProvider<Object> {
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final JavaTypeProvider INSTANCE = new JavaTypeProvider();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private JavaTypeProvider() {}
	
	@Override
	public @NotNull Object empty() {
		return new Object();
	}
	
	@Override
	public @NotNull Result<Object> createNull() {
		return Result.success(null);
	}
	
	@Override
	public @NotNull Result<Object> createBoolean(boolean value) {
		return Result.success(value);
	}
	
	@Override
	public @NotNull Result<Object> createByte(byte value) {
		return Result.success(value);
	}
	
	@Override
	public @NotNull Result<Object> createShort(short value) {
		return Result.success(value);
	}
	
	@Override
	public @NotNull Result<Object> createInteger(int value) {
		return Result.success(value);
	}
	
	@Override
	public @NotNull Result<Object> createLong(long value) {
		return Result.success(value);
	}
	
	@Override
	public @NotNull Result<Object> createFloat(float value) {
		return Result.success(value);
	}
	
	@Override
	public @NotNull Result<Object> createDouble(double value) {
		return Result.success(value);
	}
	
	@Override
	public @NotNull Result<Object> createString(@NotNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		return Result.success(value);
	}
	
	@Override
	public @NotNull Result<Object> createList(@NotNull List<?> values) {
		Objects.requireNonNull(values, "Values must not be null");
		return Result.success(values);
	}
	
	@Override
	public @NotNull Result<Object> createMap() {
		return Result.success(Maps.newLinkedHashMap());
	}
	
	@Override
	public @NotNull Result<Object> createMap(@NotNull Map<String, ?> values) {
		Objects.requireNonNull(values, "Values must not be null");
		return Result.success(values);
	}
	
	@Override
	public @NotNull Result<Object> getEmpty(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");

		if (type.getClass() == Object.class) {
			return Result.success(type);
		}
		return Result.error("Object '" + type + "' is not an empty object");
	}

	@Override
	public @NotNull Result<Boolean> isNull(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");
		return Result.success(false);
	}
	
	@Override
	public @NotNull Result<Boolean> getBoolean(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		if (type instanceof Boolean booleanValue) {
			return Result.success(booleanValue);
		}
		return Result.error("Object '" + type + "' is not a boolean");
	}
	
	@Override
	public @NotNull Result<Byte> getByte(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		if (type instanceof Byte byteValue) {
			return Result.success(byteValue);
		}
		return Result.error("Object '" + type + "' is not a byte");
	}
	
	@Override
	public @NotNull Result<Short> getShort(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		if (type instanceof Short shortValue) {
			return Result.success(shortValue);
		}
		return Result.error("Object '" + type + "' is not a short");
	}
	
	@Override
	public @NotNull Result<Integer> getInteger(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		if (type instanceof Integer intValue) {
			return Result.success(intValue);
		}
		return Result.error("Object '" + type + "' is not an integer");
	}
	
	@Override
	public @NotNull Result<Long> getLong(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		if (type instanceof Long longValue) {
			return Result.success(longValue);
		}
		return Result.error("Object '" + type + "' is not a long");
	}
	
	@Override
	public @NotNull Result<Float> getFloat(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		if (type instanceof Float floatValue) {
			return Result.success(floatValue);
		}
		return Result.error("Object '" + type + "' is not a float");
	}
	
	@Override
	public @NotNull Result<Double> getDouble(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		if (type instanceof Double doubleValue) {
			return Result.success(doubleValue);
		}
		return Result.error("Object '" + type + "' is not a double");
	}
	
	@Override
	public @NotNull Result<String> getString(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		if (type instanceof String stringValue) {
			return Result.success(stringValue);
		}
		return Result.error("Object '" + type + "' is not a string");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NotNull Result<List<Object>> getList(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		if (type instanceof List<?> list) {
			return Result.success((List<Object>) list);
		}
		return Result.error("Object '" + type + "' is not a list");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NotNull Result<Map<String, Object>> getMap(@NotNull Object type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		if (type instanceof Map<?, ?> map) {
			return Result.success((Map<String, Object>) map);
		}
		return Result.error("Object '" + type + "' is not a map");
	}
	
	@Override
	public @NotNull Result<Boolean> has(@NotNull Object type, @NotNull String key) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		
		if (type instanceof Map<?, ?> map) {
			return Result.success(map.containsKey(key));
		}
		return Result.error("Object '" + type + "' is not a map");
	}
	
	@Override
	public @NotNull Result<Object> get(@NotNull Object type, @NotNull String key) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		
		if (type instanceof Map<?, ?> map) {
			return Result.success(map.get(key));
		}
		return Result.error("Object '" + type + "' is not a map");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NotNull Result<Object> set(@NotNull Object type, @NotNull String key, @NotNull Object value) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		
		if (type instanceof Map<?, ?> map) {
			return Result.success(((Map<String, Object>) map).put(key, value));
		}
		return Result.error("Object '" + type + "' is not a map");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NotNull Result<Object> merge(@Nullable Object current, @Nullable Object value) {
		if (current == null) {
			return Result.success(value);
		}
		if (value == null) {
			return Result.success(current);
		}
		
		if (current.getClass() == Object.class) {
			return Result.success(value);
		}
		
		if (current instanceof List<?> currentList && value instanceof List<?> valueList) {
			((List<Object>) currentList).addAll(valueList);
			return Result.success(currentList);
		}
		
		if (current instanceof Map<?, ?> currentMap && value instanceof Map<?, ?> valueMap) {
			((Map<String, Object>) currentMap).putAll((Map<String, Object>) valueMap);
			return Result.success(currentMap);
		}
		return Result.error("Unable to merge '" + current + "' with '" + value + "'");
	}
}

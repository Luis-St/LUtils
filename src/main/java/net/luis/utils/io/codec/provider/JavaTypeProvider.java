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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

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
	public @NonNull Object empty() {
		return new Object();
	}
	
	@Override
	public @NonNull Result<Object> createNull() {
		return Result.success(null);
	}
	
	@Override
	public @NonNull Result<Object> createBoolean(boolean value) {
		return Result.success(value);
	}
	
	@Override
	public @NonNull Result<Object> createByte(byte value) {
		return Result.success(value);
	}
	
	@Override
	public @NonNull Result<Object> createShort(short value) {
		return Result.success(value);
	}
	
	@Override
	public @NonNull Result<Object> createInteger(int value) {
		return Result.success(value);
	}
	
	@Override
	public @NonNull Result<Object> createLong(long value) {
		return Result.success(value);
	}
	
	@Override
	public @NonNull Result<Object> createFloat(float value) {
		return Result.success(value);
	}
	
	@Override
	public @NonNull Result<Object> createDouble(double value) {
		return Result.success(value);
	}
	
	@Override
	public @NonNull Result<Object> createString(@Nullable String value) {
		if (value == null) {
			return Result.error("Value 'null' is not a valid string");
		}
		return Result.success(value);
	}
	
	@Override
	public @NonNull Result<Object> createList(@Nullable List<?> values) {
		if (values == null) {
			return Result.error("Value 'null' is not a valid list");
		}
		return Result.success(values);
	}
	
	@Override
	public @NonNull Result<Object> createMap() {
		return Result.success(Maps.newLinkedHashMap());
	}
	
	@Override
	public @NonNull Result<Object> createMap(@Nullable Map<String, ?> values) {
		if (values == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		
		return Result.success(values);
	}
	
	@Override
	public @NonNull Result<Object> getEmpty(@Nullable Object type) {
		if (type == null) {
			return Result.error("Value 'null' is not an empty object");
		}
		
		if (type.getClass() == Object.class) {
			return Result.success(type);
		}
		return Result.error("Object '" + type + "' is not an empty object");
	}
	
	@Override
	public @NonNull Result<Boolean> isNull(@Nullable Object type) {
		return Result.success(type == null);
	}
	
	@Override
	public @NonNull Result<Boolean> getBoolean(@Nullable Object type) {
		if (type == null) {
			return Result.error("Value 'null' is not a boolean");
		}
		
		if (type instanceof Boolean booleanValue) {
			return Result.success(booleanValue);
		}
		return Result.error("Object '" + type + "' is not a boolean");
	}
	
	@Override
	public @NonNull Result<Byte> getByte(@Nullable Object type) {
		if (type == null) {
			return Result.error("Value 'null' is not a byte");
		}
		
		if (type instanceof Byte byteValue) {
			return Result.success(byteValue);
		}
		return Result.error("Object '" + type + "' is not a byte");
	}
	
	@Override
	public @NonNull Result<Short> getShort(@Nullable Object type) {
		if (type == null) {
			return Result.error("Value 'null' is not a short");
		}
		
		if (type instanceof Short shortValue) {
			return Result.success(shortValue);
		}
		return Result.error("Object '" + type + "' is not a short");
	}
	
	@Override
	public @NonNull Result<Integer> getInteger(@Nullable Object type) {
		if (type == null) {
			return Result.error("Value 'null' is not an integer");
		}
		
		if (type instanceof Integer intValue) {
			return Result.success(intValue);
		}
		return Result.error("Object '" + type + "' is not an integer");
	}
	
	@Override
	public @NonNull Result<Long> getLong(@Nullable Object type) {
		if (type == null) {
			return Result.error("Value 'null' is not a long");
		}
		
		if (type instanceof Long longValue) {
			return Result.success(longValue);
		}
		return Result.error("Object '" + type + "' is not a long");
	}
	
	@Override
	public @NonNull Result<Float> getFloat(@Nullable Object type) {
		if (type == null) {
			return Result.error("Value 'null' is not a float");
		}
		
		if (type instanceof Float floatValue) {
			return Result.success(floatValue);
		}
		return Result.error("Object '" + type + "' is not a float");
	}
	
	@Override
	public @NonNull Result<Double> getDouble(@Nullable Object type) {
		if (type == null) {
			return Result.error("Value 'null' is not a double");
		}
		
		if (type instanceof Double doubleValue) {
			return Result.success(doubleValue);
		}
		return Result.error("Object '" + type + "' is not a double");
	}
	
	@Override
	public @NonNull Result<String> getString(@Nullable Object type) {
		if (type == null) {
			return Result.error("Value 'null' is not a string");
		}
		
		if (type instanceof String stringValue) {
			return Result.success(stringValue);
		}
		return Result.error("Object '" + type + "' is not a string");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Result<List<Object>> getList(@Nullable Object type) {
		if (type == null) {
			return Result.error("Value 'null' is not a list");
		}
		
		if (type instanceof List<?> list) {
			return Result.success((List<Object>) list);
		}
		return Result.error("Object '" + type + "' is not a list");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Result<Map<String, Object>> getMap(@Nullable Object type) {
		if (type == null) {
			return Result.error("Value 'null' is not a map");
		}
		
		if (type instanceof Map<?, ?> map) {
			return Result.success((Map<String, Object>) map);
		}
		return Result.error("Object '" + type + "' is not a map");
	}
	
	@Override
	public @NonNull Result<Boolean> has(@Nullable Object type, @Nullable String key) {
		if (type == null) {
			return Result.error("Value 'null' is not a map");
		}
		if (key == null) {
			return Result.error("Value 'null' is not a valid key");
		}
		
		if (type instanceof Map<?, ?> map) {
			return Result.success(map.containsKey(key));
		}
		return Result.error("Object '" + type + "' is not a map");
	}
	
	@Override
	public @NonNull Result<Object> get(@Nullable Object type, @Nullable String key) {
		if (type == null) {
			return Result.error("Value 'null' is not a map");
		}
		if (key == null) {
			return Result.error("Value 'null' is not a valid key");
		}
		
		if (type instanceof Map<?, ?> map) {
			return Result.success(map.get(key));
		}
		return Result.error("Object '" + type + "' is not a map");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Result<Object> set(@Nullable Object type, @Nullable String key, @Nullable Object value) {
		if (type == null) {
			return Result.error("Value 'null' is not a map");
		}
		if (key == null) {
			return Result.error("Value 'null' is not a valid key");
		}
		
		if (type instanceof Map<?, ?> map) {
			return Result.success(((Map<String, Object>) map).put(key, value));
		}
		return Result.error("Object '" + type + "' is not a map");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Result<Object> merge(@Nullable Object current, @Nullable Object value) {
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

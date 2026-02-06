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
import org.jetbrains.annotations.UnknownNullability;
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
	 * An empty java element instance.<br>
	 * Used for internal purposes only.<br>
	 * The java element has no string representation.<br>
	 */
	private static final Object EMPTY_ELEMENT = new Object() {
		@Override
		public String toString() {
			return "Empty java element has no string representation";
		}
	};
	
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
		return EMPTY_ELEMENT;
	}
	
	@Override
	@SuppressWarnings({ "ReturnOfNull", "DataFlowIssue" })
	public @NonNull Object createNull() {
		return null;
	}
	
	@Override
	public @NonNull Object createBoolean(boolean value) {
		return value;
	}
	
	@Override
	public @NonNull Object createByte(byte value) {
		return value;
	}
	
	@Override
	public @NonNull Object createShort(short value) {
		return value;
	}
	
	@Override
	public @NonNull Object createInteger(int value) {
		return value;
	}
	
	@Override
	public @NonNull Object createLong(long value) {
		return value;
	}
	
	@Override
	public @NonNull Object createFloat(float value) {
		return value;
	}
	
	@Override
	public @NonNull Object createDouble(double value) {
		return value;
	}
	
	@Override
	public @NonNull Object createString(@Nullable String value) {
		if (value == null) {
			throw new TypeProviderException("Value 'null' is not a valid string");
		}
		return value;
	}
	
	@Override
	public @NonNull Object createList(@Nullable List<?> values) {
		if (values == null) {
			throw new TypeProviderException("Value 'null' is not a valid list");
		}
		return values;
	}
	
	@Override
	public @NonNull Object createMap() {
		return Maps.newLinkedHashMap();
	}
	
	@Override
	public @NonNull Object createMap(@Nullable Map<String, ?> values) {
		if (values == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		return values;
	}
	
	@Override
	public boolean isEmpty(@Nullable Object type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not an empty object");
		}
		return type == EMPTY_ELEMENT;
	}
	
	@Override
	public boolean isNull(@Nullable Object type) {
		return type == null;
	}
	
	@Override
	public @NonNull Boolean getBoolean(@Nullable Object type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a boolean");
		}
		
		if (type instanceof Boolean booleanValue) {
			return booleanValue;
		}
		throw new TypeProviderException("Object '" + type + "' is not a boolean");
	}
	
	@Override
	public @NonNull Byte getByte(@Nullable Object type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a byte");
		}
		
		if (type instanceof Byte byteValue) {
			return byteValue;
		}
		throw new TypeProviderException("Object '" + type + "' is not a byte");
	}
	
	@Override
	public @NonNull Short getShort(@Nullable Object type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a short");
		}
		
		if (type instanceof Short shortValue) {
			return shortValue;
		}
		throw new TypeProviderException("Object '" + type + "' is not a short");
	}
	
	@Override
	public @NonNull Integer getInteger(@Nullable Object type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not an integer");
		}
		
		if (type instanceof Integer intValue) {
			return intValue;
		}
		throw new TypeProviderException("Object '" + type + "' is not an integer");
	}
	
	@Override
	public @NonNull Long getLong(@Nullable Object type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a long");
		}
		
		if (type instanceof Long longValue) {
			return longValue;
		}
		throw new TypeProviderException("Object '" + type + "' is not a long");
	}
	
	@Override
	public @NonNull Float getFloat(@Nullable Object type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a float");
		}
		
		if (type instanceof Float floatValue) {
			return floatValue;
		}
		throw new TypeProviderException("Object '" + type + "' is not a float");
	}
	
	@Override
	public @NonNull Double getDouble(@Nullable Object type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a double");
		}
		
		if (type instanceof Double doubleValue) {
			return doubleValue;
		}
		throw new TypeProviderException("Object '" + type + "' is not a double");
	}
	
	@Override
	public @NonNull String getString(@Nullable Object type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a string");
		}
		
		if (type instanceof String stringValue) {
			return stringValue;
		}
		throw new TypeProviderException("Object '" + type + "' is not a string");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull List<Object> getList(@Nullable Object type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a list");
		}
		
		if (type instanceof List<?> list) {
			return (List<Object>) list;
		}
		throw new TypeProviderException("Object '" + type + "' is not a list");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Map<String, Object> getMap(@Nullable Object type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a map");
		}
		
		if (type instanceof Map<?, ?> map) {
			return (Map<String, Object>) map;
		}
		throw new TypeProviderException("Object '" + type + "' is not a map");
	}
	
	@Override
	public boolean has(@Nullable Object type, @Nullable String key) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not a valid key");
		}
		
		if (type instanceof Map<?, ?> map) {
			return map.containsKey(key);
		}
		throw new TypeProviderException("Object '" + type + "' is not a map");
	}
	
	@Override
	public @NonNull Object get(@Nullable Object type, @Nullable String key) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not a valid key");
		}
		
		if (type instanceof Map<?, ?> map) {
			return map.get(key);
		}
		throw new TypeProviderException("Object '" + type + "' is not a map");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void set(@Nullable Object type, @Nullable String key, @Nullable Object value) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not a valid key");
		}
		
		if (type instanceof Map<?, ?> map) {
			((Map<String, Object>) map).put(key, value);
		}
		throw new TypeProviderException("Object '" + type + "' is not a map");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @UnknownNullability Object merge(@Nullable Object current, @Nullable Object value) {
		if (current == null) {
			return value;
		}
		if (value == null) {
			return current;
		}
		
		if (current.getClass() == Object.class) {
			return value;
		}
		
		if (current instanceof List<?> currentList && value instanceof List<?> valueList) {
			((List<Object>) currentList).addAll(valueList);
			return currentList;
		}
		
		if (current instanceof Map<?, ?> currentMap && value instanceof Map<?, ?> valueMap) {
			((Map<String, Object>) currentMap).putAll((Map<String, Object>) valueMap);
			return currentMap;
		}
		throw new TypeProviderException("Unable to merge '" + current + "' with '" + value + "'");
	}
}

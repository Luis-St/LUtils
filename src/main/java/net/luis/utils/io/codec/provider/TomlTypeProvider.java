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
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

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
	private static final TomlElement EMPTY_ELEMENT = new TomlElement() {
		@Override
		public @NonNull String toString(@NonNull TomlConfig config) {
			return "Empty toml element has no string representation";
		}
	};
	
	/**
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
	public @NonNull Result<TomlElement> createNull() {
		return Result.success(TomlNull.INSTANCE);
	}
	
	@Override
	public @NonNull Result<TomlElement> createBoolean(boolean value) {
		return Result.success(new TomlValue(value));
	}
	
	@Override
	public @NonNull Result<TomlElement> createByte(byte value) {
		return Result.success(new TomlValue(value));
	}
	
	@Override
	public @NonNull Result<TomlElement> createShort(short value) {
		return Result.success(new TomlValue(value));
	}
	
	@Override
	public @NonNull Result<TomlElement> createInteger(int value) {
		return Result.success(new TomlValue(value));
	}
	
	@Override
	public @NonNull Result<TomlElement> createLong(long value) {
		return Result.success(new TomlValue(value));
	}
	
	@Override
	public @NonNull Result<TomlElement> createFloat(float value) {
		return Result.success(new TomlValue(value));
	}
	
	@Override
	public @NonNull Result<TomlElement> createDouble(double value) {
		return Result.success(new TomlValue(value));
	}
	
	@Override
	public @NonNull Result<TomlElement> createString(@Nullable String value) {
		if (value == null) {
			return Result.error("Value 'null' is not a valid string");
		}
		
		return Result.success(new TomlValue(value));
	}
	
	@Override
	public @NonNull Result<TomlElement> createList(@Nullable List<? extends TomlElement> values) {
		if (values == null) {
			return Result.error("Value 'null' is not a valid list");
		}
		
		return Result.success(new TomlArray(values));
	}
	
	@Override
	public @NonNull Result<TomlElement> createMap() {
		return Result.success(new TomlTable());
	}
	
	@Override
	public @NonNull Result<TomlElement> createMap(@Nullable Map<String, ? extends TomlElement> values) {
		if (values == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		
		return Result.success(new TomlTable(values));
	}
	
	@Override
	public @NonNull Result<TomlElement> getEmpty(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not empty");
		}
		
		if (type != EMPTY_ELEMENT) {
			return Result.error("Toml element '" + type + "' is not a toml null");
		}
		return Result.success(type);
	}
	
	@Override
	public @NonNull Result<Boolean> isNull(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not toml null");
		}
		
		return Result.success(type.isTomlNull());
	}
	
	@Override
	public @NonNull Result<Boolean> getBoolean(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a boolean");
		}
		
		if (!type.isTomlValue()) {
			return Result.error("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isBoolean()) {
			return Result.error("Toml element '" + type + "' is not a toml boolean");
		}
		return Result.success(value.getAsBoolean());
	}
	
	@Override
	public @NonNull Result<Byte> getByte(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a byte");
		}
		
		if (!type.isTomlValue()) {
			return Result.error("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isNumber()) {
			return Result.error("Toml element '" + type + "' is not a toml byte");
		}
		return Result.success(value.getAsByte());
	}
	
	@Override
	public @NonNull Result<Short> getShort(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a short");
		}
		
		if (!type.isTomlValue()) {
			return Result.error("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isNumber()) {
			return Result.error("Toml element '" + type + "' is not a toml short");
		}
		return Result.success(value.getAsShort());
	}
	
	@Override
	public @NonNull Result<Integer> getInteger(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not an integer");
		}
		
		if (!type.isTomlValue()) {
			return Result.error("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isNumber()) {
			return Result.error("Toml element '" + type + "' is not a toml integer");
		}
		return Result.success(value.getAsInteger());
	}
	
	@Override
	public @NonNull Result<Long> getLong(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a long");
		}
		
		if (!type.isTomlValue()) {
			return Result.error("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isNumber()) {
			return Result.error("Toml element '" + type + "' is not a toml long");
		}
		return Result.success(value.getAsLong());
	}
	
	@Override
	public @NonNull Result<Float> getFloat(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a float");
		}
		
		if (!type.isTomlValue()) {
			return Result.error("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isNumber()) {
			return Result.error("Toml element '" + type + "' is a toml string, not a toml float");
		}
		return Result.success(value.getAsFloat());
	}
	
	@Override
	public @NonNull Result<Double> getDouble(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a double");
		}
		
		if (!type.isTomlValue()) {
			return Result.error("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isNumber()) {
			return Result.error("Toml element '" + type + "' is not a toml double");
		}
		return Result.success(value.getAsDouble());
	}
	
	@Override
	public @NonNull Result<String> getString(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a string");
		}
		
		if (!type.isTomlValue()) {
			return Result.error("Toml element '" + type + "' is not a toml value");
		}
		
		TomlValue value = type.getAsTomlValue();
		if (!value.isString()) {
			return Result.error("Toml element '" + type + "' is not a toml string");
		}
		return Result.success(value.getAsString());
	}
	
	@Override
	public @NonNull Result<List<TomlElement>> getList(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid list");
		}
		
		if (!type.isTomlArray()) {
			return Result.error("Toml element '" + type + "' is not a toml array");
		}
		return Result.success(type.getAsTomlArray().getElements());
	}
	
	@Override
	public @NonNull Result<Map<String, TomlElement>> getMap(@Nullable TomlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		
		if (!type.isTomlTable()) {
			return Result.error("Toml element '" + type + "' is not a toml table");
		}
		
		Map<String, TomlElement> map = Maps.newLinkedHashMap();
		type.getAsTomlTable().forEach(map::put);
		return Result.success(map);
	}
	
	@Override
	public @NonNull Result<Boolean> has(@Nullable TomlElement type, @Nullable String key) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		if (key == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (!type.isTomlTable()) {
			return Result.error("Toml element '" + type + "' is not a toml table");
		}
		return Result.success(type.getAsTomlTable().containsKey(key));
	}
	
	@Override
	public @NonNull Result<TomlElement> get(@Nullable TomlElement type, @Nullable String key) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		if (key == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (!type.isTomlTable()) {
			return Result.error("Toml element '" + type + "' is not a toml table");
		}
		return Result.success(type.getAsTomlTable().get(key));
	}
	
	@Override
	public @NonNull Result<TomlElement> set(@Nullable TomlElement type, @Nullable String key, @Nullable TomlElement value) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		if (key == null) {
			return Result.error("Value 'null' is not valid");
		}
		if (value == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (!type.isTomlTable()) {
			return Result.error("Toml element '" + type + "' is not a toml table");
		}
		return Result.success(type.getAsTomlTable().add(key, value));
	}
	
	@Override
	public @NonNull Result<TomlElement> merge(@Nullable TomlElement current, @Nullable TomlElement value) {
		if (current == null) {
			return Result.success(value);
		}
		if (value == null) {
			return Result.success(current);
		}
		
		if (current == EMPTY_ELEMENT || current.isTomlNull()) {
			return Result.success(value);
		}
		if (value == EMPTY_ELEMENT || value.isTomlNull()) {
			return Result.success(current);
		}
		
		if (current.isTomlArray() && value.isTomlArray()) {
			TomlArray array = current.getAsTomlArray();
			array.addAll(value.getAsTomlArray());
			return Result.success(array);
		}
		
		if (current.isTomlTable() && value.isTomlTable()) {
			TomlTable table = current.getAsTomlTable();
			table.addAll(value.getAsTomlTable());
			return Result.success(table);
		}
		return Result.error("Unable to merge '" + current + "' with '" + value + "'");
	}
}

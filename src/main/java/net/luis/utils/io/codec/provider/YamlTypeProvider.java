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
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

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
	private static final YamlElement EMPTY_ELEMENT = new YamlElement() {
		@Override
		public @NonNull String toString(@NonNull YamlConfig config) {
			return "Empty yaml element has no string representation";
		}
	};
	
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
	public @NonNull Result<YamlElement> createNull() {
		return Result.success(YamlNull.INSTANCE);
	}
	
	@Override
	public @NonNull Result<YamlElement> createBoolean(boolean value) {
		return Result.success(new YamlScalar(value));
	}
	
	@Override
	public @NonNull Result<YamlElement> createByte(byte value) {
		return Result.success(new YamlScalar(value));
	}
	
	@Override
	public @NonNull Result<YamlElement> createShort(short value) {
		return Result.success(new YamlScalar(value));
	}
	
	@Override
	public @NonNull Result<YamlElement> createInteger(int value) {
		return Result.success(new YamlScalar(value));
	}
	
	@Override
	public @NonNull Result<YamlElement> createLong(long value) {
		return Result.success(new YamlScalar(value));
	}
	
	@Override
	public @NonNull Result<YamlElement> createFloat(float value) {
		return Result.success(new YamlScalar(value));
	}
	
	@Override
	public @NonNull Result<YamlElement> createDouble(double value) {
		return Result.success(new YamlScalar(value));
	}
	
	@Override
	public @NonNull Result<YamlElement> createString(@Nullable String value) {
		if (value == null) {
			return Result.error("Value 'null' is not a valid string");
		}
		
		return Result.success(new YamlScalar(value));
	}
	
	@Override
	public @NonNull Result<YamlElement> createList(@Nullable List<? extends YamlElement> values) {
		if (values == null) {
			return Result.error("Value 'null' is not a valid list");
		}
		
		return Result.success(new YamlSequence(values));
	}
	
	@Override
	public @NonNull Result<YamlElement> createMap() {
		return Result.success(new YamlMapping());
	}
	
	@Override
	public @NonNull Result<YamlElement> createMap(@Nullable Map<String, ? extends YamlElement> values) {
		if (values == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		
		return Result.success(new YamlMapping(values));
	}
	
	@Override
	public @NonNull Result<YamlElement> getEmpty(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not empty");
		}
		
		if (type != EMPTY_ELEMENT) {
			return Result.error("Yaml element '" + type + "' is not a yaml null");
		}
		return Result.success(type);
	}
	
	@Override
	public @NonNull Result<Boolean> isNull(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not yaml null");
		}
		
		return Result.success(type.isYamlNull());
	}
	
	@Override
	public @NonNull Result<Boolean> getBoolean(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a boolean");
		}
		
		if (!type.isYamlScalar()) {
			return Result.error("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlBoolean()) {
			return Result.error("Yaml element '" + type + "' is not a yaml boolean");
		}
		return Result.success(scalar.getAsBoolean());
	}
	
	@Override
	public @NonNull Result<Byte> getByte(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a byte");
		}
		
		if (!type.isYamlScalar()) {
			return Result.error("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			return Result.error("Yaml element '" + type + "' is not a yaml byte");
		}
		return Result.success(scalar.getAsByte());
	}
	
	@Override
	public @NonNull Result<Short> getShort(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a short");
		}
		
		if (!type.isYamlScalar()) {
			return Result.error("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			return Result.error("Yaml element '" + type + "' is not a yaml short");
		}
		return Result.success(scalar.getAsShort());
	}
	
	@Override
	public @NonNull Result<Integer> getInteger(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not an integer");
		}
		
		if (!type.isYamlScalar()) {
			return Result.error("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			return Result.error("Yaml element '" + type + "' is not a yaml integer");
		}
		return Result.success(scalar.getAsInteger());
	}
	
	@Override
	public @NonNull Result<Long> getLong(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a long");
		}
		
		if (!type.isYamlScalar()) {
			return Result.error("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			return Result.error("Yaml element '" + type + "' is not a yaml long");
		}
		return Result.success(scalar.getAsLong());
	}
	
	@Override
	public @NonNull Result<Float> getFloat(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a float");
		}
		
		if (!type.isYamlScalar()) {
			return Result.error("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (scalar.isYamlString()) {
			return Result.error("Yaml element '" + type + "' is a yaml string, not a yaml float");
		}
		return Result.success(scalar.getAsFloat());
	}
	
	@Override
	public @NonNull Result<Double> getDouble(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a double");
		}
		
		if (!type.isYamlScalar()) {
			return Result.error("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			return Result.error("Yaml element '" + type + "' is not a yaml double");
		}
		return Result.success(scalar.getAsDouble());
	}
	
	@Override
	public @NonNull Result<String> getString(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a string");
		}
		
		if (!type.isYamlScalar()) {
			return Result.error("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlString()) {
			return Result.error("Yaml element '" + type + "' is not a yaml string");
		}
		return Result.success(scalar.getAsString());
	}
	
	@Override
	public @NonNull Result<List<YamlElement>> getList(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid list");
		}
		
		if (!type.isYamlSequence()) {
			return Result.error("Yaml element '" + type + "' is not a yaml sequence");
		}
		return Result.success(type.getAsYamlSequence().getElements());
	}
	
	@Override
	public @NonNull Result<Map<String, YamlElement>> getMap(@Nullable YamlElement type) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		
		if (!type.isYamlMapping()) {
			return Result.error("Yaml element '" + type + "' is not a yaml mapping");
		}
		
		Map<String, YamlElement> map = Maps.newLinkedHashMap();
		type.getAsYamlMapping().forEach(map::put);
		return Result.success(map);
	}
	
	@Override
	public @NonNull Result<Boolean> has(@Nullable YamlElement type, @Nullable String key) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		if (key == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (!type.isYamlMapping()) {
			return Result.error("Yaml element '" + type + "' is not a yaml mapping");
		}
		return Result.success(type.getAsYamlMapping().containsKey(key));
	}
	
	@Override
	public @NonNull Result<YamlElement> get(@Nullable YamlElement type, @Nullable String key) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		if (key == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (!type.isYamlMapping()) {
			return Result.error("Yaml element '" + type + "' is not a yaml mapping");
		}
		return Result.success(type.getAsYamlMapping().get(key));
	}
	
	@Override
	public @NonNull Result<YamlElement> set(@Nullable YamlElement type, @Nullable String key, @Nullable YamlElement value) {
		if (type == null) {
			return Result.error("Value 'null' is not a valid map");
		}
		if (key == null) {
			return Result.error("Value 'null' is not valid");
		}
		if (value == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (!type.isYamlMapping()) {
			return Result.error("Yaml element '" + type + "' is not a yaml mapping");
		}
		YamlMapping mapping = type.getAsYamlMapping();
		mapping.add(key, value);
		return Result.success(mapping);
	}
	
	@Override
	public @NonNull Result<YamlElement> merge(@Nullable YamlElement current, @Nullable YamlElement value) {
		if (current == null) {
			return Result.success(value);
		}
		if (value == null) {
			return Result.success(current);
		}
		
		if (current == EMPTY_ELEMENT || current.isYamlNull()) {
			return Result.success(value);
		}
		if (value == EMPTY_ELEMENT || value.isYamlNull()) {
			return Result.success(current);
		}
		
		if (current.isYamlSequence() && value.isYamlSequence()) {
			YamlSequence sequence = current.getAsYamlSequence();
			sequence.addAll(value.getAsYamlSequence());
			return Result.success(sequence);
		}
		
		if (current.isYamlMapping() && value.isYamlMapping()) {
			YamlMapping mapping = current.getAsYamlMapping();
			mapping.addAll(value.getAsYamlMapping());
			return Result.success(mapping);
		}
		return Result.error("Unable to merge '" + current + "' with '" + value + "'");
	}
}

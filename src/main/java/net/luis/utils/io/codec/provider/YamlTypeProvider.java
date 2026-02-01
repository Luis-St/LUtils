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
import org.jetbrains.annotations.UnknownNullability;
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
	public @NonNull YamlElement createNull() {
		return YamlNull.INSTANCE;
	}
	
	@Override
	public @NonNull YamlElement createBoolean(boolean value) {
		return new YamlScalar(value);
	}
	
	@Override
	public @NonNull YamlElement createByte(byte value) {
		return new YamlScalar(value);
	}
	
	@Override
	public @NonNull YamlElement createShort(short value) {
		return new YamlScalar(value);
	}
	
	@Override
	public @NonNull YamlElement createInteger(int value) {
		return new YamlScalar(value);
	}
	
	@Override
	public @NonNull YamlElement createLong(long value) {
		return new YamlScalar(value);
	}
	
	@Override
	public @NonNull YamlElement createFloat(float value) {
		return new YamlScalar(value);
	}
	
	@Override
	public @NonNull YamlElement createDouble(double value) {
		return new YamlScalar(value);
	}
	
	@Override
	public @NonNull YamlElement createString(@Nullable String value) {
		if (value == null) {
			throw new TypeProviderException("Value 'null' is not a valid string");
		}
		return new YamlScalar(value);
	}
	
	@Override
	public @NonNull YamlElement createList(@Nullable List<? extends YamlElement> values) {
		if (values == null) {
			throw new TypeProviderException("Value 'null' is not a valid list");
		}
		return new YamlSequence(values);
	}
	
	@Override
	public @NonNull YamlElement createMap() {
		return new YamlMapping();
	}
	
	@Override
	public @NonNull YamlElement createMap(@Nullable Map<String, ? extends YamlElement> values) {
		if (values == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		return new YamlMapping(values);
	}
	
	@Override
	public boolean isEmpty(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not empty");
		}
		return type == EMPTY_ELEMENT;
	}
	
	@Override
	public boolean isNull(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not yaml null");
		}
		return type.isYamlNull();
	}
	
	@Override
	public @NonNull Boolean getBoolean(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a boolean");
		}
		if (!type.isYamlScalar()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlBoolean()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml boolean");
		}
		return scalar.getAsBoolean();
	}
	
	@Override
	public @NonNull Byte getByte(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a byte");
		}
		if (!type.isYamlScalar()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml byte");
		}
		return scalar.getAsByte();
	}
	
	@Override
	public @NonNull Short getShort(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a short");
		}
		if (!type.isYamlScalar()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml short");
		}
		return scalar.getAsShort();
	}
	
	@Override
	public @NonNull Integer getInteger(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not an integer");
		}
		if (!type.isYamlScalar()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml integer");
		}
		return scalar.getAsInteger();
	}
	
	@Override
	public @NonNull Long getLong(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a long");
		}
		if (!type.isYamlScalar()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml long");
		}
		return scalar.getAsLong();
	}
	
	@Override
	public @NonNull Float getFloat(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a float");
		}
		if (!type.isYamlScalar()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (scalar.isYamlString()) {
			throw new TypeProviderException("Yaml element '" + type + "' is a yaml string, not a yaml float");
		}
		return scalar.getAsFloat();
	}
	
	@Override
	public @NonNull Double getDouble(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a double");
		}
		if (!type.isYamlScalar()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlNumber()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml double");
		}
		return scalar.getAsDouble();
	}
	
	@Override
	public @NonNull String getString(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a string");
		}
		if (!type.isYamlScalar()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml scalar");
		}
		
		YamlScalar scalar = type.getAsYamlScalar();
		if (!scalar.isYamlString()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml string");
		}
		return scalar.getAsString();
	}
	
	@Override
	public @NonNull List<YamlElement> getList(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid list");
		}
		
		if (!type.isYamlSequence()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml sequence");
		}
		return type.getAsYamlSequence().getElements();
	}
	
	@Override
	public @NonNull Map<String, YamlElement> getMap(@Nullable YamlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (!type.isYamlMapping()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml mapping");
		}
		
		Map<String, YamlElement> map = Maps.newLinkedHashMap();
		type.getAsYamlMapping().forEach(map::put);
		return map;
	}
	
	@Override
	public boolean has(@Nullable YamlElement type, @Nullable String key) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		
		if (!type.isYamlMapping()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml mapping");
		}
		return type.getAsYamlMapping().containsKey(key);
	}
	
	@Override
	public @NonNull YamlElement get(@Nullable YamlElement type, @Nullable String key) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		
		if (!type.isYamlMapping()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml mapping");
		}
		
		YamlElement element = type.getAsYamlMapping().get(key);
		if (element == null) {
			throw new TypeProviderException("Key '" + key + "' does not exist in yaml mapping '" + type + "'");
		}
		return element;
	}
	
	@Override
	public void set(@Nullable YamlElement type, @Nullable String key, @Nullable YamlElement value) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		if (value == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		
		if (!type.isYamlMapping()) {
			throw new TypeProviderException("Yaml element '" + type + "' is not a yaml mapping");
		}
		type.getAsYamlMapping().add(key, value);
	}
	
	@Override
	public @UnknownNullability YamlElement merge(@Nullable YamlElement current, @Nullable YamlElement value) {
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
		throw new TypeProviderException("Unable to merge '" + current + "' with '" + value + "'");
	}
}

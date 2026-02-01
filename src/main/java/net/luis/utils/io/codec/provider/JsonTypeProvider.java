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
import net.luis.utils.io.data.json.*;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Type provider implementation for json elements.<br>
 * This class is a singleton and should be accessed through the {@link #INSTANCE} constant.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class JsonTypeProvider implements TypeProvider<JsonElement> {
	
	/**
	 * An empty json element instance.<br>
	 * Used for internal purposes only.<br>
	 * The json element has no string representation and will throw an exception if {@link JsonElement#toString(JsonConfig)} is called.<br>
	 */
	private static final JsonElement EMPTY_ELEMENT = _ -> "Empty json element has no string representation";
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final JsonTypeProvider INSTANCE = new JsonTypeProvider();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private JsonTypeProvider() {}
	
	@Override
	public @NonNull JsonElement empty() {
		return EMPTY_ELEMENT;
	}
	
	@Override
	public @NonNull JsonElement createNull() {
		return JsonNull.INSTANCE;
	}
	
	@Override
	public @NonNull JsonElement createBoolean(boolean value) {
		return new JsonPrimitive(value);
	}
	
	@Override
	public @NonNull JsonElement createByte(byte value) {
		return new JsonPrimitive(value);
	}
	
	@Override
	public @NonNull JsonElement createShort(short value) {
		return new JsonPrimitive(value);
	}
	
	@Override
	public @NonNull JsonElement createInteger(int value) {
		return new JsonPrimitive(value);
	}
	
	@Override
	public @NonNull JsonElement createLong(long value) {
		return new JsonPrimitive(value);
	}
	
	@Override
	public @NonNull JsonElement createFloat(float value) {
		return new JsonPrimitive(value);
	}
	
	@Override
	public @NonNull JsonElement createDouble(double value) {
		return new JsonPrimitive(value);
	}
	
	@Override
	public @NonNull JsonElement createString(@Nullable String value) {
		if (value == null) {
			throw new TypeProviderException("Value 'null' is not a valid string");
		}
		return new JsonPrimitive(value);
	}
	
	@Override
	public @NonNull JsonElement createList(@Nullable List<? extends JsonElement> values) {
		if (values == null) {
			throw new TypeProviderException("Value 'null' is not a valid list");
		}
		return new JsonArray(values);
	}
	
	@Override
	public @NonNull JsonElement createMap() {
		return new JsonObject();
	}
	
	@Override
	public @NonNull JsonElement createMap(@Nullable Map<String, ? extends JsonElement> values) {
		if (values == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		return new JsonObject(values);
	}
	
	@Override
	public boolean isEmpty(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not empty");
		}
		return type == EMPTY_ELEMENT;
	}
	
	@Override
	public boolean isNull(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not json null");
		}
		return type.isJsonNull();
	}
	
	@Override
	public @NonNull Boolean getBoolean(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a boolean");
		}
		if (!type.isJsonPrimitive()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json primitive");
		}
		
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		if (!primitive.isJsonBoolean()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json boolean");
		}
		return primitive.getAsBoolean();
	}
	
	@Override
	public @NonNull Byte getByte(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a byte");
		}
		if (!type.isJsonPrimitive()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json primitive");
		}
		
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		if (!primitive.isJsonNumber()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json byte");
		}
		return primitive.getAsByte();
	}
	
	@Override
	public @NonNull Short getShort(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a short");
		}
		if (!type.isJsonPrimitive()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json primitive");
		}
		
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		if (!primitive.isJsonNumber()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json short");
		}
		return primitive.getAsShort();
	}
	
	@Override
	public @NonNull Integer getInteger(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not an integer");
		}
		if (!type.isJsonPrimitive()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json primitive");
		}
		
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		if (!primitive.isJsonNumber()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json integer");
		}
		return primitive.getAsInteger();
	}
	
	@Override
	public @NonNull Long getLong(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a long");
		}
		if (!type.isJsonPrimitive()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json primitive");
		}
		
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		if (!primitive.isJsonNumber()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json long");
		}
		return primitive.getAsLong();
	}
	
	@Override
	public @NonNull Float getFloat(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a float");
		}
		if (!type.isJsonPrimitive()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json primitive");
		}
		
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		if (primitive.isJsonString()) {
			throw new TypeProviderException("Json element '" + type + "' is a json string, not a json float");
		}
		return primitive.getAsFloat();
	}
	
	@Override
	public @NonNull Double getDouble(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a double");
		}
		if (!type.isJsonPrimitive()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json primitive");
		}
		
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		if (!primitive.isJsonNumber()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json double");
		}
		return primitive.getAsDouble();
	}
	
	@Override
	public @NonNull String getString(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a string");
		}
		if (!type.isJsonPrimitive()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json primitive");
		}
		
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		if (!primitive.isJsonString()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json string");
		}
		return type.getAsJsonPrimitive().getAsString();
	}
	
	@Override
	public @NonNull List<JsonElement> getList(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid list");
		}
		
		if (!type.isJsonArray()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json array");
		}
		return type.getAsJsonArray().getElements();
	}
	
	@Override
	public @NonNull Map<String, JsonElement> getMap(@Nullable JsonElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (!type.isJsonObject()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json object");
		}
		
		Map<String, JsonElement> map = Maps.newLinkedHashMap();
		type.getAsJsonObject().forEach(map::put);
		return map;
	}
	
	@Override
	public boolean has(@Nullable JsonElement type, @Nullable String key) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		
		if (!type.isJsonObject()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json object");
		}
		return type.getAsJsonObject().containsKey(key);
	}
	
	@Override
	public @NonNull JsonElement get(@Nullable JsonElement type, @Nullable String key) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		
		if (!type.isJsonObject()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json object");
		}
		
		JsonElement element = type.getAsJsonObject().get(key);
		if (element == null) {
			throw new TypeProviderException("Key '" + key + "' does not exist in json object '" + type + "'");
		}
		return element;
	}
	
	@Override
	public void set(@Nullable JsonElement type, @Nullable String key, @Nullable JsonElement value) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		if (value == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		
		if (!type.isJsonObject()) {
			throw new TypeProviderException("Json element '" + type + "' is not a json object");
		}
		type.getAsJsonObject().add(key, value);
	}
	
	@Override
	public @UnknownNullability JsonElement merge(@Nullable JsonElement current, @Nullable JsonElement value) {
		if (current == null) {
			return value;
		}
		if (value == null) {
			return current;
		}
		
		if (current == EMPTY_ELEMENT || current.isJsonNull()) {
			return value;
		}
		if (value == EMPTY_ELEMENT || value.isJsonNull()) {
			return current;
		}
		
		if (current.isJsonArray() && value.isJsonArray()) {
			JsonArray array = current.getAsJsonArray();
			array.addAll(value.getAsJsonArray());
			return array;
		}
		
		if (current.isJsonObject() && value.isJsonObject()) {
			JsonObject object = current.getAsJsonObject();
			object.addAll(value.getAsJsonObject());
			return object;
		}
		throw new TypeProviderException("Unable to merge '" + current + "' with '" + value + "'");
	}
}

/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Luis-St
 *
 */

public final class JsonTypeProvider implements TypeProvider<JsonElement> {
	
	public static final JsonTypeProvider INSTANCE = new JsonTypeProvider();
	
	private JsonTypeProvider() {}
	
	//region Creation
	@Override
	public @NotNull JsonElement empty() {
		return JsonNull.INSTANCE;
	}
	
	@Override
	public @NotNull Result<JsonElement> createBoolean(boolean value) {
		return Result.success(new JsonPrimitive(value));
	}
	
	@Override
	public @NotNull Result<JsonElement> createByte(byte value) {
		return Result.success(new JsonPrimitive(value));
	}
	
	@Override
	public @NotNull Result<JsonElement> createShort(short value) {
		return Result.success(new JsonPrimitive(value));
	}
	
	@Override
	public @NotNull Result<JsonElement> createInteger(int value) {
		return Result.success(new JsonPrimitive(value));
	}
	
	@Override
	public @NotNull Result<JsonElement> createLong(long value) {
		return Result.success(new JsonPrimitive(value));
	}
	
	@Override
	public @NotNull Result<JsonElement> createFloat(float value) {
		return Result.success(new JsonPrimitive(value));
	}
	
	@Override
	public @NotNull Result<JsonElement> createDouble(double value) {
		return Result.success(new JsonPrimitive(value));
	}
	
	@Override
	public @NotNull Result<JsonElement> createString(@NotNull String value) {
		return Result.success(new JsonPrimitive(value));
	}
	
	@Override
	public @NotNull Result<JsonElement> createList() {
		return Result.success(new JsonArray());
	}
	
	@Override
	public @NotNull Result<JsonElement> createList(@NotNull List<JsonElement> values) {
		return Result.success(new JsonArray(values));
	}
	
	@Override
	public @NotNull Result<JsonElement> createMap() {
		return Result.success(new JsonObject());
	}
	
	@Override
	public @NotNull Result<JsonElement> createMap(@NotNull Map<String, JsonElement> values) {
		return Result.success(new JsonObject(values));
	}
	//endregion
	
	//region Getters
	@Override
	public @NotNull Result<JsonElement> getEmpty(@NotNull JsonElement type) {
		if (!type.isJsonNull()) {
			return Result.error("Json element '" + type + "' is not a json null");
		}
		return Result.success(type);
	}
	
	@Override
	public @NotNull Result<Boolean> getBoolean(@NotNull JsonElement type) {
		if (!type.isJsonPrimitive()) {
			return Result.error("Json element '" + type + "' is not a json primitive");
		}
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		try {
			return Result.success(primitive.getAsBooleanStrict());
		} catch (IllegalStateException e) {
			return Result.error("Json element '" + type + "' is not a json boolean: " + e.getMessage());
		}
	}
	
	@Override
	public @NotNull Result<Byte> getByte(@NotNull JsonElement type) {
		if (!type.isJsonPrimitive()) {
			return Result.error("Json element '" + type + "' is not a json primitive");
		}
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		try {
			return Result.success(primitive.getAsByteStrict());
		} catch (IllegalStateException e) {
			return Result.error("Json element '" + type + "' is not a json byte: " + e.getMessage());
		}
	}
	
	@Override
	public @NotNull Result<Short> getShort(@NotNull JsonElement type) {
		if (!type.isJsonPrimitive()) {
			return Result.error("Json element '" + type + "' is not a json primitive");
		}
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		try {
			return Result.success(primitive.getAsShortStrict());
		} catch (IllegalStateException e) {
			return Result.error("Json element '" + type + "' is not a json short: " + e.getMessage());
		}
	}
	
	@Override
	public @NotNull Result<Integer> getInteger(@NotNull JsonElement type) {
		if (!type.isJsonPrimitive()) {
			return Result.error("Json element '" + type + "' is not a json primitive");
		}
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		try {
			return Result.success(primitive.getAsIntegerStrict());
		} catch (IllegalStateException e) {
			return Result.error("Json element '" + type + "' is not a json integer: " + e.getMessage());
		}
	}
	
	@Override
	public @NotNull Result<Long> getLong(@NotNull JsonElement type) {
		if (!type.isJsonPrimitive()) {
			return Result.error("Json element '" + type + "' is not a json primitive");
		}
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		try {
			return Result.success(primitive.getAsLongStrict());
		} catch (IllegalStateException e) {
			return Result.error("Json element '" + type + "' is not a json long: " + e.getMessage());
		}
	}
	
	@Override
	public @NotNull Result<Float> getFloat(@NotNull JsonElement type) {
		if (!type.isJsonPrimitive()) {
			return Result.error("Json element '" + type + "' is not a json primitive");
		}
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		try {
			return Result.success(primitive.getAsFloatStrict());
		} catch (IllegalStateException e) {
			return Result.error("Json element '" + type + "' is not a json float: " + e.getMessage());
		}
	}
	
	@Override
	public @NotNull Result<Double> getDouble(@NotNull JsonElement type) {
		if (!type.isJsonPrimitive()) {
			return Result.error("Json element '" + type + "' is not a json primitive");
		}
		JsonPrimitive primitive = type.getAsJsonPrimitive();
		try {
			return Result.success(primitive.getAsDoubleStrict());
		} catch (IllegalStateException e) {
			return Result.error("Json element '" + type + "' is not a json double: " + e.getMessage());
		}
	}
	
	@Override
	public @NotNull Result<String> getString(@NotNull JsonElement type) {
		if (!type.isJsonPrimitive()) {
			return Result.error("Json element '" + type + "' is not a json primitive");
		}
		return Result.success(type.getAsJsonPrimitive().getAsString());
	}
	
	@Override
	public @NotNull Result<List<JsonElement>> getList(@NotNull JsonElement type) {
		if (!type.isJsonArray()) {
			return Result.error("Json element '" + type + "' is not a json array");
		}
		return Result.success(type.getAsJsonArray().getElements());
	}
	
	@Override
	public @NotNull Result<Map<String, JsonElement>> getMap(@NotNull JsonElement type) {
		if (!type.isJsonObject()) {
			return Result.error("Json element '" + type + "' is not a json object");
		}
		Map<String, JsonElement> map = Maps.newLinkedHashMap();
		type.getAsJsonObject().forEach(map::put);
		return Result.success(map);
	}
	//endregion
	
	//region Modification
	@Override
	public @NotNull Result<Boolean> has(@NotNull JsonElement type, @NotNull String key) {
		if (!type.isJsonObject()) {
			return Result.error("Json element '" + type + "' is not a json object");
		}
		return Result.success(type.getAsJsonObject().containsKey(key));
	}
	
	@Override
	public @NotNull Result<JsonElement> get(@NotNull JsonElement type, @NotNull String key) {
		if (!type.isJsonObject()) {
			return Result.error("Json element '" + type + "' is not a json object");
		}
		return Result.success(type.getAsJsonObject().get(key));
	}
	
	@Override
	public @NotNull Result<JsonElement> set(@NotNull JsonElement type, @NotNull String key, @NotNull JsonElement value) {
		if (!type.isJsonObject()) {
			return Result.error("Json element '" + type + "' is not a json object");
		}
		return Result.success(type.getAsJsonObject().add(key, value));
	}
	
	@Override
	public @NotNull Result<JsonElement> merge(@NotNull JsonElement current, @NotNull JsonElement value) {
		if (current.isJsonNull()) {
			return Result.success(value);
		}
		if (current.isJsonArray() && value.isJsonArray()) {
			JsonArray array = current.getAsJsonArray();
			array.addAll(value.getAsJsonArray());
			return Result.success(array);
		}
		if (current.isJsonObject() && value.isJsonObject()) {
			JsonObject object = current.getAsJsonObject();
			object.addAll(value.getAsJsonObject());
			return Result.success(object);
		}
		return Result.error("Unable to merge '" + current + "' with '" + value + "'");
	}
	//endregion
}
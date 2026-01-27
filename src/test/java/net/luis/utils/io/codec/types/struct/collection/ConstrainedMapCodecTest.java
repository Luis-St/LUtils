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

package net.luis.utils.io.codec.types.struct.collection;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.collection.MapConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MapCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedMapCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(1));
		Map<String, Integer> validMap = Map.of("a", 1, "b", 2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), validMap);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(1));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(Map.of("a", 1, "b", 2), result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(1));
		String str = codec.toString();
		assertTrue(str.contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).equalTo(Map.of("a", 1, "b", 2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).equalTo(Map.of("a", 1, "b", 2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("c", 3));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).equalTo(Map.of("a", 1, "b", 2));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).equalTo(Map.of("a", 1, "b", 2));
		
		JsonObject json = new JsonObject();
		json.add("c", new JsonPrimitive(3));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notEqualTo(Map.of("a", 1, "b", 2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("c", 3, "d", 4));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notEqualTo(Map.of("a", 1, "b", 2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notEqualTo(Map.of("a", 1, "b", 2));
		
		JsonObject json = new JsonObject();
		json.add("c", new JsonPrimitive(3));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notEqualTo(Map.of("a", 1, "b", 2));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).in(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).in(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("c", 3));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).in(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		JsonObject json = new JsonObject();
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).in(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		JsonObject json = new JsonObject();
		json.add("c", new JsonPrimitive(3));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notIn(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("c", 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notIn(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notIn(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		JsonObject json = new JsonObject();
		json.add("c", new JsonPrimitive(3));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notIn(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		JsonObject json = new JsonObject();
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMinSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMinSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMinSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(2));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMinSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(3));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMaxSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.maxSize(5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMaxSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.maxSize(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMaxSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.maxSize(5));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMaxSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.maxSize(2));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		json.add("c", new JsonPrimitive(3));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartExactSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.exactSize(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartExactSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.exactSize(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartExactSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.exactSize(2));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartExactSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.exactSize(3));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartSizeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.sizeBetween(2, 4));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartSizeBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.sizeBetween(3, 5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartSizeBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.sizeBetween(1, 2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartSizeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.sizeBetween(1, 3));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartSizeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.sizeBetween(3, 5));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartRequiredKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKey("a");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartRequiredKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKey("a");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("b", 2, "c", 3));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartRequiredKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKey("a");
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartRequiredKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKey("a");
		
		JsonObject json = new JsonObject();
		json.add("b", new JsonPrimitive(2));
		json.add("c", new JsonPrimitive(3));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartRequiredKeysConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKeys(List.of("a", "b"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartRequiredKeysConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKeys(List.of("a", "b"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "c", 3));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartForbiddenKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKey("x");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartForbiddenKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKey("a");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartForbiddenKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKey("x");
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartForbiddenKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKey("a");
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartForbiddenKeysConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKeys(List.of("x", "y"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartForbiddenKeysConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKeys(List.of("a", "b"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "c", 3));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAllowedKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).allowedKeys(List.of("a", "b", "c"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAllowedKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).allowedKeys(List.of("a", "b"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "x", 2));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAllowedKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).allowedKeys(List.of("a", "b", "c"));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartAllowedKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).allowedKeys(List.of("a", "b"));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("x", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartUniqueValuesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).uniqueValues();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartUniqueValuesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).uniqueValues();
		
		Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), map);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartUniqueValuesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).uniqueValues();
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartUniqueValuesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).uniqueValues();
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(1));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).custom(map -> {
			if (map.values().stream().allMatch(v -> v > 0)) {
				return Result.success(null);
			}
			return Result.error("All values must be positive");
		});
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).custom(map -> {
			if (map.values().stream().allMatch(v -> v > 0)) {
				return Result.success(null);
			}
			return Result.error("All values must be positive");
		});
		
		Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", -2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), map);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).custom(map -> {
			if (map.values().stream().allMatch(v -> v > 0)) {
				return Result.success(null);
			}
			return Result.error("All values must be positive");
		});
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).custom(map -> {
			if (map.values().stream().allMatch(v -> v > 0)) {
				return Result.success(null);
			}
			return Result.error("All values must be positive");
		});
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(-2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(2).maxSize(4)).requiredKey("a");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(2).maxSize(4)).requiredKey("a");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of("b", 2, "c", 3));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(1).maxSize(3)).forbiddenKey("x");
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(3).maxSize(5));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), json);
		assertTrue(result.isError());
	}
}

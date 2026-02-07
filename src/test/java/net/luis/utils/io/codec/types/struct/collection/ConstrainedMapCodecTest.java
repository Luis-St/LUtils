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
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonObject;
import net.luis.utils.io.data.json.JsonPrimitive;
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
	void encodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(1));
		Map<String, Integer> validMap = Map.of("a", 1, "b", 2);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), validMap));
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(1));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		Map<String, Integer> result = codec.decode(typeProvider, typeProvider.empty(), json);
		assertEquals(Map.of("a", 1, "b", 2), result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(1));
		String str = codec.toString();
		assertTrue(str.contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).equalTo(Map.of("a", 1, "b", 2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2)));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).equalTo(Map.of("a", 1, "b", 2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("c", 3)));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).equalTo(Map.of("a", 1, "b", 2));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).equalTo(Map.of("a", 1, "b", 2));
		
		JsonObject json = new JsonObject();
		json.add("c", new JsonPrimitive(3));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notEqualTo(Map.of("a", 1, "b", 2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("c", 3, "d", 4)));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notEqualTo(Map.of("a", 1, "b", 2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2)));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notEqualTo(Map.of("a", 1, "b", 2));
		
		JsonObject json = new JsonObject();
		json.add("c", new JsonPrimitive(3));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notEqualTo(Map.of("a", 1, "b", 2));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).in(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1)));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).in(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("c", 3)));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).in(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		JsonObject json = new JsonObject();
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).in(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		JsonObject json = new JsonObject();
		json.add("c", new JsonPrimitive(3));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notIn(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("c", 3)));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notIn(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1)));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notIn(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		JsonObject json = new JsonObject();
		json.add("c", new JsonPrimitive(3));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).notIn(List.of(Map.of("a", 1), Map.of("b", 2)));
		
		JsonObject json = new JsonObject();
		json.add("b", new JsonPrimitive(2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeMinSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3)));
	}
	
	@Test
	void encodeMinSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(3));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2)));
	}
	
	@Test
	void decodeMinSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(2));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeMinSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(3));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeMaxSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.maxSize(5));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3)));
	}
	
	@Test
	void encodeMaxSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.maxSize(2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3)));
	}
	
	@Test
	void decodeMaxSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.maxSize(5));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeMaxSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.maxSize(2));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		json.add("c", new JsonPrimitive(3));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeExactSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.exactSize(3));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3)));
	}
	
	@Test
	void encodeExactSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.exactSize(3));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2)));
	}
	
	@Test
	void decodeExactSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.exactSize(2));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeExactSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.exactSize(3));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeSizeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.sizeBetween(2, 4));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3)));
	}
	
	@Test
	void encodeSizeBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.sizeBetween(3, 5));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2)));
	}
	
	@Test
	void encodeSizeBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.sizeBetween(1, 2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3)));
	}
	
	@Test
	void decodeSizeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.sizeBetween(1, 3));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeSizeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.sizeBetween(3, 5));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeRequiredKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKey("a");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2)));
	}
	
	@Test
	void encodeRequiredKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKey("a");
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("b", 2, "c", 3)));
	}
	
	@Test
	void decodeRequiredKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKey("a");
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeRequiredKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKey("a");
		
		JsonObject json = new JsonObject();
		json.add("b", new JsonPrimitive(2));
		json.add("c", new JsonPrimitive(3));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeRequiredKeysConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKeys(List.of("a", "b"));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3)));
	}
	
	@Test
	void encodeRequiredKeysConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).requiredKeys(List.of("a", "b"));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "c", 3)));
	}
	
	@Test
	void encodeForbiddenKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKey("x");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2)));
	}
	
	@Test
	void encodeForbiddenKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKey("a");
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2)));
	}
	
	@Test
	void decodeForbiddenKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKey("x");
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeForbiddenKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKey("a");
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeForbiddenKeysConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKeys(List.of("x", "y"));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2)));
	}
	
	@Test
	void encodeForbiddenKeysConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).forbiddenKeys(List.of("a", "b"));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "c", 3)));
	}
	
	@Test
	void encodeAllowedKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).allowedKeys(List.of("a", "b", "c"));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2)));
	}
	
	@Test
	void encodeAllowedKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).allowedKeys(List.of("a", "b"));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "x", 2)));
	}
	
	@Test
	void decodeAllowedKeyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).allowedKeys(List.of("a", "b", "c"));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeAllowedKeyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).allowedKeys(List.of("a", "b"));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("x", new JsonPrimitive(2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeUniqueValuesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).uniqueValues();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3)));
	}
	
	@Test
	void encodeUniqueValuesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).uniqueValues();
		
		Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 1);
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), map));
	}
	
	@Test
	void decodeUniqueValuesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).uniqueValues();
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeUniqueValuesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).uniqueValues();
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(1));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).custom(map -> {
			if (!map.values().stream().allMatch(v -> v > 0)) {
				throw new ConstraintViolateException("All values must be positive");
			}
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2)));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).custom(map -> {
			if (!map.values().stream().allMatch(v -> v > 0)) {
				throw new ConstraintViolateException("All values must be positive");
			}
		});
		
		Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", -2);
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), map));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).custom(map -> {
			if (!map.values().stream().allMatch(v -> v > 0)) {
				throw new ConstraintViolateException("All values must be positive");
			}
		});
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).custom(map -> {
			if (!map.values().stream().allMatch(v -> v > 0)) {
				throw new ConstraintViolateException("All values must be positive");
			}
		});
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(-2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void encodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(2).maxSize(4)).requiredKey("a");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of("a", 1, "b", 2, "c", 3)));
	}
	
	@Test
	void encodeCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(2).maxSize(4)).requiredKey("a");
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Map.of("b", 2, "c", 3)));
	}
	
	@Test
	void decodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(1).maxSize(3)).forbiddenKey("x");
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
	
	@Test
	void decodeCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER).size(builder -> builder.minSize(3).maxSize(5));
		
		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive(1));
		json.add("b", new JsonPrimitive(2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), json));
	}
}

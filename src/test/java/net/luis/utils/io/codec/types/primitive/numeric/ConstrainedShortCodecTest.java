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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.numeric.IntegerConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ShortCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedShortCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 100);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive((short) 100), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isSuccess());
		assertEquals((short) 100, result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey((short) 100);
		assertTrue(result.isSuccess());
		assertEquals("100", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Short> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
		assertEquals((short) 100, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Short> codec = new ShortCodec();
		assertFalse(codec.toString().contains("Constrained"));
		assertEquals("ShortCodec", codec.toString());
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withEqualTo((short) 42));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withEqualTo((short) 42));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 42));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withEqualTo((short) 42));
		
		Result<String> result = codec.encodeKey((short) 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withEqualTo((short) 42));
		
		Result<Short> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withEqualTo((short) 42));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 100);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withEqualTo((short) 42));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withEqualTo((short) 42));
		
		Result<String> result = codec.encodeKey((short) 100);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withEqualTo((short) 42));
		
		Result<Short> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotEqualTo((short) 42));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotEqualTo((short) 42));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotEqualTo((short) 42));
		
		Result<String> result = codec.encodeKey((short) 100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotEqualTo((short) 42));
		
		Result<Short> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotEqualTo((short) 42));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotEqualTo((short) 42));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 42));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotEqualTo((short) 42));
		
		Result<String> result = codec.encodeKey((short) 42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotEqualTo((short) 42));
		
		Result<Short> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 20);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 20));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<String> result = codec.encodeKey((short) 20);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<Short> result = codec.decodeKey("20");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<String> result = codec.encodeKey((short) 50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<Short> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<String> result = codec.encodeKey((short) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<Short> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 20);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 20));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<String> result = codec.encodeKey((short) 20);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withNotIn(Set.of((short) 10, (short) 20, (short) 30)));
		
		Result<Short> result = codec.decodeKey("20");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThan((short) 50));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThan((short) 50));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThan((short) 50));
		
		Result<String> result = codec.encodeKey((short) 100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThan((short) 50));
		
		Result<Short> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThan((short) 50));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThan((short) 50));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThan((short) 50));
		
		Result<String> result = codec.encodeKey((short) 50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThan((short) 50));
		
		Result<Short> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThanOrEqual((short) 50));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThanOrEqual((short) 50));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThanOrEqual((short) 50));
		
		Result<String> result = codec.encodeKey((short) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThanOrEqual((short) 50));
		
		Result<Short> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThanOrEqual((short) 50));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 49);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThanOrEqual((short) 50));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 49));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThanOrEqual((short) 50));
		
		Result<String> result = codec.encodeKey((short) 49);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withGreaterThanOrEqual((short) 50));
		
		Result<Short> result = codec.decodeKey("49");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThan((short) 50));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 25);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThan((short) 50));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 25));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThan((short) 50));
		
		Result<String> result = codec.encodeKey((short) 25);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThan((short) 50));
		
		Result<Short> result = codec.decodeKey("25");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThan((short) 50));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThan((short) 50));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThan((short) 50));
		
		Result<String> result = codec.encodeKey((short) 50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThan((short) 50));
		
		Result<Short> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThanOrEqual((short) 50));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThanOrEqual((short) 50));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 25));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThanOrEqual((short) 50));
		
		Result<String> result = codec.encodeKey((short) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThanOrEqual((short) 50));
		
		Result<Short> result = codec.decodeKey("25");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThanOrEqual((short) 50));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 51);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThanOrEqual((short) 50));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 51));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThanOrEqual((short) 50));
		
		Result<String> result = codec.encodeKey((short) 51);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withLessThanOrEqual((short) 50));
		
		Result<Short> result = codec.decodeKey("51");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetween((short) 10, (short) 100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetween((short) 10, (short) 100));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetween((short) 10, (short) 100));
		
		Result<String> result = codec.encodeKey((short) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetween((short) 10, (short) 100));
		
		Result<Short> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetween((short) 10, (short) 100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetween((short) 10, (short) 100));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetween((short) 10, (short) 100));
		
		Result<String> result = codec.encodeKey((short) 5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetween((short) 10, (short) 100));
		
		Result<Short> result = codec.decodeKey("150");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetweenOrEqual((short) 10, (short) 100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetweenOrEqual((short) 10, (short) 100));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetweenOrEqual((short) 10, (short) 100));
		
		Result<String> result = codec.encodeKey((short) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetweenOrEqual((short) 10, (short) 100));
		
		Result<Short> result = codec.decodeKey("75");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetweenOrEqual((short) 10, (short) 100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 9);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetweenOrEqual((short) 10, (short) 100));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 101));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetweenOrEqual((short) 10, (short) 100));
		
		Result<String> result = codec.encodeKey((short) 5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withBetweenOrEqual((short) 10, (short) 100));
		
		Result<Short> result = codec.decodeKey("150");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey((short) 1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Short> result = codec.decodeKey("500");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -10));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey((short) -5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Short> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<String> result = codec.encodeKey((short) -100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Short> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<String> result = codec.encodeKey((short) 50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Short> result = codec.decodeKey("10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) -42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<String> result = codec.encodeKey((short) -1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Short> result = codec.decodeKey("-500");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 10));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<String> result = codec.encodeKey((short) 5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Short> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<String> result = codec.encodeKey((short) 100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Short> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) -1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -100));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<String> result = codec.encodeKey((short) -50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Short> result = codec.decodeKey("-10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<String> result = codec.encodeKey((short) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Short> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -1));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<String> result = codec.encodeKey((short) 100);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Short> result = codec.decodeKey("-50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<String> result = codec.encodeKey((short) 1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Short> result = codec.decodeKey("-1");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<String> result = codec.encodeKey((short) 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Short> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<String> result = codec.encodeKey((short) 100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Short> result = codec.decodeKey("75");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) -1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 101));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<String> result = codec.encodeKey((short) 150);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Short> result = codec.decodeKey("-10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEvenConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<String> result = codec.encodeKey((short) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEvenConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Short> result = codec.decodeKey("-50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 41);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 99));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEvenConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<String> result = codec.encodeKey((short) 1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEvenConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Short> result = codec.decodeKey("-51");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 41);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 99));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyOddConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<String> result = codec.encodeKey((short) 1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyOddConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Short> result = codec.decodeKey("-51");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyOddConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<String> result = codec.encodeKey((short) 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyOddConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Short> result = codec.decodeKey("-50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyDivisibleByConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<String> result = codec.encodeKey((short) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyDivisibleByConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<Short> result = codec.decodeKey("-25");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 51));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyDivisibleByConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<String> result = codec.encodeKey((short) 7);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyDivisibleByConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<Short> result = codec.decodeKey("-26");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withPowerOf(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 27);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withPowerOf(3));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 81));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPowerOfConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withPowerOf(3));
		
		Result<String> result = codec.encodeKey((short) 1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPowerOfConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withPowerOf(3));
		
		Result<Short> result = codec.decodeKey("9");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withPowerOf(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withPowerOf(3));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPowerOfConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withPowerOf(3));
		
		Result<String> result = codec.encodeKey((short) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPowerOfConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withPowerOf(3));
		
		Result<Short> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 64);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 128));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<String> result = codec.encodeKey((short) 1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Short> result = codec.decodeKey("256");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<String> result = codec.encodeKey((short) 3);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Short> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withCustom(value -> value % 7 == 0 ? Result.success() : Result.error("Value must be divisible by 7")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 49);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withCustom(value -> value % 7 == 0 ? Result.success() : Result.error("Value must be divisible by 7")));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withCustom(value -> value % 7 == 0 ? Result.success() : Result.error("Value must be divisible by 7")));
		
		Result<String> result = codec.encodeKey((short) 21);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withCustom(value -> value % 7 == 0 ? Result.success() : Result.error("Value must be divisible by 7")));
		
		Result<Short> result = codec.decodeKey("7");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withCustom(value -> value % 7 == 0 ? Result.success() : Result.error("Value must be divisible by 7")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (short) 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec().apply(config -> config.withCustom(value -> value % 7 == 0 ? Result.success() : Result.error("Value must be divisible by 7")));
		
		Result<Short> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withCustom(value -> value % 7 == 0 ? Result.success() : Result.error("Value must be divisible by 7")));
		
		Result<String> result = codec.encodeKey((short) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Short> codec = new ShortCodec().apply(config -> config.withCustom(value -> value % 7 == 0 ? Result.success() : Result.error("Value must be divisible by 7")));
		
		Result<Short> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
}

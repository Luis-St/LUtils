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
import net.luis.utils.io.codec.constraint.config.numeric.DecimalConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FloatCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedFloatCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14f);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(3.14f), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f));
		assertTrue(result.isSuccess());
		assertEquals(3.14f, result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(3.14f);
		assertTrue(result.isSuccess());
		assertEquals("3.14", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<Float> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
		assertEquals(3.14f, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Float> codec = new FloatCodec();
		assertFalse(codec.toString().contains("Constrained"));
		assertEquals("FloatCodec", codec.toString());
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withEqualTo(3.14f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withEqualTo(3.14f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withEqualTo(3.14f));
		
		Result<String> result = codec.encodeKey(3.14f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withEqualTo(3.14f));
		
		Result<Float> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withEqualTo(3.14f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withEqualTo(3.14f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withEqualTo(3.14f));
		
		Result<String> result = codec.encodeKey(42.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withEqualTo(3.14f));
		
		Result<Float> result = codec.decodeKey("42.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotEqualTo(3.14f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotEqualTo(3.14f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotEqualTo(3.14f));
		
		Result<String> result = codec.encodeKey(42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotEqualTo(3.14f));
		
		Result<Float> result = codec.decodeKey("42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotEqualTo(3.14f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotEqualTo(3.14f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotEqualTo(3.14f));
		
		Result<String> result = codec.encodeKey(3.14f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotEqualTo(3.14f));
		
		Result<Float> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withIn(Set.of(3.14f, 42.0f, 100.5f)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withIn(Set.of(3.14f, 42.0f, 100.5f)));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withIn(Set.of(3.14f, 42.0f, 100.5f)));
		
		Result<String> result = codec.encodeKey(42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withIn(Set.of(3.14f, 42.0f, 100.5f)));
		
		Result<Float> result = codec.decodeKey("42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withIn(Set.of(3.14f, 42.0f)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100.5f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withIn(Set.of(3.14f, 42.0f)));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100.5f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withIn(Set.of(3.14f, 42.0f)));
		
		Result<String> result = codec.encodeKey(100.5f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withIn(Set.of(3.14f, 42.0f)));
		
		Result<Float> result = codec.decodeKey("100.5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotIn(Set.of(3.14f, 42.0f)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100.5f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotIn(Set.of(3.14f, 42.0f)));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100.5f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotIn(Set.of(3.14f, 42.0f)));
		
		Result<String> result = codec.encodeKey(100.5f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotIn(Set.of(3.14f, 42.0f)));
		
		Result<Float> result = codec.decodeKey("100.5");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotIn(Set.of(3.14f, 42.0f)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotIn(Set.of(3.14f, 42.0f)));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotIn(Set.of(3.14f, 42.0f)));
		
		Result<String> result = codec.encodeKey(3.14f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withNotIn(Set.of(3.14f, 42.0f)));
		
		Result<Float> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThan(10.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThan(10.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThan(10.0f));
		
		Result<String> result = codec.encodeKey(42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThan(10.0f));
		
		Result<Float> result = codec.decodeKey("42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThan(10.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 10.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThan(10.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(5.0f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThan(10.0f));
		
		Result<String> result = codec.encodeKey(10.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThan(10.0f));
		
		Result<Float> result = codec.decodeKey("5.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThanOrEqual(10.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 10.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThanOrEqual(10.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThanOrEqual(10.0f));
		
		Result<String> result = codec.encodeKey(10.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThanOrEqual(10.0f));
		
		Result<Float> result = codec.decodeKey("42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThanOrEqual(10.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 9.99f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThanOrEqual(10.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(5.0f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThanOrEqual(10.0f));
		
		Result<String> result = codec.encodeKey(9.99f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withGreaterThanOrEqual(10.0f));
		
		Result<Float> result = codec.decodeKey("5.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThan(100.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThan(100.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThan(100.0f));
		
		Result<String> result = codec.encodeKey(42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThan(100.0f));
		
		Result<Float> result = codec.decodeKey("42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThan(100.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThan(100.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(150.0f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThan(100.0f));
		
		Result<String> result = codec.encodeKey(100.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThan(100.0f));
		
		Result<Float> result = codec.decodeKey("150.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThanOrEqual(100.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThanOrEqual(100.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThanOrEqual(100.0f));
		
		Result<String> result = codec.encodeKey(100.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThanOrEqual(100.0f));
		
		Result<Float> result = codec.decodeKey("42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThanOrEqual(100.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100.01f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThanOrEqual(100.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(150.0f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThanOrEqual(100.0f));
		
		Result<String> result = codec.encodeKey(100.01f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withLessThanOrEqual(100.0f));
		
		Result<Float> result = codec.decodeKey("150.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetween(10.0f, 100.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetween(10.0f, 100.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(50.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetween(10.0f, 100.0f));
		
		Result<String> result = codec.encodeKey(42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetween(10.0f, 100.0f));
		
		Result<Float> result = codec.decodeKey("50.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetween(10.0f, 100.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 10.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetween(10.0f, 100.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100.0f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetween(10.0f, 100.0f));
		
		Result<String> result = codec.encodeKey(5.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetween(10.0f, 100.0f));
		
		Result<Float> result = codec.decodeKey("150.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetweenOrEqual(10.0f, 100.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 10.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetweenOrEqual(10.0f, 100.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetweenOrEqual(10.0f, 100.0f));
		
		Result<String> result = codec.encodeKey(50.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetweenOrEqual(10.0f, 100.0f));
		
		Result<Float> result = codec.decodeKey("100.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetweenOrEqual(10.0f, 100.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 9.99f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetweenOrEqual(10.0f, 100.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100.01f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetweenOrEqual(10.0f, 100.0f));
		
		Result<String> result = codec.encodeKey(5.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withBetweenOrEqual(10.0f, 100.0f));
		
		Result<Float> result = codec.decodeKey("150.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(0.001f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<Float> result = codec.decodeKey("42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(-1.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPositive);
		
		Result<Float> result = codec.decodeKey("0.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonPositive);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonPositive);
		
		Result<String> result = codec.encodeKey(-42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonPositive);
		
		Result<Float> result = codec.decodeKey("0.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.001f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonPositive);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonPositive);
		
		Result<String> result = codec.encodeKey(42.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonPositive);
		
		Result<Float> result = codec.decodeKey("1.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -3.14f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNegative);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-42.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNegative);
		
		Result<String> result = codec.encodeKey(-0.001f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNegative);
		
		Result<Float> result = codec.decodeKey("-42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNegative);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNegative);
		
		Result<String> result = codec.encodeKey(1.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNegative);
		
		Result<Float> result = codec.decodeKey("0.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonNegative);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonNegative);
		
		Result<String> result = codec.encodeKey(42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonNegative);
		
		Result<Float> result = codec.decodeKey("0.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -0.001f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonNegative);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonNegative);
		
		Result<String> result = codec.encodeKey(-42.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonNegative);
		
		Result<Float> result = codec.decodeKey("-1.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withZero);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withZero);
		
		Result<String> result = codec.encodeKey(0.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withZero);
		
		Result<Float> result = codec.decodeKey("0.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.001f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withZero);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withZero);
		
		Result<String> result = codec.encodeKey(-1.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withZero);
		
		Result<Float> result = codec.decodeKey("42.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonZero);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-42.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonZero);
		
		Result<String> result = codec.encodeKey(0.001f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonZero);
		
		Result<Float> result = codec.decodeKey("-0.001");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonZero);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonZero);
		
		Result<String> result = codec.encodeKey(0.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNonZero);
		
		Result<Float> result = codec.decodeKey("0.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPercentage);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 50.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPercentage);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPercentage);
		
		Result<String> result = codec.encodeKey(100.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPercentage);
		
		Result<Float> result = codec.decodeKey("75.5");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPercentage);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -1.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPercentage);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100.01f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPercentage);
		
		Result<String> result = codec.encodeKey(150.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withPercentage);
		
		Result<Float> result = codec.decodeKey("-0.1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartFiniteConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withFinite);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartFiniteConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withFinite);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.MAX_VALUE));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyFiniteConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withFinite);
		
		Result<String> result = codec.encodeKey(Float.MIN_VALUE);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyFiniteConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withFinite);
		
		Result<Float> result = codec.decodeKey("42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartFiniteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withFinite);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Float.POSITIVE_INFINITY);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartFiniteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withFinite);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.NEGATIVE_INFINITY));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyFiniteConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withFinite);
		
		Result<String> result = codec.encodeKey(Float.NaN);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyFiniteConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withFinite);
		
		Result<Float> result = codec.decodeKey("Infinity");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotNaNConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNotNaN);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotNaNConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNotNaN);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.POSITIVE_INFINITY));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotNaNConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNotNaN);
		
		Result<String> result = codec.encodeKey(Float.NEGATIVE_INFINITY);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotNaNConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNotNaN);
		
		Result<Float> result = codec.decodeKey("42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotNaNConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNotNaN);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Float.NaN);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotNaNConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNotNaN);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.NaN));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotNaNConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNotNaN);
		
		Result<String> result = codec.encodeKey(Float.NaN);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotNaNConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNotNaN);
		
		Result<Float> result = codec.decodeKey("NaN");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withIntegral);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withIntegral);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyIntegralConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withIntegral);
		
		Result<String> result = codec.encodeKey(-5.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyIntegralConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withIntegral);
		
		Result<Float> result = codec.decodeKey("0.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withIntegral);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withIntegral);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0.5f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyIntegralConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withIntegral);
		
		Result<String> result = codec.encodeKey(2.718f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyIntegralConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withIntegral);
		
		Result<Float> result = codec.decodeKey("1.5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNormalized);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.5f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNormalized);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNormalizedConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNormalized);
		
		Result<String> result = codec.encodeKey(1.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNormalizedConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNormalized);
		
		Result<Float> result = codec.decodeKey("0.75");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNormalized);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -0.1f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNormalized);
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1.1f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNormalizedConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNormalized);
		
		Result<String> result = codec.encodeKey(2.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNormalizedConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(DecimalConstraintConfig::withNormalized);
		
		Result<Float> result = codec.decodeKey("-0.5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withCustom(value -> value % 2.0f == 0.0f ? Result.success() : Result.error("Value must be even")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 4.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withCustom(value -> value % 2.0f == 0.0f ? Result.success() : Result.error("Value must be even")));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(10.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withCustom(value -> value % 2.0f == 0.0f ? Result.success() : Result.error("Value must be even")));
		
		Result<String> result = codec.encodeKey(6.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withCustom(value -> value % 2.0f == 0.0f ? Result.success() : Result.error("Value must be even")));
		
		Result<Float> result = codec.decodeKey("8.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withCustom(value -> value % 2.0f == 0.0f ? Result.success() : Result.error("Value must be even")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config.withCustom(value -> value % 2.0f == 0.0f ? Result.success() : Result.error("Value must be even")));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(5.0f));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withCustom(value -> value % 2.0f == 0.0f ? Result.success() : Result.error("Value must be even")));
		
		Result<String> result = codec.encodeKey(7.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config.withCustom(value -> value % 2.0f == 0.0f ? Result.success() : Result.error("Value must be even")));
		
		Result<Float> result = codec.decodeKey("9.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config
			.withPositive()
			.withFinite()
			.withLessThan(100.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config
			.withPositive()
			.withFinite()
			.withLessThan(100.0f));
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(50.0f));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCombinedConstraintsSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config
			.withNonNegative()
			.withNormalized());
		
		Result<String> result = codec.encodeKey(0.5f);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCombinedConstraintsSuccess() {
		Codec<Float> codec = new FloatCodec().apply(config -> config
			.withNonNegative()
			.withNormalized());
		
		Result<Float> result = codec.decodeKey("0.75");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCombinedConstraintsPositiveViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config
			.withPositive()
			.withLessThan(100.0f));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -5.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCombinedConstraintsFiniteViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec().apply(config -> config
			.withPositive()
			.withFinite());
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.POSITIVE_INFINITY));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCombinedConstraintsRangeViolation() {
		Codec<Float> codec = new FloatCodec().apply(config -> config
			.withPositive()
			.withLessThan(100.0f));
		
		Result<String> result = codec.encodeKey(150.0f);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCombinedConstraintsMultipleViolations() {
		Codec<Float> codec = new FloatCodec().apply(config -> config
			.withPositive()
			.withFinite()
			.withLessThan(100.0f));
		
		Result<Float> result = codec.decodeKey("-50.0");
		assertTrue(result.isError());
	}
	
}

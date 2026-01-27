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
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DoubleCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedDoubleCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(3.14), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
		assertEquals(3.14, result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
		assertEquals("3.14", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
		assertEquals(3.14, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Double> codec = Codecs.DOUBLE;
		assertEquals("DoubleCodec", codec.toString());
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		Result<String> result = codec.encodeKey(42.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		Result<Double> result = codec.decodeKey("42.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		Result<String> result = codec.encodeKey(42.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		Result<Double> result = codec.decodeKey("42.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		Result<String> result = codec.encodeKey(42.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		Result<Double> result = codec.decodeKey("42.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		Result<String> result = codec.encodeKey(42.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		Result<Double> result = codec.decodeKey("42.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(0.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(0.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(0.0);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(0.0);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(10.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(10.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(10.0);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(10.0);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(3.14);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(3.14);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(3.14);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(3.14);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(10.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(10.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(10.0);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(10.0);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThan(10.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThan(10.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.lessThan(10.0);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.lessThan(10.0);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThan(3.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThan(3.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.lessThan(3.0);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.lessThan(3.0);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.14);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.14);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.14);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.14);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.0);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.0);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 10.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 10.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 10.0);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 10.0);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 3.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 3.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 3.0);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 3.0);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(3.14, 10.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(3.14, 10.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(3.14, 10.0);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(3.14, 10.0);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(5.0, 10.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(5.0, 10.0);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(5.0, 10.0);
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(5.0, 10.0);
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		Result<String> result = codec.encodeKey(-3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		Result<Double> result = codec.decodeKey("-3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		Result<String> result = codec.encodeKey(-3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		Result<Double> result = codec.decodeKey("-3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		Result<String> result = codec.encodeKey(-3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		Result<Double> result = codec.decodeKey("-3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		Result<String> result = codec.encodeKey(-3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		Result<Double> result = codec.decodeKey("-3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		Result<String> result = codec.encodeKey(0.0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		Result<Double> result = codec.decodeKey("0.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		Result<String> result = codec.encodeKey(0.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		Result<Double> result = codec.decodeKey("0.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 50.0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(50.0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		Result<String> result = codec.encodeKey(50.0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		Result<Double> result = codec.decodeKey("50.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 150.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(150.0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		Result<String> result = codec.encodeKey(150.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		Result<Double> result = codec.decodeKey("150.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartFiniteConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartFiniteConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyFiniteConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyFiniteConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartFiniteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Double.POSITIVE_INFINITY);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartFiniteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.POSITIVE_INFINITY));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyFiniteConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<String> result = codec.encodeKey(Double.POSITIVE_INFINITY);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyFiniteConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Result<Double> result = codec.decodeKey("Infinity");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotNaNConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotNaNConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotNaNConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotNaNConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotNaNConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Double.NaN);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotNaNConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.NaN));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotNaNConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		Result<String> result = codec.encodeKey(Double.NaN);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotNaNConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		Result<Double> result = codec.decodeKey("NaN");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyIntegralConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		Result<String> result = codec.encodeKey(42.0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyIntegralConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		Result<Double> result = codec.decodeKey("42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyIntegralConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyIntegralConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.5);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0.5));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNormalizedConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		Result<String> result = codec.encodeKey(0.5);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNormalizedConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		Result<Double> result = codec.decodeKey("0.5");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNormalizedConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		Result<String> result = codec.encodeKey(3.14);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNormalizedConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		Result<Double> result = codec.decodeKey("3.14");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> value % 2.0 == 0.0 ? Result.success(null) : Result.error("Value must be even")
		);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42.0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> value % 2.0 == 0.0 ? Result.success(null) : Result.error("Value must be even")
		);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> value % 2.0 == 0.0 ? Result.success(null) : Result.error("Value must be even")
		);
		
		Result<String> result = codec.encodeKey(42.0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> value % 2.0 == 0.0 ? Result.success(null) : Result.error("Value must be even")
		);
		
		Result<Double> result = codec.decodeKey("42.0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> value % 2.0 == 0.0 ? Result.success(null) : Result.error("Value must be even")
		);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> value % 2.0 == 0.0 ? Result.success(null) : Result.error("Value must be even")
		);
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> value % 2.0 == 0.0 ? Result.success(null) : Result.error("Value must be even")
		);
		
		Result<String> result = codec.encodeKey(3.0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> value % 2.0 == 0.0 ? Result.success(null) : Result.error("Value must be even")
		);
		
		Result<Double> result = codec.decodeKey("3.0");
		assertTrue(result.isError());
	}
	
}

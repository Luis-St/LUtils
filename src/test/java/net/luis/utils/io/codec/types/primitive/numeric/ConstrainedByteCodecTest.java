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
 * Test class for {@link ByteCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedByteCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 42);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive((byte) 42), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 42));
		assertTrue(result.isSuccess());
		assertEquals((byte) 42, result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<String> result = codec.encodeKey((byte) 42);
		assertTrue(result.isSuccess());
		assertEquals("42", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<Byte> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
		assertEquals((byte) 42, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Byte> codec = Codecs.BYTE;
		assertFalse(codec.toString().contains("Constrained"));
		assertEquals("ByteCodec", codec.toString());
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		Result<String> result = codec.encodeKey((byte) 10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		Result<Byte> result = codec.decodeKey("10");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 20);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 20));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		Result<String> result = codec.encodeKey((byte) 20);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		Result<Byte> result = codec.decodeKey("20");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 20);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 20));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		Result<String> result = codec.encodeKey((byte) 20);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		Result<Byte> result = codec.decodeKey("20");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		Result<String> result = codec.encodeKey((byte) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		Result<Byte> result = codec.decodeKey("10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20, (byte) 30));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 20);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20, (byte) 30));
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 20));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20, (byte) 30));
		
		Result<String> result = codec.encodeKey((byte) 20);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20, (byte) 30));
		
		Result<Byte> result = codec.decodeKey("20");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 30);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20));
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 30));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20));
		
		Result<String> result = codec.encodeKey((byte) 30);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20));
		
		Result<Byte> result = codec.decodeKey("30");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 30);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 30));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		Result<String> result = codec.encodeKey((byte) 30);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		Result<Byte> result = codec.decodeKey("30");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		Result<String> result = codec.encodeKey((byte) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		Result<Byte> result = codec.decodeKey("10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 20);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 20));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		Result<String> result = codec.encodeKey((byte) 20);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		Result<Byte> result = codec.decodeKey("20");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		Result<String> result = codec.encodeKey((byte) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		Result<Byte> result = codec.decodeKey("10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		Result<String> result = codec.encodeKey((byte) 10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		Result<Byte> result = codec.decodeKey("10");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 5));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		Result<String> result = codec.encodeKey((byte) 5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		Result<Byte> result = codec.decodeKey("5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 30);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 30));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		Result<String> result = codec.encodeKey((byte) 30);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		Result<Byte> result = codec.decodeKey("30");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		Result<String> result = codec.encodeKey((byte) 50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		Result<Byte> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		Result<String> result = codec.encodeKey((byte) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		Result<Byte> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 60);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 60));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		Result<String> result = codec.encodeKey((byte) 60);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		Result<Byte> result = codec.decodeKey("60");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 30);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 30));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		Result<String> result = codec.encodeKey((byte) 30);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		Result<Byte> result = codec.decodeKey("30");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		Result<String> result = codec.encodeKey((byte) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		Result<Byte> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		Result<String> result = codec.encodeKey((byte) 30);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		Result<Byte> result = codec.decodeKey("30");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 60));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		Result<String> result = codec.encodeKey((byte) 5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		Result<Byte> result = codec.decodeKey("60");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 42));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<String> result = codec.encodeKey((byte) 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<Byte> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -5));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<String> result = codec.encodeKey((byte) 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Result<Byte> result = codec.decodeKey("-5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -10));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		Result<String> result = codec.encodeKey((byte) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		Result<Byte> result = codec.decodeKey("-10");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 5));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		Result<String> result = codec.encodeKey((byte) 5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		Result<Byte> result = codec.decodeKey("5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) -10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -10));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		Result<String> result = codec.encodeKey((byte) -10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		Result<Byte> result = codec.decodeKey("-10");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 5));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		Result<String> result = codec.encodeKey((byte) 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		Result<Byte> result = codec.decodeKey("5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		Result<String> result = codec.encodeKey((byte) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		Result<Byte> result = codec.decodeKey("10");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) -5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -5));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		Result<String> result = codec.encodeKey((byte) -5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		Result<Byte> result = codec.decodeKey("-5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		Result<String> result = codec.encodeKey((byte) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		Result<Byte> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -5));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		Result<String> result = codec.encodeKey((byte) 5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		Result<Byte> result = codec.decodeKey("-5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -10));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		Result<String> result = codec.encodeKey((byte) 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		Result<Byte> result = codec.decodeKey("-10");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		Result<String> result = codec.encodeKey((byte) 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		Result<Byte> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		Result<String> result = codec.encodeKey((byte) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		Result<Byte> result = codec.decodeKey("75");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 101);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -1));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		Result<String> result = codec.encodeKey((byte) 101);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		Result<Byte> result = codec.decodeKey("-1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.even();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.even();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEvenConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.even();
		
		Result<String> result = codec.encodeKey((byte) -10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEvenConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.even();
		
		Result<Byte> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.even();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 41);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.even();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 3));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEvenConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.even();
		
		Result<String> result = codec.encodeKey((byte) 41);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEvenConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.even();
		
		Result<Byte> result = codec.decodeKey("3");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 41);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyOddConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		Result<String> result = codec.encodeKey((byte) -11);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyOddConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		Result<Byte> result = codec.decodeKey("99");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyOddConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		Result<String> result = codec.encodeKey((byte) 42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyOddConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		Result<Byte> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 25);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyDivisibleByConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		Result<String> result = codec.encodeKey((byte) 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyDivisibleByConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		Result<Byte> result = codec.decodeKey("-15");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 23);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 17));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyDivisibleByConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		Result<String> result = codec.encodeKey((byte) 23);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyDivisibleByConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		Result<Byte> result = codec.decodeKey("17");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 27);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 9));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPowerOfConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		Result<String> result = codec.encodeKey((byte) 81);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPowerOfConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		Result<Byte> result = codec.decodeKey("1");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 15));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPowerOfConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		Result<String> result = codec.encodeKey((byte) 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPowerOfConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		Result<Byte> result = codec.decodeKey("15");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 64);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 32));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		Result<String> result = codec.encodeKey((byte) 16);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		Result<Byte> result = codec.decodeKey("1");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 100));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		Result<String> result = codec.encodeKey((byte) 50);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		Result<Byte> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		});
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 21);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		});
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 49));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		});
		
		Result<String> result = codec.encodeKey((byte) 14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		});
		
		Result<Byte> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		});
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 22);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		});
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		});
		
		Result<String> result = codec.encodeKey((byte) 22);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		});
		
		Result<Byte> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
}

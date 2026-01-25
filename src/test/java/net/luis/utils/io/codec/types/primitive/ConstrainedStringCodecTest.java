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

package net.luis.utils.io.codec.types.primitive;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.io.codec.constraint.config.StringConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedStringCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(1)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("hello"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(1)));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(1)));
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(1)));
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(1)));
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualTo("expected"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "expected");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualTo("expected"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("expected"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualTo("expected"));
		
		Result<String> result = codec.encodeKey("expected");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualTo("expected"));
		
		Result<String> result = codec.decodeKey("expected");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualTo("expected"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "different");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualTo("expected"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("different"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualTo("expected"));
		
		Result<String> result = codec.encodeKey("different");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualTo("expected"));
		
		Result<String> result = codec.decodeKey("different");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEqualTo("forbidden"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "allowed");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEqualTo("forbidden"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("allowed"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEqualTo("forbidden"));
		
		Result<String> result = codec.encodeKey("allowed");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEqualTo("forbidden"));
		
		Result<String> result = codec.decodeKey("allowed");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEqualTo("forbidden"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "forbidden");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEqualTo("forbidden"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("forbidden"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEqualTo("forbidden"));
		
		Result<String> result = codec.encodeKey("forbidden");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEqualTo("forbidden"));
		
		Result<String> result = codec.decodeKey("forbidden");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withIn(Set.of("apple", "banana", "cherry")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "banana");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withIn(Set.of("apple", "banana", "cherry")));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("banana"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withIn(Set.of("apple", "banana", "cherry")));
		
		Result<String> result = codec.encodeKey("banana");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withIn(Set.of("apple", "banana", "cherry")));
		
		Result<String> result = codec.decodeKey("banana");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withIn(Set.of("apple", "banana", "cherry")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "mango");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withIn(Set.of("apple", "banana", "cherry")));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("mango"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withIn(Set.of("apple", "banana", "cherry")));
		
		Result<String> result = codec.encodeKey("mango");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withIn(Set.of("apple", "banana", "cherry")));
		
		Result<String> result = codec.decodeKey("mango");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotIn(Set.of("forbidden1", "forbidden2")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "allowed");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotIn(Set.of("forbidden1", "forbidden2")));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("allowed"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotIn(Set.of("forbidden1", "forbidden2")));
		
		Result<String> result = codec.encodeKey("allowed");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotIn(Set.of("forbidden1", "forbidden2")));
		
		Result<String> result = codec.decodeKey("allowed");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotIn(Set.of("forbidden1", "forbidden2")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "forbidden1");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotIn(Set.of("forbidden1", "forbidden2")));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("forbidden1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotIn(Set.of("forbidden1", "forbidden2")));
		
		Result<String> result = codec.encodeKey("forbidden1");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotIn(Set.of("forbidden1", "forbidden2")));
		
		Result<String> result = codec.decodeKey("forbidden1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(3)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(3)));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyMinLengthConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(3)));
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyMinLengthConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(3)));
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(5)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "ab");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(5)));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("ab"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyMinLengthConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(5)));
		
		Result<String> result = codec.encodeKey("ab");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyMinLengthConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMinLength(5)));
		
		Result<String> result = codec.decodeKey("ab");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(10)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(10)));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyMaxLengthConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(10)));
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyMaxLengthConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(10)));
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(3)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(3)));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyMaxLengthConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(3)));
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyMaxLengthConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withMaxLength(3)));
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withExactLength(5)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withExactLength(5)));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyExactLengthConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withExactLength(5)));
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyExactLengthConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withExactLength(5)));
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withExactLength(5)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hi");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withExactLength(5)));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hi"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyExactLengthConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withExactLength(5)));
		
		Result<String> result = codec.encodeKey("hi");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyExactLengthConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withLength(LengthConstraintConfig.UNCONSTRAINED.withExactLength(5)));
		
		Result<String> result = codec.decodeKey("hi");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartStartsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWith("pre_"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "pre_value");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartStartsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWith("pre_"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("pre_value"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyStartsWithConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWith("pre_"));
		
		Result<String> result = codec.encodeKey("pre_value");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyStartsWithConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWith("pre_"));
		
		Result<String> result = codec.decodeKey("pre_value");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartStartsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWith("pre_"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "value");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartStartsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWith("pre_"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("value"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyStartsWithConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWith("pre_"));
		
		Result<String> result = codec.encodeKey("value");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyStartsWithConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWith("pre_"));
		
		Result<String> result = codec.decodeKey("value");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotStartsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotStartsWith("bad_"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "good_value");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotStartsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotStartsWith("bad_"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("good_value"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotStartsWithConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotStartsWith("bad_"));
		
		Result<String> result = codec.encodeKey("good_value");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotStartsWithConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotStartsWith("bad_"));
		
		Result<String> result = codec.decodeKey("good_value");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotStartsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotStartsWith("bad_"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "bad_value");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotStartsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotStartsWith("bad_"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("bad_value"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotStartsWithConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotStartsWith("bad_"));
		
		Result<String> result = codec.encodeKey("bad_value");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotStartsWithConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotStartsWith("bad_"));
		
		Result<String> result = codec.decodeKey("bad_value");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEndsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWith(".txt"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "file.txt");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEndsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWith(".txt"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("file.txt"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEndsWithConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWith(".txt"));
		
		Result<String> result = codec.encodeKey("file.txt");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEndsWithConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWith(".txt"));
		
		Result<String> result = codec.decodeKey("file.txt");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEndsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWith(".txt"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "file.pdf");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEndsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWith(".txt"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("file.pdf"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEndsWithConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWith(".txt"));
		
		Result<String> result = codec.encodeKey("file.pdf");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEndsWithConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWith(".txt"));
		
		Result<String> result = codec.decodeKey("file.pdf");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEndsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEndsWith(".tmp"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "file.txt");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEndsWithConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEndsWith(".tmp"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("file.txt"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEndsWithConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEndsWith(".tmp"));
		
		Result<String> result = codec.encodeKey("file.txt");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEndsWithConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEndsWith(".tmp"));
		
		Result<String> result = codec.decodeKey("file.txt");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEndsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEndsWith(".tmp"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "file.tmp");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEndsWithConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEndsWith(".tmp"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("file.tmp"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEndsWithConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEndsWith(".tmp"));
		
		Result<String> result = codec.encodeKey("file.tmp");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEndsWithConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotEndsWith(".tmp"));
		
		Result<String> result = codec.decodeKey("file.tmp");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartContainsConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withContains("test"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "this_test_works");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartContainsConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withContains("test"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("this_test_works"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyContainsConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withContains("test"));
		
		Result<String> result = codec.encodeKey("this_test_works");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyContainsConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withContains("test"));
		
		Result<String> result = codec.decodeKey("this_test_works");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartContainsConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withContains("test"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "no_match_here");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartContainsConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withContains("test"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("no_match_here"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyContainsConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withContains("test"));
		
		Result<String> result = codec.encodeKey("no_match_here");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyContainsConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withContains("test"));
		
		Result<String> result = codec.decodeKey("no_match_here");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotContainsConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotContains("forbidden"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "allowed_text");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotContainsConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotContains("forbidden"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("allowed_text"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotContainsConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotContains("forbidden"));
		
		Result<String> result = codec.encodeKey("allowed_text");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotContainsConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotContains("forbidden"));
		
		Result<String> result = codec.decodeKey("allowed_text");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotContainsConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotContains("forbidden"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "this_is_forbidden_text");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotContainsConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotContains("forbidden"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("this_is_forbidden_text"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotContainsConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotContains("forbidden"));
		
		Result<String> result = codec.encodeKey("this_is_forbidden_text");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotContainsConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotContains("forbidden"));
		
		Result<String> result = codec.decodeKey("this_is_forbidden_text");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMatchesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withMatches("^[a-z]+$"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMatchesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withMatches("^[a-z]+$"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyMatchesConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withMatches("^[a-z]+$"));
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyMatchesConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withMatches("^[a-z]+$"));
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMatchesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withMatches("^[a-z]+$"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "Hello123");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMatchesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withMatches("^[a-z]+$"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Hello123"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyMatchesConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withMatches("^[a-z]+$"));
		
		Result<String> result = codec.encodeKey("Hello123");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyMatchesConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withMatches("^[a-z]+$"));
		
		Result<String> result = codec.decodeKey("Hello123");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotMatchesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotMatches("^[0-9]+$"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotMatchesConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotMatches("^[0-9]+$"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotMatchesConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotMatches("^[0-9]+$"));
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotMatchesConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotMatches("^[0-9]+$"));
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotMatchesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotMatches("^[0-9]+$"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "12345");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotMatchesConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withNotMatches("^[0-9]+$"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("12345"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotMatchesConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotMatches("^[0-9]+$"));
		
		Result<String> result = codec.encodeKey("12345");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotMatchesConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withNotMatches("^[0-9]+$"));
		
		Result<String> result = codec.decodeKey("12345");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartTrimmedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withTrimmed);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartTrimmedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withTrimmed);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyTrimmedConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withTrimmed);
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyTrimmedConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withTrimmed);
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartTrimmedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withTrimmed);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "  hello  ");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartTrimmedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withTrimmed);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("  hello  "));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyTrimmedConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withTrimmed);
		
		Result<String> result = codec.encodeKey("  hello  ");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyTrimmedConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withTrimmed);
		
		Result<String> result = codec.decodeKey("  hello  ");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotBlankConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNotBlank);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotBlankConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNotBlank);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotBlankConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNotBlank);
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotBlankConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNotBlank);
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotBlankConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNotBlank);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "   ");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotBlankConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNotBlank);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("   "));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotBlankConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNotBlank);
		
		Result<String> result = codec.encodeKey("   ");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotBlankConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNotBlank);
		
		Result<String> result = codec.decodeKey("   ");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartUpperCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withUpperCase);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "HELLO");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartUpperCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withUpperCase);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("HELLO"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyUpperCaseConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withUpperCase);
		
		Result<String> result = codec.encodeKey("HELLO");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyUpperCaseConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withUpperCase);
		
		Result<String> result = codec.decodeKey("HELLO");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartUpperCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withUpperCase);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "Hello");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartUpperCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withUpperCase);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Hello"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyUpperCaseConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withUpperCase);
		
		Result<String> result = codec.encodeKey("Hello");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyUpperCaseConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withUpperCase);
		
		Result<String> result = codec.decodeKey("Hello");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLowerCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withLowerCase);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLowerCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withLowerCase);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLowerCaseConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withLowerCase);
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLowerCaseConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withLowerCase);
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLowerCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withLowerCase);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "Hello");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLowerCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withLowerCase);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Hello"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLowerCaseConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withLowerCase);
		
		Result<String> result = codec.encodeKey("Hello");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLowerCaseConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withLowerCase);
		
		Result<String> result = codec.decodeKey("Hello");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNumericConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNumeric);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "12345");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNumericConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNumeric);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("12345"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNumericConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNumeric);
		
		Result<String> result = codec.encodeKey("12345");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNumericConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNumeric);
		
		Result<String> result = codec.decodeKey("12345");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNumeric);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNumeric);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNumericConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNumeric);
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNumericConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withNumeric);
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAlphabeticConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphabetic);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartAlphabeticConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphabetic);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyAlphabeticConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphabetic);
		
		Result<String> result = codec.encodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyAlphabeticConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphabetic);
		
		Result<String> result = codec.decodeKey("hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAlphabeticConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphabetic);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello123");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAlphabeticConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphabetic);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello123"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyAlphabeticConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphabetic);
		
		Result<String> result = codec.encodeKey("hello123");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyAlphabeticConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphabetic);
		
		Result<String> result = codec.decodeKey("hello123");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAlphanumericConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphanumeric);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello123");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartAlphanumericConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphanumeric);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello123"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyAlphanumericConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphanumeric);
		
		Result<String> result = codec.encodeKey("hello123");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyAlphanumericConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphanumeric);
		
		Result<String> result = codec.decodeKey("hello123");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAlphanumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphanumeric);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello@123");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAlphanumericConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphanumeric);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello@123"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyAlphanumericConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphanumeric);
		
		Result<String> result = codec.encodeKey("hello@123");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyAlphanumericConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAlphanumeric);
		
		Result<String> result = codec.decodeKey("hello@123");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAsciiConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAscii);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "Hello World!");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartAsciiConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAscii);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Hello World!"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyAsciiConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAscii);
		
		Result<String> result = codec.encodeKey("Hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyAsciiConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAscii);
		
		Result<String> result = codec.decodeKey("Hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAsciiConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAscii);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "Hello \u4E16\u754C");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAsciiConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAscii);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Hello \u4E16\u754C"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyAsciiConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAscii);
		
		Result<String> result = codec.encodeKey("Hello\u00E9");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyAsciiConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(StringConstraintConfig::withAscii);
		
		Result<String> result = codec.decodeKey("Hello\u00E9");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEqualToIgnoreCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualToIgnoreCase("hello"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "HELLO");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToIgnoreCaseConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualToIgnoreCase("hello"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("HeLLo"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToIgnoreCaseConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualToIgnoreCase("hello"));
		
		Result<String> result = codec.encodeKey("Hello");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToIgnoreCaseConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualToIgnoreCase("hello"));
		
		Result<String> result = codec.decodeKey("hElLo");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToIgnoreCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualToIgnoreCase("hello"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "world");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToIgnoreCaseConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualToIgnoreCase("hello"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("world"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToIgnoreCaseConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualToIgnoreCase("hello"));
		
		Result<String> result = codec.encodeKey("world");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToIgnoreCaseConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEqualToIgnoreCase("hello"));
		
		Result<String> result = codec.decodeKey("world");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartStartsWithAnyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWithAny(List.of("http://", "https://", "ftp://")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "https://example.com");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartStartsWithAnyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWithAny(List.of("http://", "https://", "ftp://")));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("http://example.com"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyStartsWithAnyConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWithAny(List.of("http://", "https://", "ftp://")));
		
		Result<String> result = codec.encodeKey("ftp://server.com");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyStartsWithAnyConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWithAny(List.of("http://", "https://", "ftp://")));
		
		Result<String> result = codec.decodeKey("https://secure.com");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartStartsWithAnyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWithAny(List.of("http://", "https://", "ftp://")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "ssh://example.com");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartStartsWithAnyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWithAny(List.of("http://", "https://", "ftp://")));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("file://local"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyStartsWithAnyConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWithAny(List.of("http://", "https://", "ftp://")));
		
		Result<String> result = codec.encodeKey("mailto:test@test.com");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyStartsWithAnyConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withStartsWithAny(List.of("http://", "https://", "ftp://")));
		
		Result<String> result = codec.decodeKey("ws://websocket.com");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEndsWithAnyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWithAny(List.of(".txt", ".pdf", ".doc")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "document.pdf");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEndsWithAnyConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWithAny(List.of(".txt", ".pdf", ".doc")));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("file.txt"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEndsWithAnyConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWithAny(List.of(".txt", ".pdf", ".doc")));
		
		Result<String> result = codec.encodeKey("report.doc");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEndsWithAnyConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWithAny(List.of(".txt", ".pdf", ".doc")));
		
		Result<String> result = codec.decodeKey("readme.txt");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEndsWithAnyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWithAny(List.of(".txt", ".pdf", ".doc")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "image.png");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEndsWithAnyConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWithAny(List.of(".txt", ".pdf", ".doc")));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("image.jpg"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEndsWithAnyConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWithAny(List.of(".txt", ".pdf", ".doc")));
		
		Result<String> result = codec.encodeKey("video.mp4");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEndsWithAnyConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withEndsWithAny(List.of(".txt", ".pdf", ".doc")));
		
		Result<String> result = codec.decodeKey("archive.zip");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartContainsAllConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withContainsAll(List.of("foo", "bar")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "foo_bar_baz");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartContainsAllConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withContainsAll(List.of("foo", "bar")));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("bar_test_foo"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyContainsAllConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withContainsAll(List.of("foo", "bar")));
		
		Result<String> result = codec.encodeKey("foobar");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyContainsAllConstraintSuccess() {
		Codec<String> codec = new StringCodec().apply(config -> config.withContainsAll(List.of("foo", "bar")));
		
		Result<String> result = codec.decodeKey("barfoo");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartContainsAllConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withContainsAll(List.of("foo", "bar")));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "foo_only");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartContainsAllConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec().apply(config -> config.withContainsAll(List.of("foo", "bar")));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("bar_only"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyContainsAllConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withContainsAll(List.of("foo", "bar")));
		
		Result<String> result = codec.encodeKey("neither");
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyContainsAllConstraintViolation() {
		Codec<String> codec = new StringCodec().apply(config -> config.withContainsAll(List.of("foo", "bar")));
		
		Result<String> result = codec.decodeKey("something_else");
		assertTrue(result.isError());
	}
}

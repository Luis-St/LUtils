/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringCodec} focusing on length constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedStringCodecTest {
	
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	@Test
	void encodeWithMinLengthConstraintSuccess() {
		Codec<String> codec = STRING.minLength(2);
		String value = "hello";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMinLengthConstraintFailure() {
		Codec<String> codec = STRING.minLength(10);
		String value = "hi";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithMaxLengthConstraintSuccess() {
		Codec<String> codec = STRING.maxLength(10);
		String value = "hello";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMaxLengthConstraintFailure() {
		Codec<String> codec = STRING.maxLength(3);
		String value = "hello";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintSuccess() {
		Codec<String> codec = STRING.exactLength(5);
		String value = "hello";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooShort() {
		Codec<String> codec = STRING.exactLength(10);
		String value = "hi";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooLong() {
		Codec<String> codec = STRING.exactLength(2);
		String value = "hello";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintSuccess() {
		Codec<String> codec = STRING.lengthBetween(3, 10);
		String value = "hello";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureBelow() {
		Codec<String> codec = STRING.lengthBetween(10, 20);
		String value = "hi";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureAbove() {
		Codec<String> codec = STRING.lengthBetween(1, 3);
		String value = "hello";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<String> codec = STRING.empty();
		String value = "";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<String> codec = STRING.empty();
		String value = "a";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<String> codec = STRING.notEmpty();
		String value = "hello";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<String> codec = STRING.notEmpty();
		String value = "";
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMinLengthConstraintSuccess() {
		Codec<String> codec = STRING.minLength(2);
		JsonPrimitive json = new JsonPrimitive("hello");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
	}
	
	@Test
	void decodeWithMinLengthConstraintFailure() {
		Codec<String> codec = STRING.minLength(10);
		JsonPrimitive json = new JsonPrimitive("hi");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMaxLengthConstraintSuccess() {
		Codec<String> codec = STRING.maxLength(10);
		JsonPrimitive json = new JsonPrimitive("hello");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
	}
	
	@Test
	void decodeWithMaxLengthConstraintFailure() {
		Codec<String> codec = STRING.maxLength(3);
		JsonPrimitive json = new JsonPrimitive("hello");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void decodeWithExactLengthConstraintSuccess() {
		Codec<String> codec = STRING.exactLength(5);
		JsonPrimitive json = new JsonPrimitive("hello");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
	}
	
	@Test
	void decodeWithExactLengthConstraintFailure() {
		Codec<String> codec = STRING.exactLength(10);
		JsonPrimitive json = new JsonPrimitive("hi");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithLengthBetweenConstraintSuccess() {
		Codec<String> codec = STRING.lengthBetween(3, 10);
		JsonPrimitive json = new JsonPrimitive("hello");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
	}
	
	@Test
	void decodeWithLengthBetweenConstraintFailure() {
		Codec<String> codec = STRING.lengthBetween(10, 20);
		JsonPrimitive json = new JsonPrimitive("hi");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<String> codec = STRING.empty();
		JsonPrimitive json = new JsonPrimitive("");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals("", result.resultOrThrow());
	}
	
	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<String> codec = STRING.empty();
		JsonPrimitive json = new JsonPrimitive("a");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<String> codec = STRING.notEmpty();
		JsonPrimitive json = new JsonPrimitive("hello");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
	}
	
	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<String> codec = STRING.notEmpty();
		JsonPrimitive json = new JsonPrimitive("");
		
		Result<String> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<String> codec = STRING;
		String str = codec.toString();
		assertEquals("StringCodec", str);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<String> codec = STRING.minLength(1).maxLength(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedStringCodec["));
		assertTrue(str.contains("constraints="));
	}
}

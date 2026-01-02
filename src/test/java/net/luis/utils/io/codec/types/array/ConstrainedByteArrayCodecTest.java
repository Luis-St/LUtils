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

package net.luis.utils.io.codec.types.array;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ByteArrayCodec} focusing on length constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedByteArrayCodecTest {
	
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	@Test
	void encodeWithMinLengthConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(2);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMinLengthConstraintFailure() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(5);
		byte[] array = { 1, 2 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithMaxLengthConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.maxLength(5);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMaxLengthConstraintFailure() {
		Codec<byte[]> codec = BYTE_ARRAY.maxLength(2);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.exactLength(3);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooSmall() {
		Codec<byte[]> codec = BYTE_ARRAY.exactLength(5);
		byte[] array = { 1, 2 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooLarge() {
		Codec<byte[]> codec = BYTE_ARRAY.exactLength(2);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.lengthBetween(2, 5);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureBelow() {
		Codec<byte[]> codec = BYTE_ARRAY.lengthBetween(5, 10);
		byte[] array = { 1, 2 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureAbove() {
		Codec<byte[]> codec = BYTE_ARRAY.lengthBetween(1, 2);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.empty();
		byte[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<byte[]> codec = BYTE_ARRAY.empty();
		byte[] array = { 1 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.notEmpty();
		byte[] array = { 1, 2 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<byte[]> codec = BYTE_ARRAY.notEmpty();
		byte[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMinLengthConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithMinLengthConstraintFailure() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMaxLengthConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithMaxLengthConstraintFailure() {
		Codec<byte[]> codec = BYTE_ARRAY.maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void decodeWithExactLengthConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithExactLengthConstraintFailure() {
		Codec<byte[]> codec = BYTE_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithLengthBetweenConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.lengthBetween(2, 5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithLengthBetweenConstraintFailure() {
		Codec<byte[]> codec = BYTE_ARRAY.lengthBetween(5, 10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.empty();
		JsonArray array = new JsonArray();
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<byte[]> codec = BYTE_ARRAY.empty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<byte[]> codec = BYTE_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(2).maxLength(5);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMin() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(5).maxLength(10);
		byte[] array = { 1, 2 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMax() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(1).maxLength(2);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithOverwrittenMinConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(10).minLength(2);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenMaxConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.maxLength(2).maxLength(5);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenExactConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.exactLength(5).exactLength(3);
		byte[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMin() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(5).maxLength(10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMax() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(1).maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void decodeWithOverwrittenMinConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(10).minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithOverwrittenMaxConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.maxLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithOverwrittenExactConstraintSuccess() {
		Codec<byte[]> codec = BYTE_ARRAY.exactLength(5).exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<byte[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<byte[]> codec = BYTE_ARRAY;
		String str = codec.toString();
		assertEquals("ByteArrayCodec", str);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<byte[]> codec = BYTE_ARRAY.minLength(1).maxLength(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedByteArrayCodec["));
		assertTrue(str.contains("constraints="));
	}
}

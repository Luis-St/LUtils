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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for constrained {@link CharacterArrayCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedCharacterArrayCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMinLength(1));
		char[] validArray = new char[] { 'a', 'b', 'c' };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), validArray);
		assertTrue(result.isSuccess());
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive('a'));
		expected.add(new JsonPrimitive('b'));
		expected.add(new JsonPrimitive('c'));
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMinLength(1));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new char[] { 'a', 'b' }, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMinLength(1));
		String str = codec.toString();
		assertTrue(str.contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withEqualTo(new char[] { 'a', 'b', 'c' }));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withEqualTo(new char[] { 'a', 'b', 'c' }));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'd', 'e' });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withEqualTo(new char[] { 'a', 'b', 'c' }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		array.add(new JsonPrimitive('c'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withEqualTo(new char[] { 'a', 'b', 'c' }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('d'));
		array.add(new JsonPrimitive('e'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withNotEqualTo(new char[] { 'a', 'b', 'c' }));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'd', 'e', 'f' });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withNotEqualTo(new char[] { 'a', 'b', 'c' }));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withNotEqualTo(new char[] { 'a', 'b', 'c' }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('d'));
		array.add(new JsonPrimitive('e'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withNotEqualTo(new char[] { 'a', 'b', 'c' }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		array.add(new JsonPrimitive('c'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' })));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b' });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' })));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'e', 'f' });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' })));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('c'));
		array.add(new JsonPrimitive('d'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' })));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('g'));
		array.add(new JsonPrimitive('h'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withNotIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' })));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'e', 'f' });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withNotIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' })));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b' });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withNotIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' })));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('g'));
		array.add(new JsonPrimitive('h'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withNotIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' })));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('c'));
		array.add(new JsonPrimitive('d'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMinLength(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMinLength(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b' });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMinLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMinLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMaxLength(5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMaxLength(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMaxLength(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMaxLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		array.add(new JsonPrimitive('c'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withExactLength(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withExactLength(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b' });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withExactLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withExactLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLengthBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withLengthBetween(2, 4));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLengthBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withLengthBetween(3, 5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b' });
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLengthBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withLengthBetween(1, 2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLengthBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withLengthBetween(1, 3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLengthBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withLengthBetween(3, 5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withCustom(arr -> {
			for (char c : arr) {
				if (c < 'a' || c > 'z') {
					return Result.error("All characters must be lowercase letters");
				}
			}
			return Result.success(null);
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withCustom(arr -> {
			for (char c : arr) {
				if (c < 'a' || c > 'z') {
					return Result.error("All characters must be lowercase letters");
				}
			}
			return Result.success(null);
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'B', 'c' });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withCustom(arr -> {
			for (char c : arr) {
				if (c < 'a' || c > 'z') {
					return Result.error("All characters must be lowercase letters");
				}
			}
			return Result.success(null);
		}));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withCustom(arr -> {
			for (char c : arr) {
				if (c < 'a' || c > 'z') {
					return Result.error("All characters must be lowercase letters");
				}
			}
			return Result.success(null);
		}));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('B'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMinLength(2).withMaxLength(4));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMinLength(2).withMaxLength(4));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new char[] { 'a' });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMinLength(1).withMaxLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec().apply(config -> config.withMinLength(3).withMaxLength(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		Result<char[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
}

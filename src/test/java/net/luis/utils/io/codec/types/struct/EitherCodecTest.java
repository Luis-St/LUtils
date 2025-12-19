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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.Either;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EitherCodec}.<br>
 *
 * @author Luis-St
 */
class EitherCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new EitherCodec<>(null, BOOLEAN));
		assertThrows(NullPointerException.class, () -> new EitherCodec<>(INTEGER, null));
		assertDoesNotThrow(() -> new EitherCodec<>(INTEGER, BOOLEAN));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		Either<Integer, Boolean> left = Either.left(1);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), left));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, left));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null value as either"));
	}
	
	@Test
	void encodeStartWithLeftValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		Either<Integer, Boolean> left = Either.left(42);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), left);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithRightValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		Either<Integer, Boolean> right = Either.right(true);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), right);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(true), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<String, Double>> codec = new EitherCodec<>(STRING, DOUBLE);
		
		Either<String, Double> leftString = Either.left("hello");
		Result<JsonElement> leftResult = codec.encodeStart(typeProvider, typeProvider.empty(), leftString);
		assertTrue(leftResult.isSuccess());
		assertEquals(new JsonPrimitive("hello"), leftResult.resultOrThrow());
		
		Either<String, Double> rightDouble = Either.right(3.14);
		Result<JsonElement> rightResult = codec.encodeStart(typeProvider, typeProvider.empty(), rightDouble);
		assertTrue(rightResult.isSuccess());
		assertEquals(new JsonPrimitive(3.14), rightResult.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNullLeftValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Optional<Integer>, Boolean>> codec = new EitherCodec<>(INTEGER.optional(), BOOLEAN);
		Either<Optional<Integer>, Boolean> left = Either.left(null);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), left);
		assertTrue(result.isSuccess());
		JsonElement element = result.resultOrThrow();
		assertFalse(element.isJsonNull());
		assertFalse(element.isJsonPrimitive());
		assertFalse(element.isJsonArray());
		assertFalse(element.isJsonObject());
	}
	
	@Test
	void encodeStartWithNullRightValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Optional<Boolean>>> codec = new EitherCodec<>(INTEGER, BOOLEAN.optional());
		Either<Integer, Optional<Boolean>> right = Either.right(null);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), right);
		assertTrue(result.isSuccess());
		JsonElement element = result.resultOrThrow();
		assertFalse(element.isJsonNull());
		assertFalse(element.isJsonPrimitive());
		assertFalse(element.isJsonArray());
		assertFalse(element.isJsonObject());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive(1)));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		Result<Either<Integer, Boolean>> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as either"));
	}
	
	@Test
	void decodeStartWithFirstCodecMatch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		Result<Either<Integer, Boolean>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals(Either.left(42), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithSecondCodecMatch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		Result<Either<Integer, Boolean>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(true));
		assertTrue(result.isSuccess());
		assertEquals(Either.right(true), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNoMatch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		Result<Either<Integer, Boolean>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("not-int-or-bool"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode value as either"));
	}
	
	@Test
	void decodeStartPrefersFirstCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<String, String>> codec = new EitherCodec<>(STRING, STRING);
		
		Result<Either<String, String>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isLeft());
		assertEquals("hello", result.resultOrThrow().leftOrThrow());
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<String, Double>> codec = new EitherCodec<>(STRING, DOUBLE);
		
		Result<Either<String, Double>> stringResult = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("text"));
		assertTrue(stringResult.isSuccess());
		assertTrue(stringResult.resultOrThrow().isLeft());
		assertEquals("text", stringResult.resultOrThrow().leftOrThrow());
		
		Result<Either<String, Double>> doubleResult = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(doubleResult.isSuccess());
		assertTrue(doubleResult.resultOrThrow().isRight());
		assertEquals(3.14, doubleResult.resultOrThrow().rightOrThrow());
	}
	
	@Test
	void decodeStartWithOverlappingTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Double, Integer>> codec = new EitherCodec<>(DOUBLE, INTEGER);
		
		Result<Either<Double, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isLeft());
		assertEquals(42.0, result.resultOrThrow().leftOrThrow());
	}
	
	@Test
	void decodeStartWithOptionalCodecs() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Optional<Integer>, String>> codec = new EitherCodec<>(INTEGER.optional(), STRING);
		
		Result<Either<Optional<Integer>, String>> intResult = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(intResult.isSuccess());
		assertTrue(intResult.resultOrThrow().isLeft());
		assertEquals(42, intResult.resultOrThrow().leftOrThrow().orElseThrow());
		
		Result<Either<Optional<Integer>, String>> stringResult = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("text"));
		assertTrue(stringResult.isSuccess());
		assertTrue(stringResult.resultOrThrow().isLeft());
		assertTrue(stringResult.resultOrThrow().leftOrThrow().isEmpty());
	}
	
	@Test
	void equalsAndHashCode() {
		EitherCodec<Integer, Boolean> codec1 = new EitherCodec<>(INTEGER, BOOLEAN);
		EitherCodec<Integer, Boolean> codec2 = new EitherCodec<>(INTEGER, BOOLEAN);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		EitherCodec<Integer, Boolean> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		String result = codec.toString();
		
		assertTrue(result.startsWith("EitherCodec["));
		assertTrue(result.endsWith("]"));
		assertTrue(result.contains(","));
	}
}

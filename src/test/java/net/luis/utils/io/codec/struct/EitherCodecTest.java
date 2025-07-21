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

package net.luis.utils.io.codec.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.Either;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EitherCodec}.<br>
 *
 * @author Luis-St
 */
class EitherCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new EitherCodec<>(null, Codec.BOOLEAN));
		assertThrows(NullPointerException.class, () -> new EitherCodec<>(Codec.INTEGER, null));
		assertDoesNotThrow(() -> new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		Either<Integer, Boolean> left = Either.left(1);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), left));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, left));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null value as either"));
	}
	
	@Test
	void encodeStartWithLeftValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		Either<Integer, Boolean> left = Either.left(42);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), left);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.orThrow());
	}
	
	@Test
	void encodeStartWithRightValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		Either<Integer, Boolean> right = Either.right(true);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), right);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(true), result.orThrow());
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<String, Double>> codec = new EitherCodec<>(Codec.STRING, Codec.DOUBLE);
		
		Either<String, Double> leftString = Either.left("hello");
		Result<JsonElement> leftResult = codec.encodeStart(typeProvider, typeProvider.empty(), leftString);
		assertTrue(leftResult.isSuccess());
		assertEquals(new JsonPrimitive("hello"), leftResult.orThrow());
		
		Either<String, Double> rightDouble = Either.right(3.14);
		Result<JsonElement> rightResult = codec.encodeStart(typeProvider, typeProvider.empty(), rightDouble);
		assertTrue(rightResult.isSuccess());
		assertEquals(new JsonPrimitive(3.14), rightResult.orThrow());
	}
	
	@Test
	void encodeStartWithNullLeftValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Optional<Integer>, Boolean>> codec = new EitherCodec<>(Codec.INTEGER.optional(), Codec.BOOLEAN);
		Either<Optional<Integer>, Boolean> left = Either.left(null);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), left);
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isJsonNull());
	}
	
	@Test
	void encodeStartWithNullRightValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Optional<Boolean>>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN.optional());
		Either<Integer, Optional<Boolean>> right = Either.right(null);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), right);
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isJsonNull());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive(1)));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		
		Result<Either<Integer, Boolean>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as either"));
	}
	
	@Test
	void decodeStartWithFirstCodecMatch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		
		Result<Either<Integer, Boolean>> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals(Either.left(42), result.orThrow());
	}
	
	@Test
	void decodeStartWithSecondCodecMatch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		
		Result<Either<Integer, Boolean>> result = codec.decodeStart(typeProvider, new JsonPrimitive(true));
		assertTrue(result.isSuccess());
		assertEquals(Either.right(true), result.orThrow());
	}
	
	@Test
	void decodeStartWithNoMatch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		
		Result<Either<Integer, Boolean>> result = codec.decodeStart(typeProvider, new JsonPrimitive("not-int-or-bool"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode value as either"));
	}
	
	@Test
	void decodeStartPrefersFirstCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<String, String>> codec = new EitherCodec<>(Codec.STRING, Codec.STRING);
		
		Result<Either<String, String>> result = codec.decodeStart(typeProvider, new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isLeft());
		assertEquals("hello", result.orThrow().leftOrThrow());
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<String, Double>> codec = new EitherCodec<>(Codec.STRING, Codec.DOUBLE);
		
		Result<Either<String, Double>> stringResult = codec.decodeStart(typeProvider, new JsonPrimitive("text"));
		assertTrue(stringResult.isSuccess());
		assertTrue(stringResult.orThrow().isLeft());
		assertEquals("text", stringResult.orThrow().leftOrThrow());
		
		Result<Either<String, Double>> doubleResult = codec.decodeStart(typeProvider, new JsonPrimitive(3.14));
		assertTrue(doubleResult.isSuccess());
		assertTrue(doubleResult.orThrow().isLeft());
		assertEquals("3.14", doubleResult.orThrow().leftOrThrow());
	}
	
	@Test
	void decodeStartWithOverlappingTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Double, Integer>> codec = new EitherCodec<>(Codec.DOUBLE, Codec.INTEGER);
		
		Result<Either<Double, Integer>> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isLeft());
		assertEquals(42.0, result.orThrow().leftOrThrow());
	}
	
	@Test
	void decodeStartWithOptionalCodecs() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Optional<Integer>, String>> codec = new EitherCodec<>(Codec.INTEGER.optional(), Codec.STRING);
		
		Result<Either<Optional<Integer>, String>> intResult = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(intResult.isSuccess());
		assertTrue(intResult.orThrow().isLeft());
		assertEquals(42, intResult.orThrow().leftOrThrow().orElseThrow());
		
		Result<Either<Optional<Integer>, String>> stringResult = codec.decodeStart(typeProvider, new JsonPrimitive("text"));
		assertTrue(stringResult.isSuccess());
		assertTrue(stringResult.orThrow().isLeft());
		assertTrue(stringResult.orThrow().leftOrThrow().isEmpty());
	}
	
	@Test
	void equalsAndHashCode() {
		EitherCodec<Integer, Boolean> codec1 = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		EitherCodec<Integer, Boolean> codec2 = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		EitherCodec<Integer, Boolean> codec = new EitherCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		String result = codec.toString();
		
		assertTrue(result.startsWith("EitherCodec["));
		assertTrue(result.endsWith("]"));
		assertTrue(result.contains(","));
	}
}

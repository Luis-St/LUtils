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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.Either;
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
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		Either<Integer, Boolean> left = Either.left(1);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), left));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, left));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null value as either"));
	}
	
	@Test
	void encodeWithLeftValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		Either<Integer, Boolean> left = Either.left(42);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), left);
		assertEquals(new JsonPrimitive(42), result);
	}
	
	@Test
	void encodeWithRightValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		Either<Integer, Boolean> right = Either.right(true);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), right);
		assertEquals(new JsonPrimitive(true), result);
	}
	
	@Test
	void encodeWithDifferentTypes() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<String, Double>> codec = new EitherCodec<>(STRING, DOUBLE);
		
		Either<String, Double> leftString = Either.left("hello");
		JsonElement leftResult = codec.encode(typeProvider, typeProvider.empty(), leftString);
		assertEquals(new JsonPrimitive("hello"), leftResult);
		
		Either<String, Double> rightDouble = Either.right(3.14);
		JsonElement rightResult = codec.encode(typeProvider, typeProvider.empty(), rightDouble);
		assertEquals(new JsonPrimitive(3.14), rightResult);
	}
	
	@Test
	void encodeWithNullLeftValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Optional<Integer>, Boolean>> codec = new EitherCodec<>(INTEGER.optional(), BOOLEAN);
		Either<Optional<Integer>, Boolean> left = Either.left(null);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), left);
		assertFalse(result.isJsonNull());
		assertFalse(result.isJsonPrimitive());
		assertFalse(result.isJsonArray());
		assertFalse(result.isJsonObject());
	}
	
	@Test
	void encodeWithNullRightValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Optional<Boolean>>> codec = new EitherCodec<>(INTEGER, BOOLEAN.optional());
		Either<Integer, Optional<Boolean>> right = Either.right(null);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), right);
		assertFalse(result.isJsonNull());
		assertFalse(result.isJsonPrimitive());
		assertFalse(result.isJsonArray());
		assertFalse(result.isJsonObject());
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive(1)));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as either"));
	}
	
	@Test
	void decodeWithFirstCodecMatch() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		Either<Integer, Boolean> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertEquals(Either.left(42), result);
	}
	
	@Test
	void decodeWithSecondCodecMatch() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		Either<Integer, Boolean> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(true));
		assertEquals(Either.right(true), result);
	}
	
	@Test
	void decodeWithNoMatch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Integer, Boolean>> codec = new EitherCodec<>(INTEGER, BOOLEAN);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-int-or-bool")));
		assertTrue(exception.getMessage().contains("Unable to decode value as either"));
	}
	
	@Test
	void decodePrefersFirstCodec() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<String, String>> codec = new EitherCodec<>(STRING, STRING);
		
		Either<String, String> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isLeft());
		assertEquals("hello", result.leftOrThrow());
	}
	
	@Test
	void decodeWithDifferentTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<String, Double>> codec = new EitherCodec<>(STRING, DOUBLE);
		
		Either<String, Double> stringResult = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("text"));
		assertTrue(stringResult.isLeft());
		assertEquals("text", stringResult.leftOrThrow());
		
		Either<String, Double> doubleResult = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertTrue(doubleResult.isRight());
		assertEquals(3.14, doubleResult.rightOrThrow());
	}
	
	@Test
	void decodeWithOverlappingTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Double, Integer>> codec = new EitherCodec<>(DOUBLE, INTEGER);
		
		Either<Double, Integer> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isLeft());
		assertEquals(42.0, result.leftOrThrow());
	}
	
	@Test
	void decodeWithOptionalCodecs() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Either<Optional<Integer>, String>> codec = new EitherCodec<>(INTEGER.optional(), STRING);
		
		Either<Optional<Integer>, String> intResult = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(intResult.isLeft());
		assertEquals(42, intResult.leftOrThrow().orElseThrow());
		
		Either<Optional<Integer>, String> stringResult = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("text"));
		assertTrue(stringResult.isLeft());
		assertTrue(stringResult.leftOrThrow().isEmpty());
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

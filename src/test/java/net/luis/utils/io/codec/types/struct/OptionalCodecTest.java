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
import net.luis.utils.io.data.json.*;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OptionalCodec}.<br>
 *
 * @author Luis-St
 */
class OptionalCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new OptionalCodec<>(null));
		assertDoesNotThrow(() -> new OptionalCodec<>(INTEGER));
	}
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), Optional.of(1)));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, Optional.of(1)));
	}
	
	@Test
	void encodeWithNull() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		JsonElement result = codec.encode(typeProvider, current, null);
		assertSame(current, result);
	}
	
	@Test
	void encodeWithEmptyOptional() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		JsonElement result = codec.encode(typeProvider, current, Optional.empty());
		assertSame(current, result);
	}
	
	@Test
	void encodeWithPresentOptional() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		JsonElement result = codec.encode(typeProvider, current, Optional.of(42));
		assertEquals(new JsonPrimitive(42), result);
	}
	
	@Test
	void encodeWithDifferentTypes() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		JsonObject current = new JsonObject();
		
		Codec<Optional<String>> stringCodec = new OptionalCodec<>(STRING);
		JsonElement stringResult = stringCodec.encode(typeProvider, current, Optional.of("hello"));
		assertEquals(new JsonPrimitive("hello"), stringResult);
		
		Codec<Optional<Boolean>> boolCodec = new OptionalCodec<>(BOOLEAN);
		JsonElement boolResult = boolCodec.encode(typeProvider, current, Optional.of(true));
		assertEquals(new JsonPrimitive(true), boolResult);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive(1)));
	}
	
	@Test
	void decodeWithNull() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		Optional<Integer> result = codec.decode(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void decodeWithEmpty() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		Optional<Integer> result = codec.decode(typeProvider, typeProvider.empty(), JsonNull.INSTANCE);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void decodeWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		Optional<Integer> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isPresent());
		assertEquals(42, result.orElseThrow());
	}
	
	@Test
	void decodeWithInvalidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		Optional<Integer> result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number"));
		assertTrue(result.isEmpty());
	}
	
	@Test
	void decodeWithDifferentTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Optional<String>> stringCodec = new OptionalCodec<>(STRING);
		Optional<String> stringResult = stringCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(stringResult.isPresent());
		assertEquals("hello", stringResult.orElseThrow());
		
		Codec<Optional<Boolean>> boolCodec = new OptionalCodec<>(BOOLEAN);
		Optional<Boolean> boolResult = boolCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(true));
		assertTrue(boolResult.isPresent());
		assertTrue(boolResult.orElseThrow());
	}
	
	@Test
	void orElseWithNull() throws DecoderException {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER).orElse(null);
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Optional<Integer> result = codec.decode(typeProvider, typeProvider.empty(), null);
		assertNull(result);
	}
	
	@Test
	void withDefaultWithValue() throws DecoderException {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER).orElse(Optional.of(99));
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Optional<Integer> result = codec.decode(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isPresent());
		assertEquals(99, result.orElseThrow());
	}
	
	@Test
	void orElseWithEmptyOptional() throws DecoderException {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER).orElse(Optional.empty());
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Optional<Integer> result = codec.decode(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void orElseGetWithNull() {
		OptionalCodec<Integer> codec = new OptionalCodec<>(INTEGER);
		assertThrows(NullPointerException.class, () -> codec.orElseGet(null));
	}
	
	@Test
	void orElseGetWithSupplier() throws DecoderException {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER).orElseGet(() -> Optional.of(123));
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Optional<Integer> result = codec.decode(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isPresent());
		assertEquals(123, result.orElseThrow());
	}
	
	@Test
	void equalsAndHashCode() {
		OptionalCodec<Integer> codec1 = new OptionalCodec<>(INTEGER);
		OptionalCodec<Integer> codec2 = new OptionalCodec<>(INTEGER);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		OptionalCodec<Integer> codec = new OptionalCodec<>(INTEGER);
		String result = codec.toString();
		
		assertTrue(result.startsWith("OptionalCodec["));
		assertTrue(result.endsWith("]"));
	}
}

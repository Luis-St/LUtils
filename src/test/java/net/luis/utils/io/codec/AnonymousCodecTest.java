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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.decoder.Decoder;
import net.luis.utils.io.codec.encoder.Encoder;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnonymousCodec}.<br>
 *
 * @author Luis-St
 */
class AnonymousCodecTest {
	
	@Test
	void constructorNullChecks() {
		Encoder<String> encoder = STRING;
		Decoder<String> decoder = STRING;
		
		assertDoesNotThrow(() -> Codec.of((Class<String>) null, encoder, decoder, "TestCodec"));
		assertThrows(NullPointerException.class, () -> Codec.of(() -> String.class, null, decoder, "TestCodec"));
		assertThrows(NullPointerException.class, () -> Codec.of(() -> String.class, encoder, null, "TestCodec"));
		assertThrows(NullPointerException.class, () -> Codec.of(() -> String.class, encoder, decoder, null));
	}
	
	@Test
	void constructorWithBlankName() {
		Encoder<String> encoder = STRING;
		Decoder<String> decoder = STRING;
		
		assertThrows(IllegalArgumentException.class, () -> Codec.of(() -> String.class, encoder, decoder, ""));
		assertThrows(IllegalArgumentException.class, () -> Codec.of(() -> String.class, encoder, decoder, "   "));
	}
	
	@Test
	void constructorWithValidParameters() {
		Encoder<String> encoder = STRING;
		Decoder<String> decoder = STRING;
		
		assertDoesNotThrow(() -> Codec.of(() -> String.class, encoder, decoder, "TestCodec"));
		assertDoesNotThrow(() -> Codec.of(String.class, encoder, decoder, "TestCodec"));
	}
	
	@Test
	void getTypeWithValidTypeSupplier() {
		Codec<String> codec = Codec.of(() -> String.class, STRING, STRING, "TestCodec");
		
		assertEquals(String.class, codec.getType());
	}
	
	@Test
	void getTypeWithNullTypeSupplierFallsBackToInference() {
		Codec<String> codec = Codec.of(() -> null, STRING, STRING, "TestCodec");
		
		assertThrows(IllegalStateException.class, codec::getType);
	}
	
	@Test
	void getTypeWithClassParameter() {
		Codec<String> codec = Codec.of(String.class, STRING, STRING, "TestCodec");
		
		assertEquals(String.class, codec.getType());
	}
	
	@Test
	void encodeStartDelegatesToEncoder() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<String> encoder = new Encoder<>() {
			@Override
			public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable String value) {
				return provider.createString("encoded:" + value);
			}
		};
		Decoder<String> decoder = STRING;
		
		Codec<String> codec = Codec.of(String.class, encoder, decoder, "TestCodec");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "test");
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("encoded:test"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyDelegatesToEncoder() {
		Encoder<String> encoder = new Encoder<>() {
			@Override
			public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable String value) {
				return Result.success(null);
			}
			
			@Override
			public @NonNull Result<String> encodeKey(@NonNull String key) {
				return Result.success("key:" + key);
			}
		};
		Decoder<String> decoder = STRING;
		
		Codec<String> codec = Codec.of(String.class, encoder, decoder, "TestCodec");
		
		Result<String> result = codec.encodeKey("test");
		assertTrue(result.isSuccess());
		assertEquals("key:test", result.resultOrThrow());
	}
	
	@Test
	void decodeStartDelegatesToDecoder() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<String> encoder = STRING;
		Decoder<String> decoder = new Decoder<>() {
			@Override
			public <R> @NonNull Result<String> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
				if (value == null) {
					return Result.error("null value");
				}
				Result<String> stringResult = provider.getString(value);
				return stringResult.flatMap(s -> Result.success("decoded:" + s));
			}
		};
		
		Codec<String> codec = Codec.of(String.class, encoder, decoder, "TestCodec");
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("test"));
		assertTrue(result.isSuccess());
		assertEquals("decoded:test", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyDelegatesToDecoder() {
		Encoder<String> encoder = STRING;
		Decoder<String> decoder = new Decoder<>() {
			@Override
			public <R> @NonNull Result<String> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
				return Result.success(null);
			}
			
			@Override
			public @NonNull Result<String> decodeKey(@NonNull String key) {
				return Result.success("decoded:" + key);
			}
		};
		
		Codec<String> codec = Codec.of(String.class, encoder, decoder, "TestCodec");
		
		Result<String> result = codec.decodeKey("test");
		assertTrue(result.isSuccess());
		assertEquals("decoded:test", result.resultOrThrow());
	}
	
	@Test
	void equalsAndHashCode() {
		Encoder<String> encoder = STRING;
		Decoder<String> decoder = STRING;
		
		Codec<String> codec1 = Codec.of(String.class, encoder, decoder, "TestCodec");
		Codec<String> codec2 = Codec.of(String.class, encoder, decoder, "TestCodec");
		Codec<String> codec3 = Codec.of(String.class, encoder, decoder, "DifferentCodec");
		
		assertNotEquals(codec1, codec2);
		
		assertNotEquals(codec1, codec3);
	}
	
	@Test
	void toStringRepresentation() {
		Codec<String> codec = Codec.of(String.class, STRING, STRING, "CustomTestCodec");
		
		assertEquals("CustomTestCodec", codec.toString());
	}
	
	@Test
	void roundTripEncodingAndDecoding() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codec.of(String.class, STRING, STRING, "TestCodec");
		
		String original = "test-value";
		JsonElement encoded = codec.encode(typeProvider, original);
		String decoded = codec.decode(typeProvider, encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	void codecWrappingWithCustomBehavior() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Encoder<Integer> upperBoundEncoder = new Encoder<>() {
			@Override
			public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Integer value) {
				if (value == null) {
					return Result.error("Cannot encode null");
				}
				if (value > 100) {
					return Result.error("Value exceeds upper bound of 100");
				}
				return provider.createInteger(value);
			}
		};
		
		Decoder<Integer> lowerBoundDecoder = new Decoder<>() {
			@Override
			public <R> @NonNull Result<Integer> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
				if (value == null) {
					return Result.error("Cannot decode null");
				}
				Result<Integer> intResult = provider.getInteger(value);
				return intResult.flatMap(i -> {
					if (i < 0) {
						return Result.error("Value below lower bound of 0");
					}
					return Result.success(i);
				});
			}
		};
		
		Codec<Integer> boundedCodec = Codec.of(Integer.class, upperBoundEncoder, lowerBoundDecoder, "BoundedIntegerCodec");
		
		Result<JsonElement> validEncode = boundedCodec.encodeStart(typeProvider, typeProvider.empty(), 50);
		assertTrue(validEncode.isSuccess());
		
		Result<JsonElement> invalidEncode = boundedCodec.encodeStart(typeProvider, typeProvider.empty(), 150);
		assertTrue(invalidEncode.isError());
		assertTrue(invalidEncode.errorOrThrow().contains("exceeds upper bound"));
		
		Result<Integer> validDecode = boundedCodec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(50));
		assertTrue(validDecode.isSuccess());
		assertEquals(50, validDecode.resultOrThrow());
		
		Result<Integer> invalidDecode = boundedCodec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-10));
		assertTrue(invalidDecode.isError());
		assertTrue(invalidDecode.errorOrThrow().contains("below lower bound"));
	}
}

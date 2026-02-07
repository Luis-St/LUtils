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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.decoder.Decoder;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.Encoder;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
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
	void encodeDelegatesToEncoder() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<String> encoder = new Encoder<>() {
			@Override
			public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable String value) throws EncoderException {
				return provider.createString("encoded:" + value);
			}
		};
		Decoder<String> decoder = STRING;
		
		Codec<String> codec = Codec.of(String.class, encoder, decoder, "TestCodec");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), "test");
		assertEquals(new JsonPrimitive("encoded:test"), result);
	}
	
	@Test
	void encodeKeyDelegatesToEncoder() throws Exception {
		Encoder<String> encoder = new Encoder<>() {
			@Override
			public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable String value) throws EncoderException {
				return provider.createString("");
			}
			
			@Override
			public @NonNull String encodeKey(@NonNull String key) throws EncoderException {
				return "key:" + key;
			}
		};
		Decoder<String> decoder = STRING;
		
		Codec<String> codec = Codec.of(String.class, encoder, decoder, "TestCodec");
		
		String result = codec.encodeKey("test");
		assertEquals("key:test", result);
	}
	
	@Test
	void decodeDelegatesToDecoder() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<String> encoder = STRING;
		Decoder<String> decoder = new Decoder<>() {
			@Override
			public <R> @NonNull String decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
				if (value == null) {
					throw new DecoderException("null value");
				}
				String s = provider.getString(value);
				return "decoded:" + s;
			}
		};
		
		Codec<String> codec = Codec.of(String.class, encoder, decoder, "TestCodec");
		
		String result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("test"));
		assertEquals("decoded:test", result);
	}
	
	@Test
	void decodeKeyDelegatesToDecoder() throws Exception {
		Encoder<String> encoder = STRING;
		Decoder<String> decoder = new Decoder<>() {
			@Override
			public <R> @NonNull String decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
				return "";
			}
			
			@Override
			public @NonNull String decodeKey(@NonNull String key) throws DecoderException {
				return "decoded:" + key;
			}
		};
		
		Codec<String> codec = Codec.of(String.class, encoder, decoder, "TestCodec");
		
		String result = codec.decodeKey("test");
		assertEquals("decoded:test", result);
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
	void roundTripEncodingAndDecoding() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = Codec.of(String.class, STRING, STRING, "TestCodec");
		
		String original = "test-value";
		JsonElement encoded = codec.encode(typeProvider, original);
		String decoded = codec.decode(typeProvider, encoded);
		
		assertEquals(original, decoded);
	}
	
	@Test
	void codecWrappingWithCustomBehavior() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Encoder<Integer> upperBoundEncoder = new Encoder<>() {
			@Override
			public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Integer value) throws EncoderException {
				if (value == null) {
					throw new EncoderException("Cannot encode null");
				}
				if (value > 100) {
					throw new EncoderException("Value exceeds upper bound of 100");
				}
				return provider.createInteger(value);
			}
		};
		
		Decoder<Integer> lowerBoundDecoder = new Decoder<>() {
			@Override
			public <R> @NonNull Integer decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
				if (value == null) {
					throw new DecoderException("Cannot decode null");
				}
				int i = provider.getInteger(value);
				if (i < 0) {
					throw new DecoderException("Value below lower bound of 0");
				}
				return i;
			}
		};
		
		Codec<Integer> boundedCodec = Codec.of(Integer.class, upperBoundEncoder, lowerBoundDecoder, "BoundedIntegerCodec");
		
		assertDoesNotThrow(() -> boundedCodec.encode(typeProvider, typeProvider.empty(), 50));
		
		EncoderException encodeException = assertThrows(EncoderException.class, () -> boundedCodec.encode(typeProvider, typeProvider.empty(), 150));
		assertTrue(encodeException.getMessage().contains("exceeds upper bound"));
		
		assertEquals(50, boundedCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(50)));
		
		DecoderException decodeException = assertThrows(DecoderException.class, () -> boundedCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-10)));
		assertTrue(decodeException.getMessage().contains("below lower bound"));
	}
}

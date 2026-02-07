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

import java.util.Map;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DiscriminatedCodec}.<br>
 *
 * @author Luis-St
 */
class DiscriminatedCodecTest {
	
	@Test
	void constructor() {
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		
		assertThrows(NullPointerException.class, () -> new DiscriminatedCodec<>(null, STRING, provider));
		assertThrows(NullPointerException.class, () -> new DiscriminatedCodec<>("field", null, provider));
		assertThrows(NullPointerException.class, () -> new DiscriminatedCodec<>("field", STRING, null));
		assertDoesNotThrow(() -> new DiscriminatedCodec<>("field", STRING, provider));
	}
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), "test"));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, "test"));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null value as discriminated"));
	}
	
	@Test
	void encodeWithValidDiscriminator() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		JsonElement result = codec.encode(typeProvider, parent, "hello");
		assertEquals(new JsonPrimitive("hello"), result);
	}
	
	@Test
	void encodeWithMissingDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, parent, "hello"));
		assertTrue(exception.getMessage().contains("Discriminator field 'type' not found"));
	}
	
	@Test
	void encodeWithDifferentTypes() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, Map.of(
			"int", INTEGER,
			"double", DOUBLE
		));
		Codec<Number> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parentInt = new JsonObject();
		parentInt.add("type", new JsonPrimitive("int"));
		JsonElement resultInt = codec.encode(typeProvider, parentInt, 42);
		assertEquals(new JsonPrimitive(42), resultInt);
		
		JsonObject parentDouble = new JsonObject();
		parentDouble.add("type", new JsonPrimitive("double"));
		JsonElement resultDouble = codec.encode(typeProvider, parentDouble, 3.14);
		assertEquals(new JsonPrimitive(3.14), resultDouble);
	}
	
	@Test
	void encodeWithInvalidDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("valid", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("invalid"));
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, parent, "test"));
		assertTrue(exception.getMessage().contains("No codec found for discriminator value 'invalid'"));
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("test")));
		assertThrows(NullPointerException.class, () -> codec.decode(typeProvider, null, new JsonPrimitive("test")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as discriminated"));
	}
	
	@Test
	void decodeWithValidDiscriminator() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		String result = codec.decode(typeProvider, parent, new JsonPrimitive("hello"));
		assertEquals("hello", result);
	}
	
	@Test
	void decodeWithMissingDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, parent, new JsonPrimitive("hello")));
		assertTrue(exception.getMessage().contains("Discriminator field 'type' not found"));
	}
	
	@Test
	void decodeWithDifferentTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, Map.of(
			"int", INTEGER,
			"double", DOUBLE
		));
		Codec<Number> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parentInt = new JsonObject();
		parentInt.add("type", new JsonPrimitive("int"));
		Number resultInt = codec.decode(typeProvider, parentInt, new JsonPrimitive(42));
		assertEquals(42, resultInt);
		
		JsonObject parentDouble = new JsonObject();
		parentDouble.add("type", new JsonPrimitive("double"));
		Number resultDouble = codec.decode(typeProvider, parentDouble, new JsonPrimitive(3.14));
		assertEquals(3.14, resultDouble);
	}
	
	@Test
	void decodeWithInvalidDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("valid", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("invalid"));
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, parent, new JsonPrimitive("test")));
		assertTrue(exception.getMessage().contains("No codec found for discriminator value 'invalid'"));
	}
	
	@Test
	void roundTripEncodingDecoding() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of(
			"string", STRING
		));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		JsonElement encoded = codec.encode(typeProvider, parent, "test");
		String decoded = codec.decode(typeProvider, parent, encoded);
		assertEquals("test", decoded);
	}
	
	@Test
	void integrationWithOptionalCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> discriminatedCodec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		assertDoesNotThrow(() -> discriminatedCodec.optional().encode(typeProvider, parent, java.util.Optional.of("test")));
	}
	
	@Test
	void integrationWithNullableCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> discriminatedCodec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		assertDoesNotThrow(() -> discriminatedCodec.nullable().encode(typeProvider, parent, "test"));
	}
	
	@Test
	void integrationWithMapBasedProvider() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Map<String, Codec<? extends Number>> codecMap = Map.of(
			"int", INTEGER,
			"long", LONG
		);
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, codecMap);
		Codec<Number> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("int"));
		
		JsonElement result = codec.encode(typeProvider, parent, 100);
		assertEquals(new JsonPrimitive(100), result);
	}
	
	@Test
	void integrationWithFunctionBasedProvider() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, type -> switch (type) {
			case "int" -> INTEGER;
			case "long" -> LONG;
			default -> throw new IllegalArgumentException("Unknown type: " + type);
		});
		Codec<Number> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("long"));
		
		JsonElement result = codec.encode(typeProvider, parent, 100L);
		assertEquals(new JsonPrimitive(100L), result);
	}
	
	@Test
	void integrationWithCodecBuilder() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonElement encoded = codec.encode(typeProvider, parent, "hello");
		assertEquals(new JsonPrimitive("hello"), encoded);
		
		String decoded = codec.decode(typeProvider, parent, new JsonPrimitive("hello"));
		assertEquals("hello", decoded);
	}
	
	@Test
	void equalsAndHashCode() {
		DiscriminatedCodecProvider<String, String> provider1 = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		
		DiscriminatedCodec<String, String> codec1 = new DiscriminatedCodec<>("field", STRING, provider1);
		DiscriminatedCodec<String, String> codec2 = new DiscriminatedCodec<>("field", STRING, provider1);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		DiscriminatedCodec<String, String> codec = new DiscriminatedCodec<>("field", STRING, provider);
		
		String result = codec.toString();
		assertTrue(result.startsWith("DiscriminatedCodec["));
		assertTrue(result.endsWith("]"));
		assertTrue(result.contains("field"));
	}
}

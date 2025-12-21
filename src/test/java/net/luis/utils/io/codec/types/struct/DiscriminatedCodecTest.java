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
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), "test"));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, "test"));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null value as discriminated"));
	}
	
	@Test
	void encodeStartWithValidDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, parent, "hello");
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("hello"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMissingDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, parent, "hello");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Discriminator field 'type' not found"));
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, Map.of(
			"int", INTEGER,
			"double", DOUBLE
		));
		Codec<Number> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parentInt = new JsonObject();
		parentInt.add("type", new JsonPrimitive("int"));
		Result<JsonElement> resultInt = codec.encodeStart(typeProvider, parentInt, 42);
		assertTrue(resultInt.isSuccess());
		assertEquals(new JsonPrimitive(42), resultInt.resultOrThrow());
		
		JsonObject parentDouble = new JsonObject();
		parentDouble.add("type", new JsonPrimitive("double"));
		Result<JsonElement> resultDouble = codec.encodeStart(typeProvider, parentDouble, 3.14);
		assertTrue(resultDouble.isSuccess());
		assertEquals(new JsonPrimitive(3.14), resultDouble.resultOrThrow());
	}
	
	@Test
	void encodeStartWithInvalidDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("valid", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("invalid"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, parent, "test");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("No codec found for discriminator value 'invalid'"));
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("test")));
		assertThrows(NullPointerException.class, () -> codec.decodeStart(typeProvider, null, new JsonPrimitive("test")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as discriminated"));
	}
	
	@Test
	void decodeStartWithValidDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		Result<String> result = codec.decodeStart(typeProvider, parent, new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMissingDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		
		Result<String> result = codec.decodeStart(typeProvider, parent, new JsonPrimitive("hello"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Discriminator field 'type' not found"));
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, Map.of(
			"int", INTEGER,
			"double", DOUBLE
		));
		Codec<Number> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parentInt = new JsonObject();
		parentInt.add("type", new JsonPrimitive("int"));
		Result<Number> resultInt = codec.decodeStart(typeProvider, parentInt, new JsonPrimitive(42));
		assertTrue(resultInt.isSuccess());
		assertEquals(42, resultInt.resultOrThrow());
		
		JsonObject parentDouble = new JsonObject();
		parentDouble.add("type", new JsonPrimitive("double"));
		Result<Number> resultDouble = codec.decodeStart(typeProvider, parentDouble, new JsonPrimitive(3.14));
		assertTrue(resultDouble.isSuccess());
		assertEquals(3.14, resultDouble.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidDiscriminator() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("valid", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("invalid"));
		
		Result<String> result = codec.decodeStart(typeProvider, parent, new JsonPrimitive("test"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("No codec found for discriminator value 'invalid'"));
	}
	
	@Test
	void roundTripEncodingDecoding() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of(
			"string", STRING
		));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, parent, "test");
		assertTrue(encoded.isSuccess());
		
		Result<String> decoded = codec.decodeStart(typeProvider, parent, encoded.resultOrThrow());
		assertTrue(decoded.isSuccess());
		assertEquals("test", decoded.resultOrThrow());
	}
	
	@Test
	void integrationWithOptionalCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> discriminatedCodec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		Result<JsonElement> result = discriminatedCodec.optional().encodeStart(typeProvider, parent, java.util.Optional.of("test"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void integrationWithNullableCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> discriminatedCodec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		Result<JsonElement> result = discriminatedCodec.nullable().encodeStart(typeProvider, parent, "test");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void integrationWithMapBasedProvider() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Map<String, Codec<? extends Number>> codecMap = Map.of(
			"int", INTEGER,
			"long", LONG
		);
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, codecMap);
		Codec<Number> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("int"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, parent, 100);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(100), result.resultOrThrow());
	}
	
	@Test
	void integrationWithFunctionBasedProvider() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		DiscriminatedCodecProvider<Number, String> provider = DiscriminatedCodecProvider.create(Number.class, type -> switch (type) {
			case "int" -> INTEGER;
			case "long" -> LONG;
			default -> throw new IllegalArgumentException("Unknown type: " + type);
		});
		Codec<Number> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("long"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, parent, 100L);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(100L), result.resultOrThrow());
	}
	
	@Test
	void integrationWithCodecBuilder() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonObject parent = new JsonObject();
		parent.add("type", new JsonPrimitive("string"));
		
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, Map.of("string", STRING));
		Codec<String> codec = new DiscriminatedCodec<>("type", STRING, provider);
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, parent, "hello");
		assertTrue(encoded.isSuccess());
		assertEquals(new JsonPrimitive("hello"), encoded.resultOrThrow());
		
		Result<String> decoded = codec.decodeStart(typeProvider, parent, new JsonPrimitive("hello"));
		assertTrue(decoded.isSuccess());
		assertEquals("hello", decoded.resultOrThrow());
	}
	
	@Test
	void equalsAndHashCode() {
		DiscriminatedCodecProvider<String, String> provider1 = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		DiscriminatedCodecProvider<String, String> provider2 = DiscriminatedCodecProvider.create(String.class, Map.of("a", STRING));
		
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

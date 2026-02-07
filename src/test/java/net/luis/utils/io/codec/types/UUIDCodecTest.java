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

package net.luis.utils.io.codec.types;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UUIDCodec}.<br>
 *
 * @author Luis-St
 */
class UUIDCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		UUID uuid = UUID.randomUUID();
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), uuid));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, uuid));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as UUID"));
	}
	
	@Test
	void encodeWithValidUUID() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), uuid);
		assertEquals(new JsonPrimitive("550e8400-e29b-41d4-a716-446655440000"), result);
	}
	
	@Test
	void encodeWithRandomUUID() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		UUID uuid = UUID.randomUUID();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), uuid);
		assertEquals(new JsonPrimitive(uuid.toString()), result);
	}
	
	@Test
	void encodeWithNilUUID() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		UUID uuid = new UUID(0L, 0L);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), uuid);
		assertEquals(new JsonPrimitive("00000000-0000-0000-0000-000000000000"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		Codec<UUID> codec = new UUIDCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValidUUID() throws EncoderException {
		Codec<UUID> codec = new UUIDCodec();
		UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
		
		String result = codec.encodeKey(uuid);
		assertEquals("550e8400-e29b-41d4-a716-446655440000", result);
	}
	
	@Test
	void encodeKeyWithRandomUUID() throws EncoderException {
		Codec<UUID> codec = new UUIDCodec();
		UUID uuid = UUID.randomUUID();
		
		String result = codec.encodeKey(uuid);
		assertEquals(uuid.toString(), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("550e8400-e29b-41d4-a716-446655440000")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as UUID"));
	}
	
	@Test
	void decodeWithValidString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		
		UUID result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("550e8400-e29b-41d4-a716-446655440000"));
		assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), result);
	}
	
	@Test
	void decodeWithNilUUID() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		
		UUID result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00000000-0000-0000-0000-000000000000"));
		assertEquals(new UUID(0L, 0L), result);
	}
	
	@Test
	void decodeWithUppercaseUUID() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		
		UUID result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("550E8400-E29B-41D4-A716-446655440000"));
		assertEquals(UUID.fromString("550E8400-E29B-41D4-A716-446655440000"), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-uuid")));
		assertTrue(exception.getMessage().contains("Unable to decode UUID"));
	}
	
	@Test
	void decodeWithInvalidLength() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("550e8400-e29b-41d4-a716")));
		assertTrue(exception.getMessage().contains("Unable to decode UUID"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = new UUIDCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		Codec<UUID> codec = new UUIDCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidString() throws DecoderException {
		Codec<UUID> codec = new UUIDCodec();
		
		UUID result = codec.decodeKey("550e8400-e29b-41d4-a716-446655440000");
		assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), result);
	}
	
	@Test
	void decodeKeyWithNilUUID() throws DecoderException {
		Codec<UUID> codec = new UUIDCodec();
		
		UUID result = codec.decodeKey("00000000-0000-0000-0000-000000000000");
		assertEquals(new UUID(0L, 0L), result);
	}
	
	@Test
	void decodeKeyWithInvalidFormat() {
		Codec<UUID> codec = new UUIDCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid-uuid"));
		assertTrue(exception.getMessage().contains("Unable to decode UUID from key"));
	}
	
	@Test
	void decodeKeyWithInvalidLength() {
		Codec<UUID> codec = new UUIDCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("550e8400-e29b-41d4-a716"));
		assertTrue(exception.getMessage().contains("Unable to decode UUID from key"));
	}
	
	@Test
	void toStringRepresentation() {
		UUIDCodec codec = new UUIDCodec();
		assertEquals("UUIDCodec", codec.toString());
	}
}

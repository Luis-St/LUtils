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

package net.luis.utils.io.codec.internal;

import net.luis.utils.io.codec.KeyableCodec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();
		UUID uuid = UUID.randomUUID();

		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), uuid));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, uuid));
	}

	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as UUID"));
	}

	@Test
	void encodeStartWithValidUUID() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();
		UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), uuid);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("550e8400-e29b-41d4-a716-446655440000"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithRandomUUID() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();
		UUID uuid = UUID.randomUUID();

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), uuid);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(uuid.toString()), result.resultOrThrow());
	}

	@Test
	void encodeStartWithNilUUID() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();
		UUID uuid = new UUID(0L, 0L);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), uuid);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("00000000-0000-0000-0000-000000000000"), result.resultOrThrow());
	}

	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();
		UUID uuid = UUID.randomUUID();

		assertThrows(NullPointerException.class, () -> codec.encodeKey(null, uuid));
		assertThrows(NullPointerException.class, () -> codec.encodeKey(typeProvider, null));
	}

	@Test
	void encodeKeyWithValidUUID() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();
		UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

		Result<String> result = codec.encodeKey(typeProvider, uuid);
		assertTrue(result.isSuccess());
		assertEquals("550e8400-e29b-41d4-a716-446655440000", result.resultOrThrow());
	}

	@Test
	void encodeKeyWithRandomUUID() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();
		UUID uuid = UUID.randomUUID();

		Result<String> result = codec.encodeKey(typeProvider, uuid);
		assertTrue(result.isSuccess());
		assertEquals(uuid.toString(), result.resultOrThrow());
	}

	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive("550e8400-e29b-41d4-a716-446655440000")));
	}

	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<UUID> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as UUID"));
	}

	@Test
	void decodeStartWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<UUID> result = codec.decodeStart(typeProvider, new JsonPrimitive("550e8400-e29b-41d4-a716-446655440000"));
		assertTrue(result.isSuccess());
		assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithNilUUID() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<UUID> result = codec.decodeStart(typeProvider, new JsonPrimitive("00000000-0000-0000-0000-000000000000"));
		assertTrue(result.isSuccess());
		assertEquals(new UUID(0L, 0L), result.resultOrThrow());
	}

	@Test
	void decodeStartWithUppercaseUUID() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<UUID> result = codec.decodeStart(typeProvider, new JsonPrimitive("550E8400-E29B-41D4-A716-446655440000"));
		assertTrue(result.isSuccess());
		assertEquals(UUID.fromString("550E8400-E29B-41D4-A716-446655440000"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<UUID> result = codec.decodeStart(typeProvider, new JsonPrimitive("invalid-uuid"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode UUID"));
		assertTrue(result.errorOrThrow().contains("Unable to parse UUID"));
	}

	@Test
	void decodeStartWithInvalidLength() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<UUID> result = codec.decodeStart(typeProvider, new JsonPrimitive("550e8400-e29b-41d4-a716"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode UUID"));
		assertTrue(result.errorOrThrow().contains("Unable to parse UUID"));
	}

	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<UUID> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isError());
	}

	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeKey(null, "550e8400-e29b-41d4-a716-446655440000"));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(typeProvider, null));
	}

	@Test
	void decodeKeyWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<UUID> result = codec.decodeKey(typeProvider, "550e8400-e29b-41d4-a716-446655440000");
		assertTrue(result.isSuccess());
		assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), result.resultOrThrow());
	}

	@Test
	void decodeKeyWithNilUUID() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<UUID> result = codec.decodeKey(typeProvider, "00000000-0000-0000-0000-000000000000");
		assertTrue(result.isSuccess());
		assertEquals(new UUID(0L, 0L), result.resultOrThrow());
	}

	@Test
	void decodeKeyWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<UUID> result = codec.decodeKey(typeProvider, "invalid-uuid");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode UUID from key"));
	}

	@Test
	void decodeKeyWithInvalidLength() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<UUID> codec = new UUIDCodec();

		Result<UUID> result = codec.decodeKey(typeProvider, "550e8400-e29b-41d4-a716");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode UUID from key"));
	}

	@Test
	void toStringRepresentation() {
		UUIDCodec codec = new UUIDCodec();
		assertEquals("UUIDCodec", codec.toString());
	}
}

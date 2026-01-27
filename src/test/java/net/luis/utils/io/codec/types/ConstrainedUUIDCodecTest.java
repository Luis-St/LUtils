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
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.constraint.util.UUIDVariant;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UUIDCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedUUIDCodecTest {
	
	private static final UUID UUID_1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
	private static final UUID UUID_2 = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
	private static final UUID UUID_3 = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
	
	private static final UUID NIL_UUID = new UUID(0L, 0L);
	
	private static final UUID MAX_UUID = new UUID(-1L, -1L);
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(UUID_1.toString()), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isSuccess());
		assertEquals(UUID_1, result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isSuccess());
		assertEquals(UUID_1.toString(), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isSuccess());
		assertEquals(UUID_1, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_2);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		Result<String> result = codec.encodeKey(UUID_2);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		Result<UUID> result = codec.decodeKey(UUID_2.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_2);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		Result<String> result = codec.encodeKey(UUID_2);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		Result<UUID> result = codec.decodeKey(UUID_2.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2, UUID_3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_2);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2, UUID_3));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2, UUID_3));
		
		Result<String> result = codec.encodeKey(UUID_2);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2, UUID_3));
		
		Result<UUID> result = codec.decodeKey(UUID_2.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_3);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_3.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2));
		
		Result<String> result = codec.encodeKey(UUID_3);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2));
		
		Result<UUID> result = codec.decodeKey(UUID_3.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_3);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_3.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		Result<String> result = codec.encodeKey(UUID_3);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		Result<UUID> result = codec.decodeKey(UUID_3.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartVersionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartVersionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyVersionConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyVersionConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_2);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyVersionConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		Result<String> result = codec.encodeKey(UUID_2);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyVersionConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		Result<UUID> result = codec.decodeKey(UUID_2.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartVersionRangeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartVersionRangeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyVersionRangeConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		Result<String> result = codec.encodeKey(UUID_3);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyVersionRangeConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartVersionRangeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), NIL_UUID);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartVersionRangeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyVersionRangeConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		Result<String> result = codec.encodeKey(NIL_UUID);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyVersionRangeConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		Result<UUID> result = codec.decodeKey(NIL_UUID.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartVariantConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartVariantConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyVariantConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyVariantConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartVariantConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), NIL_UUID);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartVariantConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyVariantConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		Result<String> result = codec.encodeKey(NIL_UUID);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyVariantConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		Result<UUID> result = codec.decodeKey(NIL_UUID.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNilConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.nil();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), NIL_UUID);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNilConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.nil();
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNilConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.nil();
		
		Result<String> result = codec.encodeKey(NIL_UUID);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNilConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.nil();
		
		Result<UUID> result = codec.decodeKey(NIL_UUID.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNilConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.nil();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNilConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.nil();
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNilConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.nil();
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNilConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.nil();
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotNilConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotNilConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotNilConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotNilConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotNilConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), NIL_UUID);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotNilConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotNilConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<String> result = codec.encodeKey(NIL_UUID);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotNilConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		Result<UUID> result = codec.decodeKey(NIL_UUID.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMaxConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.max();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), MAX_UUID);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMaxConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.max();
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(MAX_UUID.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyMaxConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.max();
		
		Result<String> result = codec.encodeKey(MAX_UUID);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyMaxConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.max();
		
		Result<UUID> result = codec.decodeKey(MAX_UUID.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMaxConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.max();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMaxConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.max();
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyMaxConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.max();
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyMaxConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.max();
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4).variant(builder -> builder.equalTo(UUIDVariant.RFC_4122)).notNil();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4).variant(builder -> builder.equalTo(UUIDVariant.RFC_4122)).notNil();
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCombinedConstraintsSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(4).variant(builder -> builder.equalTo(UUIDVariant.RFC_4122)).notNil();
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCombinedConstraintsSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(4).variant(builder -> builder.equalTo(UUIDVariant.RFC_4122)).notNil();
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCombinedConstraintsVersionViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4).notNil();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_2);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCombinedConstraintsNilViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(0, 5)).notNil();
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCombinedConstraintsVariantViolation() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		Result<String> result = codec.encodeKey(NIL_UUID);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCombinedConstraintsMultipleViolations() {
		Codec<UUID> codec = Codecs.UUID.version(4).variant(builder -> builder.equalTo(UUIDVariant.RFC_4122)).notNil();
		
		Result<UUID> result = codec.decodeKey(NIL_UUID.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartVersionNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartVersionNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyVersionNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyVersionNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartVersionNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_2);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartVersionNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyVersionNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		Result<String> result = codec.encodeKey(UUID_2);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyVersionNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		Result<UUID> result = codec.decodeKey(UUID_2.toString());
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartVariantNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartVariantNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyVariantNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		Result<String> result = codec.encodeKey(UUID_1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyVariantNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		Result<UUID> result = codec.decodeKey(UUID_1.toString());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartVariantNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), NIL_UUID);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartVariantNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		Result<UUID> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString()));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyVariantNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		Result<String> result = codec.encodeKey(NIL_UUID);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyVariantNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		Result<UUID> result = codec.decodeKey(NIL_UUID.toString());
		assertTrue(result.isError());
	}
}

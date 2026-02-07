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
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.constraint.util.UUIDVariant;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
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
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), UUID_1);
		assertEquals(new JsonPrimitive(UUID_1.toString()), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		UUID result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString()));
		assertEquals(UUID_1, result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		String result = codec.encodeKey(UUID_1);
		assertEquals(UUID_1.toString(), result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		UUID result = codec.decodeKey(UUID_1.toString());
		assertEquals(UUID_1, result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString())));
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		assertDoesNotThrow(() -> codec.encodeKey(UUID_1));
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		assertDoesNotThrow(() -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), UUID_2));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString())));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(UUID_2));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.equalTo(UUID_1);
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(UUID_2.toString()));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), UUID_2));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString())));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		assertDoesNotThrow(() -> codec.encodeKey(UUID_2));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		assertDoesNotThrow(() -> codec.decodeKey(UUID_2.toString()));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString())));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(UUID_1));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notEqualTo(UUID_1);
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2, UUID_3));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), UUID_2));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2, UUID_3));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString())));
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2, UUID_3));
		
		assertDoesNotThrow(() -> codec.encodeKey(UUID_2));
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2, UUID_3));
		
		assertDoesNotThrow(() -> codec.decodeKey(UUID_2.toString()));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), UUID_3));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_3.toString())));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(UUID_3));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.in(Set.of(UUID_1, UUID_2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(UUID_3.toString()));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), UUID_3));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_3.toString())));
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		assertDoesNotThrow(() -> codec.encodeKey(UUID_3));
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		assertDoesNotThrow(() -> codec.decodeKey(UUID_3.toString()));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString())));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(UUID_1));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notIn(Set.of(UUID_1, UUID_2));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeVersionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeVersionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString())));
	}
	
	@Test
	void encodeKeyVersionConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		assertDoesNotThrow(() -> codec.encodeKey(UUID_1));
	}
	
	@Test
	void decodeKeyVersionConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		assertDoesNotThrow(() -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), UUID_2));
	}
	
	@Test
	void decodeVersionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString())));
	}
	
	@Test
	void encodeKeyVersionConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(UUID_2));
	}
	
	@Test
	void decodeKeyVersionConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(4);
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(UUID_2.toString()));
	}
	
	@Test
	void encodeVersionRangeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeVersionRangeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString())));
	}
	
	@Test
	void encodeKeyVersionRangeConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		assertDoesNotThrow(() -> codec.encodeKey(UUID_3));
	}
	
	@Test
	void decodeKeyVersionRangeConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		assertDoesNotThrow(() -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeVersionRangeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), NIL_UUID));
	}
	
	@Test
	void decodeVersionRangeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString())));
	}
	
	@Test
	void encodeKeyVersionRangeConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(NIL_UUID));
	}
	
	@Test
	void decodeKeyVersionRangeConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(1, 5));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(NIL_UUID.toString()));
	}
	
	@Test
	void encodeVariantConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeVariantConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString())));
	}
	
	@Test
	void encodeKeyVariantConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		assertDoesNotThrow(() -> codec.encodeKey(UUID_1));
	}
	
	@Test
	void decodeKeyVariantConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		assertDoesNotThrow(() -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeVariantConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), NIL_UUID));
	}
	
	@Test
	void decodeVariantConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString())));
	}
	
	@Test
	void encodeKeyVariantConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(NIL_UUID));
	}
	
	@Test
	void decodeKeyVariantConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(NIL_UUID.toString()));
	}
	
	@Test
	void encodeNilConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.nil();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), NIL_UUID));
	}
	
	@Test
	void decodeNilConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.nil();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString())));
	}
	
	@Test
	void encodeKeyNilConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.nil();
		
		assertDoesNotThrow(() -> codec.encodeKey(NIL_UUID));
	}
	
	@Test
	void decodeKeyNilConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.nil();
		
		assertDoesNotThrow(() -> codec.decodeKey(NIL_UUID.toString()));
	}
	
	@Test
	void encodeNilConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.nil();
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeNilConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.nil();
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString())));
	}
	
	@Test
	void encodeKeyNilConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.nil();
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(UUID_1));
	}
	
	@Test
	void decodeKeyNilConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.nil();
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeNotNilConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeNotNilConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString())));
	}
	
	@Test
	void encodeKeyNotNilConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		assertDoesNotThrow(() -> codec.encodeKey(UUID_1));
	}
	
	@Test
	void decodeKeyNotNilConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		assertDoesNotThrow(() -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeNotNilConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), NIL_UUID));
	}
	
	@Test
	void decodeNotNilConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString())));
	}
	
	@Test
	void encodeKeyNotNilConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(NIL_UUID));
	}
	
	@Test
	void decodeKeyNotNilConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.notNil();
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(NIL_UUID.toString()));
	}
	
	@Test
	void encodeMaxConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.max();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), MAX_UUID));
	}
	
	@Test
	void decodeMaxConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.max();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(MAX_UUID.toString())));
	}
	
	@Test
	void encodeKeyMaxConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.max();
		
		assertDoesNotThrow(() -> codec.encodeKey(MAX_UUID));
	}
	
	@Test
	void decodeKeyMaxConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.max();
		
		assertDoesNotThrow(() -> codec.decodeKey(MAX_UUID.toString()));
	}
	
	@Test
	void encodeMaxConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.max();
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeMaxConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.max();
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString())));
	}
	
	@Test
	void encodeKeyMaxConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.max();
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(UUID_1));
	}
	
	@Test
	void decodeKeyMaxConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.max();
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4).variant(builder -> builder.equalTo(UUIDVariant.RFC_4122)).notNil();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4).variant(builder -> builder.equalTo(UUIDVariant.RFC_4122)).notNil();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString())));
	}
	
	@Test
	void encodeKeyCombinedConstraintsSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(4).variant(builder -> builder.equalTo(UUIDVariant.RFC_4122)).notNil();
		
		assertDoesNotThrow(() -> codec.encodeKey(UUID_1));
	}
	
	@Test
	void decodeKeyCombinedConstraintsSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(4).variant(builder -> builder.equalTo(UUIDVariant.RFC_4122)).notNil();
		
		assertDoesNotThrow(() -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeCombinedConstraintsVersionViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(4).notNil();
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), UUID_2));
	}
	
	@Test
	void decodeCombinedConstraintsNilViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.betweenOrEqual(0, 5)).notNil();
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString())));
	}
	
	@Test
	void encodeKeyCombinedConstraintsVariantViolation() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.equalTo(UUIDVariant.RFC_4122));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(NIL_UUID));
	}
	
	@Test
	void decodeKeyCombinedConstraintsMultipleViolations() {
		Codec<UUID> codec = Codecs.UUID.version(4).variant(builder -> builder.equalTo(UUIDVariant.RFC_4122)).notNil();
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(NIL_UUID.toString()));
	}
	
	@Test
	void encodeVersionNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeVersionNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString())));
	}
	
	@Test
	void encodeKeyVersionNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		assertDoesNotThrow(() -> codec.encodeKey(UUID_1));
	}
	
	@Test
	void decodeKeyVersionNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		assertDoesNotThrow(() -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeVersionNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), UUID_2));
	}
	
	@Test
	void decodeVersionNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_2.toString())));
	}
	
	@Test
	void encodeKeyVersionNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(UUID_2));
	}
	
	@Test
	void decodeKeyVersionNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.version(builder -> builder.notEqualTo(1));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(UUID_2.toString()));
	}
	
	@Test
	void encodeVariantNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), UUID_1));
	}
	
	@Test
	void decodeVariantNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(UUID_1.toString())));
	}
	
	@Test
	void encodeKeyVariantNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		assertDoesNotThrow(() -> codec.encodeKey(UUID_1));
	}
	
	@Test
	void decodeKeyVariantNotEqualToConstraintSuccess() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		assertDoesNotThrow(() -> codec.decodeKey(UUID_1.toString()));
	}
	
	@Test
	void encodeVariantNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encode(typeProvider, typeProvider.empty(), NIL_UUID));
	}
	
	@Test
	void decodeVariantNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(NIL_UUID.toString())));
	}
	
	@Test
	void encodeKeyVariantNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		assertThrows(ConstraintViolateException.class, () -> codec.encodeKey(NIL_UUID));
	}
	
	@Test
	void decodeKeyVariantNotEqualToConstraintViolation() {
		Codec<UUID> codec = Codecs.UUID.variant(builder -> builder.notEqualTo(UUIDVariant.NFC));
		
		assertThrows(ConstraintViolateException.class, () -> codec.decodeKey(NIL_UUID.toString()));
	}
}

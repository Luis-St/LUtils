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

import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConfiguredCodec}.<br>
 *
 * @author Luis-St
 */
class ConfiguredCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new ConfiguredCodec<>(null, TestObjectOptionalInteger::age));
		assertThrows(NullPointerException.class, () -> new ConfiguredCodec<>(INTEGER.optional(), null));
		assertDoesNotThrow(() -> new ConfiguredCodec<>(INTEGER.optional(), TestObjectOptionalInteger::age));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<Optional<Integer>, TestObjectOptionalInteger> codec = new ConfiguredCodec<>(INTEGER.optional(), TestObjectOptionalInteger::age);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), null));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, null));
	}
	
	@Test
	void encodeStartWithNullObject() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<Optional<Integer>, TestObjectOptionalInteger> codec = new ConfiguredCodec<>(INTEGER.optional(), TestObjectOptionalInteger::age);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode component because the component can not be retrieved from a null object"));
	}
	
	@Test
	void encodeStartWithEmptyOptional() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<Optional<Integer>, TestObjectOptionalInteger> codec = new ConfiguredCodec<>(INTEGER.optional(), TestObjectOptionalInteger::age);
		JsonObject map = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, map, new TestObjectOptionalInteger(Optional.empty()));
		assertTrue(result.isSuccess());
		assertTrue(map.isEmpty());
		assertSame(map, result.orThrow());
	}
	
	@Test
	void encodeStartWithPresentOptional() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<Optional<Integer>, TestObjectOptionalInteger> codec = new ConfiguredCodec<>(INTEGER.optional(), TestObjectOptionalInteger::age);
		JsonObject map = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, map, new TestObjectOptionalInteger(Optional.of(42)));
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.orThrow());
	}
	
	@Test
	void encodeStartWithRequiredCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<String, TestObjectString> codec = new ConfiguredCodec<>(STRING, TestObjectString::name);
		JsonObject map = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, map, new TestObjectString("test"));
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("test"), result.orThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<Optional<Integer>, TestObjectOptionalInteger> codec = new ConfiguredCodec<>(INTEGER.optional(), TestObjectOptionalInteger::age);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.createMap().orThrow()));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<Optional<Integer>, TestObjectOptionalInteger> codec = new ConfiguredCodec<>(INTEGER.optional(), TestObjectOptionalInteger::age);
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithEmpty() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<Optional<Integer>, TestObjectOptionalInteger> codec = new ConfiguredCodec<>(INTEGER.optional(), TestObjectOptionalInteger::age);
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty());
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<Optional<Integer>, TestObjectOptionalInteger> codec = new ConfiguredCodec<>(INTEGER.optional(), TestObjectOptionalInteger::age);
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isPresent());
		assertEquals(42, result.orThrow().orElseThrow());
	}
	
	@Test
	void decodeStartWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<Integer, TestObjectInteger> codec = new ConfiguredCodec<>(INTEGER, TestObjectInteger::age);
		
		Result<Integer> result = codec.decodeStart(typeProvider, new JsonPrimitive("not-a-number"));
		assertTrue(result.isError());
	}
	
	@Test
	void equalsAndHashCode() {
		ConfiguredCodec<Integer, TestObjectInteger> codec1 = new ConfiguredCodec<>(INTEGER, TestObjectInteger::age);
		ConfiguredCodec<Integer, TestObjectInteger> codec2 = new ConfiguredCodec<>(INTEGER, TestObjectInteger::age);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		ConfiguredCodec<Integer, TestObjectInteger> codec = new ConfiguredCodec<>(INTEGER, TestObjectInteger::age);
		String result = codec.toString();
		
		assertTrue(result.startsWith("ConfigurableCodec["));
		assertTrue(result.endsWith("]"));
	}
	
	//region Internal
	private record TestObjectOptionalInteger(@NotNull Optional<Integer> age) {}
	
	private record TestObjectString(@NotNull String name) {}
	
	private record TestObjectInteger(int age) {}
	//endregion
}

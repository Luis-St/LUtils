/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConfigurableCodec}.<br>
 *
 * @author Luis-St
 */
class ConfigurableCodecTest {
	
	private static final CodecBuilder<TestObject> BUILDER = new CodecBuilder<>();
	
	@Test
	void encodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfigurableCodec<Optional<Integer>, TestObject> codec = new ConfigurableCodec<>(BUILDER, Codec.INTEGER.optional());
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), null));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, null));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Optional.of(42));
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonPrimitive.class, result.orThrow());
		assertEquals(new JsonPrimitive(42), result.orThrow().getAsJsonPrimitive());
		// no further testing of #encodeStart as it is a simple pass-through
	}
	
	@Test
	void encodeNamedStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfigurableCodec<Optional<Integer>, TestObject> codec = new ConfigurableCodec<>(BUILDER, Codec.INTEGER.optional());
		
		assertThrows(NullPointerException.class, () -> codec.encodeNamedStart(null, typeProvider.empty(), null));
		assertThrows(NullPointerException.class, () -> codec.encodeNamedStart(typeProvider, null, null));
		
		Result<JsonElement> missingNameResult = codec.encodeNamedStart(typeProvider, typeProvider.createMap().orThrow(), new TestObject(Optional.of(42)));
		assertTrue(missingNameResult.isError());
		codec.named("age");
		
		Result<JsonElement> missingGetterResult = codec.encodeNamedStart(typeProvider, typeProvider.createMap().orThrow(), new TestObject(Optional.of(42)));
		assertTrue(missingGetterResult.isError());
		codec.getter(TestObject::age);
		
		JsonObject map = new JsonObject();
		Result<JsonElement> emptyResult = codec.encodeNamedStart(typeProvider, map, new TestObject(Optional.empty()));
		assertTrue(emptyResult.isSuccess());
		assertTrue(map.isEmpty());
		assertSame(map, emptyResult.orThrow());
		
		Result<JsonElement> result = codec.encodeNamedStart(typeProvider, map, new TestObject(Optional.of(42)));
		assertTrue(result.isSuccess());
		assertNull(result.orThrow());
		assertTrue(map.containsKey("age"));
	}
	
	@Test
	void decodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfigurableCodec<Optional<Integer>, TestObject> codec = new ConfigurableCodec<>(BUILDER, Codec.INTEGER.optional());
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, null));
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals(42, result.orThrow().orElseThrow());
		// no further testing of #encodeStart as it is a simple pass-through
	}
	
	@Test
	void decodeNamedStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfigurableCodec<Optional<Integer>, TestObject> codec = new ConfigurableCodec<>(BUILDER, Codec.INTEGER.optional());
		
		assertThrows(NullPointerException.class, () -> codec.decodeNamedStart(null, typeProvider.createMap().orThrow()));
		assertThrows(NullPointerException.class, () -> codec.decodeNamedStart(typeProvider, null));
		
		JsonObject map = new JsonObject();
		Result<Optional<Integer>> missingNameResult = codec.decodeNamedStart(typeProvider, map);
		assertTrue(missingNameResult.isError());
		codec.named("age");
		
		Result<Optional<Integer>> emptyResult = codec.decodeNamedStart(typeProvider, map);
		assertTrue(emptyResult.isSuccess());
		assertTrue(emptyResult.orThrow().isEmpty());
		map.add("age", 42);
		
		Result<Optional<Integer>> result = codec.decodeNamedStart(typeProvider, map);
		assertTrue(result.isSuccess());
		assertEquals(42, result.orThrow().orElseThrow());
	}
	
	@Test
	void named() {
		assertThrows(NullPointerException.class, () -> new ConfigurableCodec<>(BUILDER, Codec.INTEGER).named(null));
		assertNotNull(new ConfigurableCodec<>(BUILDER, Codec.INTEGER).named("age"));
	}
	
	@Test
	void getter() {
		assertThrows(NullPointerException.class, () -> new ConfigurableCodec<>(BUILDER, Codec.INTEGER.optional()).getter(null));
		assertNotNull(new ConfigurableCodec<>(BUILDER, Codec.INTEGER.optional()).getter(TestObject::age));
	}
	
	//region Internal
	private record TestObject(@NotNull Optional<Integer> age) {}
	//endregion
}

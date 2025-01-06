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
 * Test class for {@link ConfiguredCodec}.<br>
 *
 * @author Luis-St
 */
class ConfiguredCodecTest {
	
	@Test
	void encodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<Optional<Integer>, TestObject> codec = new ConfiguredCodec<>(Codec.INTEGER.optional(), TestObject::age);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), null));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, null));
		assertTrue(codec.encodeStart(typeProvider, typeProvider.empty(), null).isError());
		
		JsonObject map = new JsonObject();
		Result<JsonElement> emptyResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, map, new TestObject(Optional.empty())));
		assertTrue(emptyResult.isSuccess());
		assertTrue(map.isEmpty());
		assertSame(map, emptyResult.orThrow());
		
		Result<JsonElement> result = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, map, new TestObject(Optional.of(42))));
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), assertInstanceOf(JsonPrimitive.class, result.orThrow()));
	}
	
	@Test
	void decodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ConfiguredCodec<Optional<Integer>, TestObject> codec = new ConfiguredCodec<>(Codec.INTEGER.optional(), TestObject::age);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.createMap().orThrow()));
		
		Result<Optional<Integer>> nullResult = codec.decodeStart(typeProvider, null);
		assertTrue(nullResult.isSuccess());
		assertTrue(nullResult.orThrow().isEmpty());
		
		Result<Optional<Integer>> emptyResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, typeProvider.empty()));
		assertTrue(emptyResult.isSuccess());
		assertTrue(emptyResult.orThrow().isEmpty());
		
		Result<Optional<Integer>> result = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(42)));
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isPresent());
		assertEquals(42, result.orThrow().orElseThrow());
	}
	
	//region Internal
	private record TestObject(@NotNull Optional<Integer> age) {}
	//endregion
}

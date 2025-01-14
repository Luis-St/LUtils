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
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NamedCodec}.<br>
 *
 * @author Luis-St
 */
class NamedCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new NamedCodec<>(null, "name"));
		assertThrows(NullPointerException.class, () -> new NamedCodec<>(Codec.INTEGER.optional(), null));
	}
	
	@Test
	void encodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(Codec.INTEGER.optional(), "name");
		JsonObject map = new JsonObject();
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, map, Optional.of(42)));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, Optional.of(42)));
		
		Result<JsonElement> nullResult = codec.encodeStart(typeProvider, map, null);
		assertTrue(nullResult.isError());
		
		Result<JsonElement> emptyResult = codec.encodeStart(typeProvider, map, Optional.empty());
		assertTrue(emptyResult.isSuccess());
		assertEquals(JsonNull.INSTANCE, assertInstanceOf(JsonNull.class, emptyResult.orThrow()));
		
		Result<JsonElement> noMapResult = codec.encodeStart(typeProvider, typeProvider.empty(), Optional.of(42));
		assertTrue(noMapResult.isError());
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, map, Optional.of(42));
		assertTrue(result.isSuccess());
		assertSame(map, assertInstanceOf(JsonObject.class, result.orThrow()));
	}
	
	@Test
	void decodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(Codec.INTEGER.optional(), "name");
		JsonObject map = new JsonObject();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, map));
		assertTrue(codec.decodeStart(typeProvider, null).isError());
		
		Result<Optional<Integer>> emptyResult = codec.decodeStart(typeProvider, map);
		assertTrue(emptyResult.isSuccess());
		assertTrue(emptyResult.orThrow().isEmpty());
		map.add("name", new JsonPrimitive(42));
		
		Result<Optional<Integer>> noMapResult = codec.decodeStart(typeProvider, typeProvider.empty());
		assertTrue(noMapResult.isError());
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, map);
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isPresent());
		assertEquals(42, result.orThrow().orElseThrow());
	}
}

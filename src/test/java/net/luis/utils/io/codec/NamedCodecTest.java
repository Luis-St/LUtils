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
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.luis.utils.io.codec.Codecs.*;
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
		assertThrows(NullPointerException.class, () -> new NamedCodec<>(INTEGER.optional(), null));
		assertThrows(NullPointerException.class, () -> new NamedCodec<>(INTEGER.optional(), "name", (String[]) null));
		assertDoesNotThrow(() -> new NamedCodec<>(INTEGER.optional(), "name"));
		assertDoesNotThrow(() -> new NamedCodec<>(INTEGER.optional(), "name", "alias"));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(INTEGER.optional(), "name");
		JsonObject map = new JsonObject();
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, map, Optional.of(42)));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, Optional.of(42)));
	}
	
	@Test
	void encodeStartWithNullValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(INTEGER.optional(), "name");
		JsonObject map = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, map, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode named 'name' null value"));
	}
	
	@Test
	void encodeStartWithEmptyOptional() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(INTEGER.optional(), "name");
		JsonObject map = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, map, Optional.empty());
		assertTrue(result.isSuccess());
		assertEquals(JsonNull.INSTANCE, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNonMapCurrent() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(INTEGER.optional(), "name");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Optional.of(42));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(INTEGER.optional(), "name");
		JsonObject map = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, map, Optional.of(42));
		assertTrue(result.isSuccess());
		assertSame(map, result.resultOrThrow());
		assertTrue(map.containsKey("name"));
		assertEquals(new JsonPrimitive(42), map.get("name"));
	}
	
	@Test
	void encodeStartWithRequiredValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Integer> codec = new NamedCodec<>(INTEGER, "number");
		JsonObject map = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, map, 123);
		assertTrue(result.isSuccess());
		assertSame(map, result.resultOrThrow());
		assertTrue(map.containsKey("number"));
		assertEquals(new JsonPrimitive(123), map.get("number"));
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(INTEGER.optional(), "name");
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonObject()));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(INTEGER.optional(), "name");
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode named 'name' null value"));
	}
	
	@Test
	void decodeStartWithEmptyMap() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(INTEGER.optional(), "name");
		JsonObject map = new JsonObject();
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, map);
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithNonMapValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(INTEGER.optional(), "name");
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty());
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Optional<Integer>> codec = new NamedCodec<>(INTEGER.optional(), "name");
		JsonObject map = new JsonObject();
		map.add("name", new JsonPrimitive(42));
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, map);
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isPresent());
		assertEquals(42, result.resultOrThrow().orElseThrow());
	}
	
	@Test
	void decodeStartWithRequiredValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Integer> codec = new NamedCodec<>(INTEGER, "number");
		JsonObject map = new JsonObject();
		map.add("number", new JsonPrimitive(123));
		
		Result<Integer> result = codec.decodeStart(typeProvider, map);
		assertTrue(result.isSuccess());
		assertEquals(123, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMissingRequiredValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Integer> codec = new NamedCodec<>(INTEGER, "number");
		JsonObject map = new JsonObject();
		
		Result<Integer> result = codec.decodeStart(typeProvider, map);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithSingleAlias() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Integer> codec = new NamedCodec<>(INTEGER, "name", "alias");
		JsonObject map = new JsonObject();
		map.add("alias", new JsonPrimitive(42));
		
		Result<Integer> result = codec.decodeStart(typeProvider, map);
		assertTrue(result.isSuccess());
		assertEquals(42, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMultipleAliases() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Integer> codec = new NamedCodec<>(INTEGER, "name", "alias1", "alias2");
		JsonObject map = new JsonObject();
		map.add("alias2", new JsonPrimitive(42));
		
		Result<Integer> result = codec.decodeStart(typeProvider, map);
		assertTrue(result.isSuccess());
		assertEquals(42, result.resultOrThrow());
	}
	
	@Test
	void decodeStartPrefersPrimaryNameOverAlias() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Integer> codec = new NamedCodec<>(INTEGER, "name", "alias");
		JsonObject map = new JsonObject();
		map.add("name", new JsonPrimitive(43));
		map.add("alias", new JsonPrimitive(42));
		
		Result<Integer> result = codec.decodeStart(typeProvider, map);
		assertTrue(result.isSuccess());
		assertEquals(43, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNoAliasesConfigured() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Integer> codec = new NamedCodec<>(INTEGER, "name");
		JsonObject map = new JsonObject();
		
		Result<Integer> result = codec.decodeStart(typeProvider, map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("no aliases configured"));
	}
	
	@Test
	void decodeStartWithNoMatchingAliases() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		NamedCodec<Integer> codec = new NamedCodec<>(INTEGER, "name", "alias1", "alias2");
		JsonObject map = new JsonObject();
		map.add("other", new JsonPrimitive(42));
		
		Result<Integer> result = codec.decodeStart(typeProvider, map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("not found"));
	}
	
	@Test
	void equalsAndHashCode() {
		NamedCodec<Integer> codec1 = new NamedCodec<>(INTEGER, "name");
		NamedCodec<Integer> codec2 = new NamedCodec<>(INTEGER, "name");
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringWithoutAliases() {
		NamedCodec<Integer> codec = new NamedCodec<>(INTEGER, "name");
		String result = codec.toString();
		
		assertTrue(result.contains("'name'"));
		assertFalse(result.contains("alias"));
	}
	
	@Test
	void toStringWithAliases() {
		NamedCodec<Integer> codec = new NamedCodec<>(INTEGER, "name", "alias");
		String result = codec.toString();
		
		assertTrue(result.contains("'name'"));
		assertTrue(result.contains("alias"));
	}
}
